import dev.kikugie.stonecutter.settings.StonecutterSettings

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven {
            setUrl("https://maven.aliyun.com/repository/google")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/public")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/gradle-plugin")
        }
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://repo.spongepowered.org/maven")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://repo.huaweicloud.com/repository/maven/")
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        maven("https://mirrors.163.com/maven/repository/maven-public/")
        maven("https://maven.parchmentmc.org")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.architectury.dev/")
        maven("https://modmaven.dev/")
        maven("https://mirrors.imucraft.cn")
        maven("https://mirrors.imucraft.cn/neoforge/")
        maven("https://mirrors.qlu.edu.cn/bmclapi/")
    }
    plugins {
        kotlin("jvm") version "1.9.24"
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.6-beta.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
extensions.configure<StonecutterSettings> {
    kotlinController = true
    centralScript = "build.gradle.kts"

    // 此处列出的版本，无论是注释掉还是其他方式，都有预制的 gradle.properties。
    shared {
        /*vers("1.16.5-fabric","1.16.5")
        vers("1.16.5-forge","1.16.5")
        vers("1.18.2-fabric","1.18.2")
        vers("1.18.2-forge","1.18.2")
        vers("1.19.2-fabric","1.19.2")
        vers("1.19.2-forge","1.19.2")
        vers("1.19.4-fabric","1.19.4")
        vers("1.19.4-forge","1.19.4")*/
        vers("1.20.1-fabric","1.20.1")
        vers("1.20.1-forge","1.20.1")
        vers("1.20.4-fabric","1.20.4")
        vers("1.20.4-forge","1.20.4")
        vers("1.20.4-neoforge","1.20.4")
        vers("1.20.6-fabric","1.20.6")
        vers("1.20.6-neoforge","1.20.6")
        vers("1.21-fabric","1.21")
        vers("1.21-neoforge","1.21")
        vers("1.21.1-fabric","1.21.1")
        vers("1.21.1-neoforge","1.21.1")
        /*vers("1.21.2+3-fabric","1.21.2")
        vers("1.21.2+3-neoforge","1.21.2")
        vers("1.21.4-fabric","1.21.4")
        vers("1.21.4-neoforge","1.21.4")
        vers("1.21.5-fabric","1.21.5")
        vers("1.21.5-neoforge","1.21.5")*/
        vcsVersion="1.20.1-forge"
    }
    create(rootProject)
}

rootProject.name = "brick_lib_api"