import JniUtils.asVariantName

plugins {
    java
    id("dev.nokee.jni-library")
    id("dev.nokee.objective-cpp-language")
    `uber-jni-jar`
    `use-prebuilt-binaries`
}

library {
    dependencies {
        jvmImplementation(project(":auto-dark-mode-base"))
        jvmImplementation("com.github.weisj:darklaf-native-utils")
        nativeImplementation("dev.nokee.framework:JavaVM:[10.14,10.15)")
        nativeImplementation("dev.nokee.framework:JavaVM:[10.14,10.15)") {
            capabilities {
                requireCapability("JavaVM:JavaNativeFoundation:[10.14,10.15)")
            }
        }
        nativeImplementation("dev.nokee.framework:AppKit:[10.14,10.15)")
    }

    targetMachines.addAll(machines.macOS.x86_64)
    variants.configureEach {
        resourcePath.set("com/github/weisj/darkmode/${project.name}/${asVariantName(targetMachine)}")
        sharedLibrary {
            compileTasks.configureEach {
                project.logger.error("CompilerArgs: $compilerArgs")
                compilerArgs.addAll("-mmacosx-version-min=10.14")

                // Build type not modeled yet, assuming release
                compilerArgs.addAll(toolChain.map {
                    when (it) {
                        is Gcc, is Clang -> listOf("-O2")
                        is VisualCpp -> listOf("/O2")
                        else -> emptyList()
                    }
                })
            }
            linkTask.configure {
                linkerArgs.addAll("-lobjc")
            }
        }
    }
}
