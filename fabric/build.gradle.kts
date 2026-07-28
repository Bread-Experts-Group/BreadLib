@file:Suppress("ImplicitThis", "UnstableApiUsage", "KDocMissingDocumentation")

plugins {
	id("multiloader-loader")
	id("fabric-loom")
}

private fun getProp(property: String): String = providers.gradleProperty(property).get()

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

dependencies {
	minecraft("com.mojang:minecraft:${getProp("minecraft_version")}")
	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-${getProp("minecraft_version")}:${getProp("parchment_version")}@zip")
	})
	modImplementation("net.fabricmc:fabric-loader:${getProp("fabric_loader_version")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${getProp("fabric_version")}")
	modImplementation("teamreborn:energy:4.1.0")
	// https://modrinth.com/mod/jade/version/15.10.5+fabric
	modImplementation("maven.modrinth:nvQzSEkH:5Sbkzz4O")
	// https://modrinth.com/mod/techreborn/version/5.11.10
	modImplementation("maven.modrinth:3eMENr4V:tYneg20h")
	// https://modrinth.com/mod/reborncore/version/5.11.10 (dependency of tech reborn)
	modImplementation("maven.modrinth:3NCrJdj3:9YLLekgJ")
}

loom {
	val aw = project(":common").file("src/main/resources/${getProp("mod_id")}.accesswidener")
	if (aw.exists()) accessWidenerPath.set(aw)
	mixin {
		defaultRefmapName.set("${getProp("mod_id")}.refmap.json")
		useLegacyMixinAp = true
	}
	runs {
		getByName("client") {
			client()
			displayName = "Fabric Client"
			generateRunConfig = true
			runDirectory.dir("$rootDir/runs/client")
		}
		getByName("server") {
			server()
			displayName = "Fabric Server"
			generateRunConfig = true
			runDirectory.dir("$rootDir/runs/server")
		}
	}
}
fabricApi {
	configureDataGeneration()
}