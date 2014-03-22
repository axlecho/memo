
#include <android/log.h>
#include <android/bitmap.h>

#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

JNIEXPORT void Java_com_axlecho_memo_newitem_NdkDrawer(JNIEnv* env,jobject thiz,jobject btm,jintArray scalerate){

}
