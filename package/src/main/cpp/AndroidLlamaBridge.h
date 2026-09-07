#pragma once

#include <jni.h>

/**
 * Android JNI declarations for the shared llama.cpp-backed inference engine.
 *
 * The active Android app still does not load this library or request model storage;
 * JNI package names match LlamaNative.kt and are wired only after the documented
 * Gradle/CMake integration steps are applied.
 */
extern "C" {

JNIEXPORT jlong JNICALL
Java_com_example_localai_LlamaNative_nativeCreate(JNIEnv* env, jobject thiz);

JNIEXPORT jboolean JNICALL
Java_com_example_localai_LlamaNative_nativeLoadModel(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jstring modelPath
);

JNIEXPORT jstring JNICALL
Java_com_example_localai_LlamaNative_nativeGenerate(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jstring prompt,
    jint maxTokens,
    jfloat temperature
);

JNIEXPORT void JNICALL
Java_com_example_localai_LlamaNative_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle);

} // extern "C"
