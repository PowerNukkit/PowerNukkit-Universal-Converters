plugins {
    kotlin("jvm") version "1.5.21"
    id("org.jetbrains.dokka") version "1.5.0"
    application
    jacoco
}

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

val kotlinVersion = "1.5.21"
val kotlinCoroutinesVersion = "1.5.1"
val jacksonVersion = "2.12.4"
val log4j2Version = "2.14.1"
val junitVersion = "5.7.2"

dependencies {
    implementation(kotlin("stdlib", kotlinVersion))
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation(kotlin("reflect", kotlinVersion))
    implementation(kotlin("reflect", kotlinVersion))
    implementation(kotlinx("coroutines-core", kotlinCoroutinesVersion))
    implementation(kotlinx("coroutines-swing", kotlinCoroutinesVersion))
    implementation(kotlinx("cli-jvm", "0.3.2"))
    implementation("io.ktor", "ktor-utils-jvm", "1.6.2")
    implementation("br.com.gamemods", "region-manipulator", "2.0.0")
    implementation("br.com.gamemods", "nbt-manipulator", "3.0.0")
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", jacksonVersion)
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", jacksonVersion)
    implementation("com.michael-bull.kotlin-inline-logger", "kotlin-inline-logger-jvm", "1.0.3")
    implementation("org.powernukkit", "version-library", "1.0.0")
    implementation("org.powernukkit.bedrock.leveldb", "bedrock-leveldb", "0.11.0-PN")
    implementation("io.gomint", "leveldb-jni", "1.3.0-SNAPSHOT")
    implementation("org.slf4j", "slf4j-api", "1.7.32")
    runtimeOnly(log4j("slf4j-impl"))
    runtimeOnly(log4j("api"))
    runtimeOnly(log4j("core"))

    testImplementation(kotlin("test-junit5", kotlinVersion))
    testImplementation("io.mockk", "mockk", "1.12.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", junitVersion)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junitVersion)
}

application {
    mainClass.set("org.powernukkit.converters.WorldConverterCLI")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xjvm-default=all",
            "-Xopt-in=kotlin.RequiresOptIn",
            //"-Xexplicit-api=strict",
        )
    }
}

tasks {
    jar {
        exclude("*.xcf")
    }

    test {
        useJUnitPlatform()
    }
}


//<editor-fold desc="DSL" defaultstate="collapsed">
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


@Suppress("unused")
fun DependencyHandler.kotlinx(module: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$module:$version"

@Suppress("unused")
fun DependencyHandler.log4j(module: String, version: String = log4j2Version) = "org.apache.logging.log4j:log4j-$module:$version"
//</editor-fold>
