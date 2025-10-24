plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("dev.kikugie.j52j") version "1.0" apply false // Enables asset processing by writing json5 files
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("me.modmuss50.mod-publish-plugin") version "1.0.+" apply false
    id("net.darkhax.curseforgegradle") version "1.1.26" apply false
}

stonecutter active "1.20.1-forge" /* You may have to edit this. Make sure it matches one of the versions present in settings.gradle.kts */
