#include "AndroidLlamaBridge.h"

/**
 * Android JNI implementation placeholder.
 *
 * Do not add this file to CMakeLists.txt or call System.loadLibrary() until the
 * llama.cpp Android library, ABI builds, lifecycle ownership, and model storage
 * policy are approved.
 */
extern "C" {

JNIEXPORT jlong JNICALL
Java_com_example_localai_LlamaNative_nativeCreate(JNIEnv*, jobject) {
    return 0L;
}

JNIEXPORT jboolean JNICALL
Java_com_example_localai_LlamaNative_nativeLoadModel(JNIEnv*, jobject, jlong, jstring) {
    return JNI_FALSE;
}

JNIEXPORT jstring JNICALL
Java_com_example_localai_LlamaNative_nativeGenerate(
    JNIEnv* env,
    jobject,
    jlong,
    jstring,
    jint,
    jfloat
) {
    return env->NewStringUTF("Llama JNI is a scaffold; no native inference backend is linked.");
}

JNIEXPORT void JNICALL
Java_com_example_localai_LlamaNative_nativeDestroy(JNIEnv*, jobject, jlong) {
    // Placeholder: native ownership does not exist yet.
}

} // extern "C"
