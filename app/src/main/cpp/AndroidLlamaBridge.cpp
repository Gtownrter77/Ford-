#include "AndroidLlamaBridge.h"

#include <memory>
#include <string>

#include "LlamaInference.h"

/**
 * Android JNI bridge for the shared LlamaInference contract.
 *
 * The Java/Kotlin layer must first load `sporttrac_llama` through System.loadLibrary
 * after the Android Gradle/CMake link steps in LLAMA_CPP_PLATFORM_LINKING.md are applied.
 */
namespace {

using sporttrac::llama::GenerationConfig;
using sporttrac::llama::LlamaInference;

std::string toStdString(JNIEnv* env, jstring value) {
    if (value == nullptr) {
        return {};
    }
    const char* chars = env->GetStringUTFChars(value, nullptr);
    if (chars == nullptr) {
        return {};
    }
    std::string result(chars);
    env->ReleaseStringUTFChars(value, chars);
    return result;
}

LlamaInference* toEngine(jlong handle) {
    return reinterpret_cast<LlamaInference*>(handle);
}

} // namespace

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_example_localai_LlamaNative_nativeCreate(JNIEnv*, jobject) {
    return reinterpret_cast<jlong>(new LlamaInference());
}

JNIEXPORT jboolean JNICALL
Java_com_example_localai_LlamaNative_nativeLoadModel(JNIEnv* env, jobject, jlong handle, jstring modelPath) {
    auto* engine = toEngine(handle);
    return engine != nullptr && engine->loadModel(toStdString(env, modelPath))
        ? JNI_TRUE
        : JNI_FALSE;
}

JNIEXPORT jstring JNICALL
Java_com_example_localai_LlamaNative_nativeGenerate(
    JNIEnv* env,
    jobject,
    jlong handle,
    jstring prompt,
    jint maxTokens,
    jfloat temperature
) {
    auto* engine = toEngine(handle);
    if (engine == nullptr) {
        return env->NewStringUTF("Native llama engine is not initialized.");
    }

    const auto result = engine->generate(
        toStdString(env, prompt),
        GenerationConfig{
            maxTokens,
            temperature,
            4096
        }
    );
    const auto& value = result.error.empty() ? result.text : result.error;
    return env->NewStringUTF(value.c_str());
}

JNIEXPORT void JNICALL
Java_com_example_localai_LlamaNative_nativeDestroy(JNIEnv*, jobject, jlong handle) {
    delete toEngine(handle);
}

} // extern "C"
