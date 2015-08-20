
#include <android/log.h>
#include <android/bitmap.h>

#define LOG_TAG "jni_ndkdrawer"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

static char* buffer;
static AndroidBitmapInfo bufferinfo;

void copyPixels(char*dst,int dstx,int dsty,int dstwidth,char* src,int srcx,int srcy,int srcwidth){
    //*(dst + dstx * width + dsty) = *(src + srcx * width + srcy);
    //*(dst + dstx * width + dsty + 1) = *(src + srcx * width + srcy + 1);
    //*(dst + dstx * width + dsty + 2) = *(src + srcx * width + srcy + 2);
    //*(dst + dstx * width + dsty + 3) = *(src + srcx * width + srcy + 3);
    memcpy(dst + dstx * dstwidth + dsty,src + srcx * srcwidth +srcy,4);
}

int scale(AndroidBitmapInfo info,char* pixels,char* buffer,int *scaleRateX,int xlen,int *scaleRateY,int ylen){
    int i = -1;
    int linedst = -1;
    float linesrc = -1;
    int x = 0;
    int y = 0;

    if(buffer == NULL || pixels == NULL){
        LOGE("pointer NULL!!");
        return 1;
    }
    int picwidth = info.stride / 4;

    for(i = 0;i < info.height;++ i){
        linedst = scaleRateX[x];
        linesrc = 0;

        if((info.width -scaleRateX[x]) < 0.0001){
            LOGE("info width == scaleRateX:scaleRateX[x]:%d",scaleRateX[x]);
            return 1;
        }

        float rate = info.width /(float) (info.width - scaleRateX[x]);

        if(y >= ylen){
            LOGE("scaleRateY outof arrayindexbound, y:%d",y);
            return 1;
        }
        if(x >= xlen){
            LOGE("scaleRateX outof arrayindexbound,x:%d",x);
            return 1;
        }

        if(i >= scaleRateY[y]){
            ++ y;
            ++ x;
        }
       
        while(1){
            //linear interpolation
            copyPixels(pixels,i,linedst * 4,info.stride,buffer,i,(int)linesrc * 4,bufferinfo.stride);
            linesrc += rate;
            linedst ++;
            if(linedst >= info.width || linesrc >= bufferinfo.width)
                    break;

        }
    }

    return 0;    
}


JNIEXPORT void Java_axlecho_memo_newitem_NdkDrawer_scale(JNIEnv* env,jobject thiz,jobject bitmap,\
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

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGB_8888 !is %d.",info.format);
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    scale(info,pixels,buffer,scaleRateX,xlen,scaleRateY,ylen);
    AndroidBitmap_unlockPixels(env,bitmap);
}

JNIEXPORT void Java_axlecho_memo_newitem_NdkDrawer_setdata(JNIEnv* env,jobject this,jobject bitmap){
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
    buffer = (char*)malloc(info.height * info.stride);
    memcpy(buffer,pixels,info.height * info.stride);
    bufferinfo = info;
    AndroidBitmap_unlockPixels(env,bitmap);
}

JNIEXPORT void Java_axlecho_memo_newitem_NdkDrawer_releasedata(JNIEnv* env,jobject thiz){
    free(buffer);
}

JNIEXPORT void Java_axlecho_memo_newitem_NdkDrawer_fillwhite(JNIEnv* env,jobject thiz,jobject bitmap){
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
}
