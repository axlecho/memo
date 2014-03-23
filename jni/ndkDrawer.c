
#include <android/log.h>
#include <android/bitmap.h>

#define LOG_TAG "jni_ndkdrawer"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

JNIEXPORT void Java_com_axlecho_memo_newitem_NdkDrawer_scale(JNIEnv* env,jobject thiz,jobject bitmap,jintArray _scalerate){
    AndroidBitmapInfo info;
    void* pixels;
    int ret;
    int *scaleRate = (int*)(*env)->getByteIntElements(env,_scalerate,0);
    if(scaleRate == NULL){
        LOGE("GetByteIntElements() failed!");
        return;
    }
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGB_8888 !is %d.",info.format);
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    
    AndroidBitmap_unlockPixels(env,bitmap);
}
