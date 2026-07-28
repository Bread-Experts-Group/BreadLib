@file:Suppress("ImplicitThis", "IncorrectFormatting", "KDocMissingDocumentation", "DEPRECATION")

plugins {
	id("multiloader-loader")
	id("net.neoforged.moddev")
}

private fun getProp(property: String): String = providers.gradleProperty(property).get()
val kotlinVersion: String = getProp("kotlin_version")

repositories {
	exclusiveContent {
		forRepository {
			maven {
				name = "Modrinth"
				url = uri("https://api.modrinth.com/maven")
			}
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}
}

neoForge {
	version = getProp("neoforge_version")
	accessTransformers.from(project(":common").file("src/main/resources/META-INF/accesstransformer.cfg"))
	parchment {
		minecraftVersion = getProp("minecraft_version")
		mappingsVersion = getProp("parchment_version")
	}
	runs {
		configureEach {
			systemProperty("neoforge.enabledGameTestNamespaces", getProp("mod_id"))
			ideName = "NeoForge ${this.name.capitalize()} (${project.path})" // Unify the run config names with fabric

			additionalRuntimeClasspathConfiguration.dependencies.addAll(
				listOf(
				dependencies.create("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion") { isTransitive = false },
				dependencies.create("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion") { isTransitive = false },
				dependencies.create("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion") {
					isTransitive = false
				},
				dependencies.create("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion") {
					isTransitive = false
				}
			))
		}
		create("client") {
			client()
			gameDirectory.set(File("$rootDir/runs/client"))
		}
		create("data") {
			data()
			programArguments.addAll(
				"--all",
				"--mod", getProp("mod_id"),
				"--output", file("src/generated/resources/").absolutePath,
				"--existing", file("src/main/resources/").absolutePath
			)
		}
		create("server") {
			server()
			gameDirectory.set(File("$rootDir/runs/server"))
		}
	}
	mods {
		create(getProp("mod_id")) {
			sourceSet(sourceSets.main.get())
			sourceSet(project(":common").sourceSets.main.get())
		}
	}
}

sourceSets.main.get().resources {
	srcDirs("src/generated/resources")
}

dependencies {
	jarJar(implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion") {})
	jarJar(implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion") {})
	// https://modrinth.com/mod/jade/version/15.10.5+fabric
	runtimeOnly("maven.modrinth:nvQzSEkH:yd8FKCmx")
	runtimeOnly("maven.modrinth:iRmWy6ga:BPGKb8pi")
}