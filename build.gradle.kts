/*
 *  PowerNukkit Universal Worlds & Converters for Minecraft
 *  Copyright (C) 2021  José Roberto de Araújo Júnior
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    kotlin("jvm") version "1.5.21"
    id("org.jetbrains.dokka") version "1.5.0"
    application
    jacoco
}

val kotlinVersion = "1.5.21"
val kotlinCoroutinesVersion = "1.5.1"
val log4j2Version = "2.14.1"
val junitVersion = "5.7.2"

allprojects {
    repositories {
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }

    afterEvaluate {
        dependencies {
            implementation(kotlin("stdlib", kotlinVersion))
            implementation(kotlin("stdlib-jdk8", kotlinVersion))
            implementation(kotlin("reflect", kotlinVersion))
            implementation(kotlinx("coroutines-core", kotlinCoroutinesVersion))

            implementation("com.michael-bull.kotlin-inline-logger", "kotlin-inline-logger-jvm", "1.0.3")
            implementation("org.slf4j", "slf4j-api", "1.7.32")

            testImplementation(kotlin("test-junit5", kotlinVersion))
            testImplementation("io.mockk", "mockk", "1.12.0")
            testImplementation("org.junit.jupiter", "junit-jupiter-api", junitVersion)
            testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junitVersion)

            runtimeOnly(log4j("slf4j-impl"))
            runtimeOnly(log4j("api"))
            runtimeOnly(log4j("core"))
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
    }
}

dependencies {
    implementation(project(":cli"))
    implementation(project(":gui"))
    implementation(log4j("api"))
    implementation(log4j("core"))
}

application {
    mainClass.set("org.powernukkit.converters.WorldConverterLauncher")
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
