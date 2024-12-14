plugins {
    val modstitchVersion = "0.1.1"
    // You can append `.<platform>` to the plugin ID to apply a specific platform without relying on the gradle property
    // like: `id("dev.isxander.modstitch.base.loom") version modstitchVersion`
    id("dev.isxander.modstitch.base") version modstitchVersion
    id("dev.isxander.modstitch.publishing") version modstitchVersion
}

modstitch {
    minecraftVersion = stonecutter.current.version
    javaTarget = 21

    /* Modstitch supports parchment, but due to a fabric-loom bug, it is unsupported on loom. A PR is pending.
    parchment {
        mappingsVersion = "2024.12.07"
    }
    */

    // This metadata is used for many things from mod publishing to string replacements in mod manifests
    metadata {
        modId = "example_mod"
        modName = "Example Mod"
        modVersion = "1.0.0"
        modGroup = "com.example"

        // There are more properties in here, type `this.` and let your IDE show you the rest
    }

    // This block configures loom-specific settings
    msLoom {
        fabricLoaderVersion = "0.16.9"

        // This block configures the `loom` extension that fabric-loom exposes by default,
        // you can configure loom like normal from here
        loom {

        }
    }

    // This block configures moddevgradle-specific settings
    msModdevgradle {
        neoForgeVersion = property("deps.neoforge") as String

        // Configures client and server runs for MDG, it is not done by default
        defaultRuns()

        // This block configures the `neoforge` extension that MDG exposes by default,
        // you can configure MDG like normal from here
        modDevGradle {

        }
    }
}

stonecutter {
    // Allows you to do `if fabric { ... }` or `if neoforge { ... }` in stonecutter comments
    consts(
        "fabric" to modstitch.isLoom,
        "neoforge" to modstitch.isModDevGradle,
    )
}

// all dependencies should go through the modstitch proxy configurations
// modstitch ensures dependencies are configured correctly for the target platform
// to create these configurations for more source sets, use `modstitch.createProxyConfigurations(sourceSets["client"])` for example
dependencies {
    if (modstitch.isLoom) {
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:0.112.0+1.21.4")
    }

    modstitchImplementation("org.commonmark:commonmark:0.21.0")

    // Modstitch does not currently support jar-in-jar (or jarJar) dependencies
}

// Stands for 'modstitch publishing'
// This is provided by the `publishing` extension plugin defined at the top
msPublishing {
    // the publication is already set up by modstitch
    maven {
        repositories {
            mavenLocal()
        }
    }

    // modstitch covers all the basics like setting the file,
    // name and version, so we just need to set the project ID
    // and configure the platforms
    mpp {
        type = STABLE
        changelog = "hello"

        modrinth {
            accessToken = "abc123"
            projectId = "12345678"
        }
    }
}

