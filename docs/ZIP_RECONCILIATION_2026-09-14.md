# ZIP Reconciliation Note

Mentor.zip was audited against GitHub main. The ZIP snapshot contains 1,084 files. Core Android/build files checked by Git blob SHA match GitHub main, including app/build.gradle.kts, settings.gradle.kts, gradle/libs.versions.toml, MainActivity.kt, SportTracData.kt, Interactive3DViewport.kt, MentorKnowledge.kt, mentor-web index/content, workflow, .gitignore, and .env.example. The ZIP's CUSTOM_DUAL_EXHAUST_GUIDE.md also exactly matches main.

The ZIP does not contain the native CMake/Llama implementation referenced by app/build.gradle.kts. GitHub main likewise has only AndroidLlamaBridge.cpp/.h under app/src/main/cpp and no shared/llama tree or LlamaInference.h; therefore the normal native build path is currently incomplete. A local Gradle build could not reach compilation because the sandbox cannot download Gradle 9.3.1 from services.gradle.org.
