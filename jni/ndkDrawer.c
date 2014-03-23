
#include <android/log.h>
#include <android/bitmap.h>

#define LOG_TAG "jni_ndkdrawer"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

void copyPixels(char*dst,int dstx,int dsty,char* src,int srcx,int srcy,int width){
    *(dst + dstx * width + dsty) = *(src + srcx * width + srcy);
    *(dst + dstx * width + dsty + 1) = *(src + srcx * width + srcy + 1);
    *(dst + dstx * width + dsty + 2) = *(src + srcx * width + srcy + 2);
    *(dst + dstx * width + dsty + 3) = *(src + srcx * width + srcy + 3);
}

int scale(AndroidBitmapInfo info,char* pixels,int *scaleRateX,int xlen,int *scaleRateY,int ylen){
    int i = -1;
    int linedst = -1;
    float linesrc = -1;
    int x = 0;
    int y = 0;

    char* buffer = (char*)malloc(info.height * info.stride);
    if(buffer == NULL){
        LOGE("malloc fail!!");
        return 1;
    }
    memcpy(buffer,pixels,info.height * info.stride);
//    memset(buffer,0xff,info.height * info.stride);    
    memset(pixels,0x00,info.height * info.stride);
    int picwidth = info.stride / 4;
    for(i = 0;i < info.height;++ i){
        linedst = scaleRateX[x];
        linesrc = 0;
        float rate = info.width /(float) (info.width - scaleRateX[x]);

        while(1){
            //linear interpolation
            copyPixels(pixels,i,linedst * 4 ,buffer,i,(int)linesrc * 4,info.stride);
            linesrc += rate;
            linedst ++;
            if(linedst > info.width || linesrc > info.width)
                break;

	    if(i > scaleRateY[y]){
                ++ y;
                ++ x;
            }
            if(y > ylen){
                return 1;
            }
        }
    }

    free(buffer);
    return 0;    
}

JNIEXPORT void Java_com_axlecho_memo_newitem_NdkDrawer_scale(JNIEnv* env,jobject thiz,jobject bitmap,\
    jintArray _scalerateX,jintArray _scalerateY){

    AndroidBitmapInfo info;
    void* pixels;
    int ret;
    int *scaleRateX = (int*)(*env)->GetIntArrayElements(env,_scalerateX,0);
    int xlen = (*env)->GetArrayLength(env,_scalerateX);

    int *scaleRateY = (int*)(*env)->GetIntArrayElements(env,_scalerateY,0);
    int ylen = (*env)->GetArrayLength(env,_scalerateY);

    if(scaleRateX == NULL || scaleRateY == NULL){
        LOGE("GetIntArrayElements() failed!");
        return;
    }
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    //LOGI("scaleRateX[100]:%d,scaleRateY[100]:%d",scaleRateX[100],scaleRateY[100]);
    //LOGI("bitmap height:%d width:%d stride:%d",info.height,info.width,info.stride);

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGB_8888 !is %d.",info.format);
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    scale(info,pixels,scaleRateX,xlen,scaleRateY,ylen);
    AndroidBitmap_unlockPixels(env,bitmap);
    //ReleaseFloatArrayElements(scaleRateX);
    //ReleaseFloatArrayElements(scaleRateY);
}

JNIEXPORT void Java_com_axlecho_memo_newitem_NdkDrawer_fillwhite(JNIEnv* env,jobject thiz,jobject bitmap){
    AndroidBitmapInfo info;
    void* pixels;
    int ret;
    
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
    memset(pixels,0xff,info.height * info.width * 4);
    AndroidBitmap_unlockPixels(env,bitmap);
    //ReleaseFloatArrayElements(scaleRateX);
    //ReleaseFloatArrayElements(scaleRateY);
}
