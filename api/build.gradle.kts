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
    kotlin("jvm")
    id("org.jetbrains.dokka")
    jacoco
    application
}

val jacksonVersion = "2.12.4"

dependencies {
    api( "br.com.gamemods", "nbt-manipulator", "3.0.0" )
    api( "org.powernukkit", "version-library", "1.0.0" )
    implementation( "io.ktor", "ktor-utils-jvm", "1.6.2" )
    implementation( "br.com.gamemods", "region-manipulator", "2.0.0" )
    implementation( "com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", jacksonVersion )
    implementation( "com.fasterxml.jackson.module", "jackson-module-kotlin", jacksonVersion )
    implementation( "org.powernukkit.bedrock.leveldb", "bedrock-leveldb", "0.11.0-PN" )
    implementation("io.gomint", "leveldb-jni", "1.3.0-SNAPSHOT")
}
