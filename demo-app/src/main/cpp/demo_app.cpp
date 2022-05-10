#include <jni.h>
#include <exception>

extern "C" JNIEXPORT void JNICALL
Java_com_fankes_apperrorsdemo_native_Channel_throwNativeException(JNIEnv *env, jobject) {
    throw std::exception();
}