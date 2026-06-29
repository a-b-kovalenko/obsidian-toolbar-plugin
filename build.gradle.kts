plugins {
    id("org.jetbrains.intellij.platform") version "2.16.0"
    kotlin("jvm") version "2.2.0"
}

group = "com.akovalenko"
version = "1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaUltimate("2026.1")
    }
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    }
}

intellijPlatform {
    signing {
        val keyFile = file("private.pem")
        val certFile = file("chain.crt")
        if (keyFile.exists() && certFile.exists()) {
            privateKeyFile.set(keyFile)
            certificateChainFile.set(certFile)
        } else {
            privateKey.set(providers.environmentVariable("SIGNING_KEY"))
            certificateChain.set(providers.environmentVariable("SIGNING_CERT"))
        }
        password.set(providers.environmentVariable("SIGNING_KEY_PASSPHRASE"))
    }
    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }
}

tasks {
    buildSearchableOptions { enabled = false }
    withType<JavaCompile> {
        options.release.set(21)
    }
    verifyPluginSignature {
        dependsOn(signPlugin)
    }
}
