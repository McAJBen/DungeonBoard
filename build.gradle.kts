import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
	kotlin("jvm") version "1.3.61"
	id("org.jetbrains.kotlin.plugin.serialization") version "1.3.60"
}

group = "mcajben.dungeonboard"

version = "dev"

val releaseVersion = "3.0.1"

val resourcesDir = File(buildDir, "resources/main")

val jarDir = File(buildDir, "libs")

val jarFile: File
	get() = File(jarDir, "${rootProject.name}-$version.jar")

val releaseFile: File
	get() = File("${rootProject.name}-$releaseVersion.jar")

repositories {
	jcenter()
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
}

tasks.withType<KotlinCompile>().configureEach {
	kotlinOptions.jvmTarget = "1.8"
}

val jar = tasks.named<Jar>("jar") {
	manifest {
		attributes("Main-Class" to "main.MainKt")
	}

	from(configurations.compileClasspath.map { config ->
		config.map { f ->
			if (f.isDirectory) f
			else zipTree(f)
		}
	})
}

val setReleaseVersion by tasks.registering {
	version = releaseVersion
}

val generateVersionProperties by tasks.registering {
	doLast {
		val propertiesFile = File(resourcesDir, "version.properties")
		propertiesFile.parentFile.mkdirs()

		Properties().apply {
			setProperty("version", version.toString())
			store(propertiesFile.bufferedWriter(), null)
			println(this)
		}
	}
}

tasks.named("processResources") {
	dependsOn(generateVersionProperties)
}

val buildRelease by tasks.registering {
	dependsOn(setReleaseVersion)
	dependsOn(generateVersionProperties)
	dependsOn(jar)
	doLast {
		releaseFile.delete()
		jarFile.copyTo(releaseFile)
		jarFile.delete()
	}
}
