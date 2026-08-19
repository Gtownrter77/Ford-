#pragma once

#include <jni.h>

/**
 * Android JNI declarations only.
 *
 * This header does not load a native library, request storage permission, load a
 * GGUF file, or invoke llama.cpp. JNI package names match LlamaNative.kt.
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
