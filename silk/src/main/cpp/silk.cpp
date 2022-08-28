#include <jni.h>
#include <string>

#include "SKP_Silk_SDK_API.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_cc_imorning_silk_SilkCoder_getVersion(JNIEnv *env, jobject obj) {
    std::string version = "silk version is: ";
    version += SKP_Silk_SDK_get_version();
    return env->NewStringUTF(version.c_str());
}