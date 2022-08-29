#include <jni.h>
#include <string>
#include <SKP_Silk_SDK_API.h>
#include "coder.h"

std::string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr) {
        return "";
    }
    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const auto stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes,
                                                                       env->NewStringUTF("UTF-8"));

    auto length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte *pBytes = env->GetByteArrayElements(stringJbytes, nullptr);

    std::string ret = std::string((char *) pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_cc_imorning_silk_SilkCoder_nativeGetSilkVersion(JNIEnv *env, jobject obj) {
    return env->NewStringUTF(SKP_Silk_SDK_get_version());
}

extern "C"
JNIEXPORT jint JNICALL
Java_cc_imorning_silk_SilkCoder_nativeEncode(JNIEnv *env, jobject obj,
                                             jstring jInput, jstring jOutput) {
    std::string input = jstring2string(env, jInput);
    std::string output = jstring2string(env, jOutput);
    int result = Coder::encode(input, output);
    LOG_I("encode_result: %d", result);
    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_cc_imorning_silk_SilkCoder_nativeDecode(JNIEnv *env, jobject obj,
                                             jstring jInput, jstring jOutput) {
    std::string input = jstring2string(env, jInput);
    std::string output = jstring2string(env, jOutput);
    int result = Coder::decode(input, output);
    LOG_I("decode_result: %d", result);
    return result;
}