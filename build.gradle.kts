import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.resolver.buildSrcSourceRootsFilePath
import java.util.Optional
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.arrayListOf
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.mapOf
import kotlin.collections.withIndex
import kotlin.text.indexOf
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty
import kotlin.text.lastIndexOf
import kotlin.text.lowercase
import kotlin.text.split
import kotlin.text.startsWith
import kotlin.text.substring

// TODO 确认此处添加的插件
plugins {
    `maven-publish`
    //kotlin("jvm") version "1.9.22"
    //id("fabric-loom") // Leaving this here if you want to swap loom.
    id("dev.architectury.loom")
    //id("dev.kikugie.j52j") // Recommended by kiku if using swaps in json5.
    id("me.modmuss50.mod-publish-plugin")
}

// 依赖项仓库，在下面的会被优先解析
repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.huaweicloud.com/repository/maven/")
    maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    maven("https://mirrors.163.com/maven/repository/maven-public/")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    maven("https://maven.parchmentmc.org")
    maven("https://modmaven.dev/")
    maven("https://mirrors.imucraft.cn")
    maven("https://mirror.nyist.edu.cn")
    maven("https://mirrors.qlu.edu.cn/")
    exclusiveContent {
        forRepository { maven("https://www.cursemaven.com") { name = "CurseForge" } }
        filter { includeGroup("curse.maven") }
    }
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
    maven("https://maven.neoforged.net/releases/")
    maven ("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev/")
    maven("https://maven.minecraftforge.net/")
}

fun bool(str: String) : Boolean {
    return str.lowercase().startsWith("t")
}

fun boolProperty(key: String) : Boolean {
    if(!hasProperty(key)){
        return false
    }
    return bool(property(key).toString())
}

fun listProperty(key: String) : ArrayList<String> {
    if(!hasProperty(key)){
        return arrayListOf()
    }
    val str = property(key).toString()
    if(str == "UNSET"){
        return arrayListOf()
    }
    return ArrayList(str.split(" "))
}

fun optionalStrProperty(key: String) : Optional<String> {
    if(!hasProperty(key)){
        return Optional.empty()
    }
    val str = property(key).toString()
    if(str =="UNSET"){
        return Optional.empty()
    }
    return Optional.of(str)
}

class VersionRange(val min: String, val max: String){
    fun asForgelike() : String{
        return "${if(min.isEmpty()) "(" else "["}${min},${max}${if(max.isEmpty()) ")" else "]"}"
    }
    fun asFabric() : String{
        var out = ""
        if(min.isNotEmpty()){
            out += ">=$min"
        }
        if(max.isNotEmpty()){
            if(out.isNotEmpty()){
                out += " "
            }
            out += "<=$max"
        }
        return out
    }
}

/**
 * Creates a VersionRange from a listProperty
 */
fun versionProperty(key: String) : VersionRange {
    if(!hasProperty(key)){
        return VersionRange("","")
    }
    val list = listProperty(key)
    for (i in 0 until list.size) {
        if(list[i] == "UNSET"){
            list[i] = ""
        }
    }
    return if(list.isEmpty()){
        VersionRange("","")
    }
    else if(list.size == 1) {
        VersionRange(list[0],"")
    }
    else{
        VersionRange(list[0], list[1])
    }
}

/**
 * Creates a VersionRange unless the value is UNSET
 */
fun optionalVersionProperty(key: String) : Optional<VersionRange>{
    val str = optionalStrProperty(key)
    if(!hasProperty(key)){
        return Optional.empty()
    }
    if(!str.isPresent){
        return Optional.empty()
    }
    return Optional.of(versionProperty(key))
}

enum class EnvType {
    FABRIC,
    FORGE,
    NEOFORGE
}

/**
 * Stores core dependency and environment information.
 */
class Env {
    val parchmentVersions = mapOf(
        "1.21.8" to "2025.07.20",
        "1.21.7" to "2025.07.18",
        "1.21.6" to "2025.06.29",
        "1.21.5" to "2025.06.15",
        "1.21.4" to "2025.03.23",
        "1.21.3" to "2024.12.07",
        "1.21.1" to "2024.11.17",
        "1.21" to "2024.11.10",
        "1.20.6" to "2024.06.16",
        "1.20.4" to "2024.04.14",
        "1.20.3" to "2023.12.31",
        "1.20.2" to "2023.12.10",
        "1.20.1" to "2023.09.03",
        "1.19.4" to "2023.06.26",
        "1.19.3" to "2023.06.25",
        "1.19.2" to "2022.11.27",
        "1.18.2" to "2022.11.06",
        "1.17.1" to "2021.12.12",
        "1.16.5" to "2022.03.06"
    );
    val archivesBaseName = property("archives_base_name").toString()

    val mcVersion = versionProperty("deps.core.mc.version_range")

    val loader = property("loom.platform").toString()
    val isFabric = loader == "fabric"
    val isForge = loader == "forge"
    val isNeo = loader == "neoforge"
    val isCommon = project.parent!!.name == "common"
    val isApi = project.parent!!.name == "core"
    val type = if(isFabric) EnvType.FABRIC else if(isForge) EnvType.FORGE else EnvType.NEOFORGE

    // TODO: 如果 MC 在未来的更新中需要更高的 JVM，请更改此控制器。
    val javaVer = if(atMost("1.16.5")) 8 else if(atMost("1.20.4")) 17 else 21

    val fabricLoaderVersion = versionProperty("deps.core.fabric.loader.version_range")
    val forgeMavenVersion = versionProperty("deps.core.forge.version_range")
    val forgeVersion = VersionRange(extractForgeVer(forgeMavenVersion.min),extractForgeVer(forgeMavenVersion.max))
    // FML language version is usually the first two numbers only.
    private val fgl: String = if(isForge) forgeMavenVersion.min.substring(forgeMavenVersion.min.lastIndexOf("-")) else ""
    val forgeLanguageVersion = VersionRange(if(isForge) fgl.substring(0,fgl.indexOf(".")) else "","")
    val neoforgeVersion = versionProperty("deps.core.neoforge.version_range")
    // The modloader system is separate from the API in Neo
    val neoforgeLoaderVersion = versionProperty("deps.core.neoforge.loader.version_range")

    fun atLeast(version: String) = stonecutter.compare(mcVersion.min, version) >= 0
    fun atMost(version: String) = stonecutter.compare(mcVersion.min, version) <= 0
    fun isNot(version: String) = stonecutter.compare(mcVersion.min, version) != 0
    fun isExact(version: String) = stonecutter.compare(mcVersion.min, version) == 0

    private fun extractForgeVer(str: String) : String {
        val split = str.split("-")
        if(split.size == 1){
            return split[0]
        }
        if(split.size > 1){
            return split[1]
        }
        return ""
    }
}
val env = Env()

enum class DepType {
    API,
    // Optional API
    API_OPTIONAL{
        override fun isOptional(): Boolean {
            return true
        }
    },
    // Implementation
    IMPL,
    // Forge Runtime Library
    FRL{
        override fun includeInDepsList(): Boolean {
            return false
        }
    },
    // Implementation and Included in output jar.
    INCLUDE{
        override fun includeInDepsList(): Boolean {
            return false
        }
    };
    open fun isOptional() : Boolean {
        return false
    }
    open fun includeInDepsList() : Boolean{
        return true
    }
}

class APIModInfo(val modid: String?, val curseSlug: String?, val rinthSlug: String?){
    constructor () : this(null,null,null)
    constructor (modid: String) : this(modid,modid,modid)
    constructor (modid: String, slug: String) : this(modid,slug,slug)
}

/**
 * APIs must have a maven source.
 * If the version range is not present then the API will not be used.
 * If modid is null then the API will not be declared as a dependency in uploads.
 * The enable condition determines whether the API will be used for this version.
 */
class APISource(val type: DepType, val modInfo: APIModInfo, val mavenLocation: String, val versionRange: Optional<VersionRange>, private val enableCondition: Predicate<APISource>) {
    val enabled = this.enableCondition.test(this)
}

/**
 * APIs with hardcoded support for convenience. These are optional.
 */
//TODO 在此处添加任何硬编码的 API。硬编码 API 应在大多数（如果不是全部）版本中使用。
val apis = arrayListOf(
    APISource(DepType.API, APIModInfo(if(env.atMost("1.16.5")) {
        "fabric"
    } else {
        "fabric-api"
    },"fabric-api"), "net.fabricmc.fabric-api:fabric-api",optionalVersionProperty("deps.api.fabric")) { src ->
        src.versionRange.isPresent && env.isFabric
    },
)

// Stores information about the mod itself.
class ModProperties {
    val id = property("mod.id").toString()
    val displayName = property("mod.display_name").toString()
    val version = property("version").toString()
    val description = optionalStrProperty("mod.description").orElse("")
    val authors = property("mod.authors").toString()
    val icon = property("mod.icon").toString()
    val issueTracker = optionalStrProperty("mod.issue_tracker").orElse("")
    val license = optionalStrProperty("mod.license").orElse("")
    val sourceUrl = optionalStrProperty("mod.source_url").orElse("")
    val generalWebsite = optionalStrProperty("mod.general_website").orElse(sourceUrl)
}

/**
 * Stores information specifically for fabric.
 * Fabric requires that the mod's render and common main() entry points be included in the fabric.mod.json file.
 */
class ModFabric {
    val commonEntry = "${group}.${env.archivesBaseName}.entrypoints.${property("mod.fabric.entry.common").toString()}"
    val clientEntry = "${group}.${env.archivesBaseName}.entrypoints.${property("mod.fabric.entry.client").toString()}"
}

/**
 * Provides access to the mixins for specific environments.
 * All environments are provided the vanilla mixin if it is enabled.
 */
class ModMixins {
    val enableVanillaMixin = boolProperty("mixins.vanilla.enable")
    val enableFabricMixin = boolProperty("mixins.fabric.enable")
    val enableForgeMixin = boolProperty("mixins.forge.enable")
    val enableNeoforgeMixin = boolProperty("mixins.neoforge.enable")

    val vanillaMixin = "mixins.${mod.id}.json"
    val fabricMixin = "mixins.fabric.${mod.id}.json"
    val forgeMixin = "mixins.forge.${mod.id}.json"
    val neoForgeMixin = "mixins.neoforge.${mod.id}.json"
    val extraMixins = listProperty("mixins.extras")

    /**
     * Modify this method if you need better control over the mixin list.
     */
    fun getMixins(env: EnvType) : List<String> {
        val out = arrayListOf<String>()
        if(enableVanillaMixin) out.add(vanillaMixin)
        when (env) {
            EnvType.FABRIC -> if(enableFabricMixin) out.add(fabricMixin)
            EnvType.FORGE -> if(enableForgeMixin) out.add(forgeMixin)
            EnvType.NEOFORGE -> if(enableNeoforgeMixin) out.add(neoForgeMixin)
        }
        return out
    }
}

class ModAWs {
    val vanillaAW = "src/main/resources/${mod.id}.accesswidener"

    fun getAWs() : String {
        return vanillaAW
    }
}

//TODO 如果您打算自动发布，请确认此控制器和相关 API 令牌（强烈推荐）
//TODO 版本数量过多Modrinth可能会限制您的速率。如果是这种情况，您应该给他们发电子邮件寻求帮助。
/**
 * 控制发布的工作。要使发布正常工作，dryRunMode 必须为 false。
 * Modrinth 和 Curseforge 项目令牌是公开访问的，因此将它们包含在文件中是安全的。
 * 不要在项目中包含您的 API 密钥！
 *
 * Modrinth API 令牌应存储在 MODRINTH_TOKEN 环境变量中。
 * curseforge API 令牌应存储在 CURSEFORGE_TOKEN 环境变量中。
 */
class ModPublish {
    val mcTargets = arrayListOf<String>()
    val modrinthProjectToken = property("publish.token.modrinth").toString()
    val curseforgeProjectToken = property("publish.token.curseforge").toString()
    val mavenURL = optionalStrProperty("publish.maven.url")
    val dryRunMode = boolProperty("publish.dry_run")

    init {
        val tempmcTargets = listProperty("publish_acceptable_mc_versions")
        if(tempmcTargets.isEmpty()){
            mcTargets.add(env.mcVersion.min)
        }
        else{
            mcTargets.addAll(tempmcTargets)
        }
    }
}
val modPublish = ModPublish()

/**
 * These dependencies will be added to the fabric.mods.json, META-INF/neoforge.mods.toml, and META-INF/mods.toml file.
 */
class ModDependencies {
    val loadBefore = listProperty("deps.before")
    fun forEachAfter(cons: BiConsumer<String,VersionRange>){
        forEachRequired(cons)
        forEachOptional(cons)
    }

    fun forEachBefore(cons: Consumer<String>){
        loadBefore.forEach(cons)
    }

    fun forEachOptional(cons: BiConsumer<String,VersionRange>){
        apis.forEach{src->
            if(src.enabled && src.type.isOptional() && src.type.includeInDepsList()) src.versionRange.ifPresent { ver -> src.modInfo.modid?.let {
                cons.accept(it, ver)
            }}
        }
    }

    fun forEachRequired(cons: BiConsumer<String,VersionRange>){
        cons.accept("minecraft",env.mcVersion)
        if(env.isForge) {
            cons.accept("forge", env.forgeVersion)
        }
        if (env.isNeo){
            cons.accept("neoforge", env.neoforgeVersion)
        }
        if(env.isFabric) {
            cons.accept("fabric", env.fabricLoaderVersion)
        }
        apis.forEach{src->
            if(src.enabled && !src.type.isOptional() && src.type.includeInDepsList()) src.versionRange.ifPresent { ver -> src.modInfo.modid?.let {
                cons.accept(it, ver)
            }}
        }
    }
}
val dependencies = ModDependencies()

/**
 * These values will change between versions and mod loaders. Handles generation of specific entries in mods.toml and neoforge.mods.toml
 */
class SpecialMultiversionedConstants {
    private val mandatoryIndicator = if(env.isNeo) "required" else "mandatory"
    val mixinField = if(env.atLeast("1.20.4") && env.isNeo) neoForgeMixinField() else if(env.isFabric) fabricMixinField() else ""
    val awField = if(env.isFabric) "  \"accessWidener\" : \"brick_lib_api.accesswidener\"," else ""

    val forgelikeLoaderVer =  if(env.isForge) env.forgeLanguageVersion.asForgelike() else env.neoforgeLoaderVersion.asForgelike()
    val forgelikeAPIVer = if(env.isForge) env.forgeVersion.asForgelike() else env.neoforgeVersion.asForgelike()
    val dependenciesField = if(env.isFabric) fabricDependencyList() else forgelikeDependencyField()
    val excludes = excludes0()
    private fun excludes0() : List<String> {
        val out = arrayListOf<String>()
        if(!env.isForge) {
            // NeoForge before 1.21 still uses the forge mods.toml :/ One of those goofy changes between versions.
            if(!env.isNeo || !env.atMost("1.20.6")) {
                out.add("META-INF/mods.toml")
            }
        }
        if(!env.isFabric){
            out.add("fabric.mod.json")
        }
        if(!env.isNeo){
            out.add("META-INF/neoforge.mods.toml")
        }
        return out
    }
    private fun neoForgeMixinField () : String {
        var out = ""
        for (mixin in modMixins.getMixins(EnvType.NEOFORGE)) {
            out += "[[mixins]]\nconfig=\"${mixin}\"\n"
        }
        return out
    }
    private fun fabricMixinField () : String {
        val list = modMixins.getMixins(EnvType.FABRIC)
        if(list.isEmpty()){
            return ""
        }
        else{
            var out = "  \"mixins\" : [\n"
            for ((index, mixin) in list.withIndex()) {
                out += "    \"${mixin}\""
                if(index < list.size-1){
                    out+=","
                }
                out+="\n"
            }
            return "$out  ],"
        }
    }
    private fun fabricDependencyList() : String{
        var out = "  \"depends\":{"
        var useComma = false
        dependencies.forEachRequired{modid,ver->
            if(useComma){
                out+=","
            }
            out+="\n"
            out+="    \"${modid}\": \"${ver.asFabric()}\""
            useComma = true
        }
        return "$out\n  }"

    }
    private fun forgelikeDependencyField() : String {
        var out = ""
        dependencies.forEachBefore{modid ->
            out += forgedep(modid,VersionRange("",""),"BEFORE",false)
        }
        dependencies.forEachOptional{modid,ver->
            out += forgedep(modid,ver,"AFTER",false)
        }
        dependencies.forEachRequired{modid,ver->
            out += forgedep(modid,ver,"AFTER",true)
        }
        return out
    }
    private fun forgedep(modid: String, versionRange: VersionRange, order: String, mandatory: Boolean) : String {
        return "[[dependencies.${mod.id}]]\n" +
                "modId=\"${modid}\"\n" +
                "${mandatoryIndicator}=${mandatory}\n" +
                "versionRange=\"${versionRange.asForgelike()}\"\n" +
                "ordering=\"${order}\"\n" +
                "side=\"BOTH\"\n"
    }
}
val mod = ModProperties()
val modFabric = ModFabric()
val modMixins = ModMixins()
val modAWs = ModAWs()
val dynamics = SpecialMultiversionedConstants()

//TODO: 如果您希望上传版本格式不同，请更改此格式（强烈建议使用这种格式）
version = "${mod.version}+${env.mcVersion.min}+${env.loader}"
group = property("group").toString()

// Adds both optional and required dependencies to stonecutter version checking.
dependencies.forEachAfter{mid, ver ->
    stonecutter.dependency(mid,ver.min)
}
apis.forEach{ src ->
    src.modInfo.modid?.let {
        stonecutter.const(it,src.enabled)
        src.versionRange.ifPresent{ ver ->
            stonecutter.dependency(it,ver.min)
        }
    }
}

//TODO: 在此处添加更多 stonecutter 常量。
stonecutter.const("fabric",env.isFabric)
stonecutter.const("forge",env.isForge)
stonecutter.const("neoforge",env.isNeo)
stonecutter.const("newnf",env.isNeo && env.atLeast("1.20.4"))
stonecutter.const("oldnf",env.isNeo && env.atMost("1.20.3"))



loom {
    accessWidenerPath = file("../../"+modAWs.getAWs())
    silentMojangMappingsLicense()
    if (env.isForge) forge {
        for (mixin in modMixins.getMixins(EnvType.FORGE)) {
            mixinConfigs(
                mixin
            )
        }
    }

    decompilers {
        get("vineflower").apply {
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "../../run"
    }
}
base { archivesName.set(env.archivesBaseName) }

dependencies {
    minecraft("com.mojang:minecraft:${env.mcVersion.min}")

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    mappings(loom.layered() {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${env.mcVersion.min}:${env.parchmentVersions[env.mcVersion.min]}@zip")
    })

    if(env.isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${env.fabricLoaderVersion.min}")
        implementation("com.electronwill.night-config:toml:3.6.7")
        implementation("com.electronwill.night-config:core:3.6.7")
    }
    if(env.isForge){
        "forge"("net.minecraftforge:forge:${env.forgeMavenVersion.min}")
        if (env.mcVersion.min>="1.18.2"){
            annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.0")?.let { compileOnly(it) }
            include("io.github.llamalad7:mixinextras-forge:0.5.0")?.let { implementation(it) }
        }
    }
    if(env.isNeo){
        "neoForge"("net.neoforged:neoforge:${env.neoforgeVersion.min}")
    }

    apis.forEach { src->
        if(src.enabled) {
            src.versionRange.ifPresent { ver ->
                if(src.type == DepType.API || src.type == DepType.API_OPTIONAL) {
                    modApi("${src.mavenLocation}:${ver.min}")
                }
                if(src.type == DepType.IMPL) {
                    modImplementation("${src.mavenLocation}:${ver.min}")
                }
                if(src.type == DepType.FRL && env.isForge){
                    "forgeRuntimeLibrary"("${src.mavenLocation}:${ver.min}")
                }
                if(src.type == DepType.INCLUDE) {
                    modImplementation("${src.mavenLocation}:${ver.min}")
                    include("${src.mavenLocation}:${ver.min}")
                }
            }
        }
    }

    vineflowerDecompilerClasspath("org.vineflower:vineflower:1.10.1")
}

java {
    withSourcesJar()
    //TODO 这是需要更新的 Java。
    val java = if(env.javaVer == 8) JavaVersion.VERSION_1_8 else if(env.javaVer == 17) JavaVersion.VERSION_17 else JavaVersion.VERSION_21
    targetCompatibility = java
    sourceCompatibility = java
}

/**
 * 替换普通的复制任务并对文件进行后处理。
 * 由于 1.20.4 之后的去复数化，有效地重命名了数据包目录。
 * TODO：确认您不应在错误的版本使用复数名字的数据包类型（mojang让你学会了多个单词的复数形式，快谢谢mojang(）。
 */
abstract class ProcessResourcesExtension : ProcessResources() {
    @get:Input
    val autoPluralize = arrayListOf(
        "/data/minecraft/tags/block",
        "/data/minecraft/tags/item",
        "/data/brick_lib/loot_table",
        "/data/brick_lib/recipe",
        "/data/brick_lib/tags/item",
    )
    override fun copy() {
        super.copy()
        val root = destinationDir.absolutePath
        autoPluralize.forEach { path ->
            val file = File(root.plus(path))
            if(file.exists()){
                file.copyRecursively(File(file.absolutePath.plus("s")),true)
                file.deleteRecursively()
            }
        }
    }
}

if(env.atMost("1.20.6")){
    tasks.replace("processResources",ProcessResourcesExtension::class)
}

tasks.processResources {
    val map = mapOf<String,String>(
        "modid" to mod.id,
        "id" to mod.id,
        "name" to mod.displayName,
        "display_name" to mod.displayName,
        "version" to mod.version,
        "description" to mod.description,
        "authors" to mod.authors,
        "github_url" to mod.sourceUrl,
        "source_url" to mod.sourceUrl,
        "website" to mod.generalWebsite,
        "icon" to mod.icon,
        "fabric_common_entry" to modFabric.commonEntry,
        "fabric_client_entry" to modFabric.clientEntry,
        "mc_min" to env.mcVersion.min,
        "mc_max" to env.mcVersion.max,
        "issue_tracker" to mod.issueTracker,
        "java_ver" to env.javaVer.toString(),
        "forgelike_loader_ver" to dynamics.forgelikeLoaderVer,
        "forgelike_api_ver" to dynamics.forgelikeAPIVer,
        "loader_id" to env.loader,
        "license" to mod.license,
        "mixin_field" to dynamics.mixinField,
        "aw_field" to dynamics.awField,
        "dependencies_field" to dynamics.dependenciesField
    )
    map.forEach{ (key, value) ->
        inputs.property(key,value)
    }
    dynamics.excludes.forEach{file->
        exclude(file)
    }
    filesMatching("pack.mcmeta") { expand(map) }
    filesMatching("fabric.mod.json") { expand(map) }
    filesMatching("META-INF/mods.toml") { expand(map) }
    filesMatching("META-INF/neoforge.mods.toml") { expand(map) }
    for (str in modMixins.getMixins(env.type)) {
        filesMatching(str) { expand(map) }
    }
    filesMatching(modAWs.vanillaAW) { expand(map) }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

//TODO: 启用自动发布。
/*publishMods {
    file = tasks.remapJar.get().archiveFile
    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
    displayName = "${mod.displayName} ${mod.version} for ${env.mcVersion.min}"
    version = mod.version
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add(env.loader)

    dryRun = modPublish.dryRunMode

    modrinth {
        projectId = modPublish.modrinthProjectToken
        // Get one here: https://modrinth.com/settings/pats, enable read, write, and create Versions ONLY!
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(modPublish.mcTargets)
        apis.forEach{ src ->
            if(src.enabled) src.versionRange.ifPresent{ ver ->
                if(src.type.isOptional()){
                    src.modInfo.rinthSlug?.let {
                        optional {
                            slug = it
                            version = ver.min

                        }
                    }
                }
                else{
                    src.modInfo.rinthSlug?.let {
                        requires {
                            slug = it
                            version = ver.min
                        }
                    }
                }
            }
        }
    }

    curseforge {
        projectId = modPublish.curseforgeProjectToken
        // Get one here: https://legacy.curseforge.com/account/api-tokens
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.addAll(modPublish.mcTargets)
        apis.forEach{ src ->
            if(src.enabled) src.versionRange.ifPresent{ ver ->
                if(src.type.isOptional()){
                    src.modInfo.curseSlug?.let {
                        optional {
                            slug = it
                            version = ver.min

                        }
                    }
                }
                else{
                    src.modInfo.curseSlug?.let {
                        requires {
                            slug = it
                            version = ver.min
                        }
                    }
                }
            }
        }
    }
}
// TODO 如果不上传到 Maven，则禁用
publishing {
    repositories {
        // TODO 我建议您怎么做的示例
        if(modPublish.mavenURL.isPresent) {
            maven {
                url = uri(modPublish.mavenURL.get())
                credentials {
                    username = System.getenv("MVN_NAME")
                    password = System.getenv("MVN_KEY")
                }
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava"){
            groupId = project.group.toString()
            artifactId = env.archivesBaseName
            version = project.version.toString()
            from(components["java"])
        }
    }
}*/