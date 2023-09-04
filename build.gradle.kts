import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    kotlin("multiplatform") version "1.9.0"
    kotlin("plugin.allopen") version "1.9.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.9"
    id("maven-publish")
}

group = "io.smallibs"
version = "0.1.0"

rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin::class.java) {
    rootProject.the<YarnRootExtension>().yarnLockMismatchReport = YarnLockMismatchReport.WARNING // NONE | FAIL
    rootProject.the<YarnRootExtension>().reportNewYarnLock = false // true
    rootProject.the<YarnRootExtension>().yarnLockAutoReplace = false // true
}

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(IR) {
        browser()
        nodejs()
        binaries.executable()
    }

    if (HostManager.host == KonanTarget.MACOS_X64) macosX64("native")
    if (HostManager.host == KonanTarget.MACOS_ARM64) macosArm64("native")
    if (HostManager.hostIsLinux) linuxX64("native")
    if (HostManager.hostIsMingw) mingwX64("native")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.9")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.quicktheories:quicktheories:0.26")
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.7.3")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-macosarm64:1.7.3")
            }
        }
        val nativeTest by getting
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    configurations {
        named("main") {
            iterations = 5 // number of iterations
            iterationTime = 300
            iterationTimeUnit = "ns"
            warmups = 20
            advanced("jvmForks", 3)
            advanced("jsUseBridge", true)
        }
    }
    targets {
        // register("jsTest")
        register("jvmTest")
        register("nativeTest")
    }
}