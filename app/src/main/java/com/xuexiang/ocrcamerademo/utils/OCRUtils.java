package com.xuexiang.ocrcamerademo.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.OcrRequestParams;
import com.baidu.ocr.sdk.model.OcrResponseResult;
import com.xuexiang.ocrcamerademo.BuildConfig;
import com.xuexiang.rxutil2.exception.RxException;
import com.xuexiang.rxutil2.rxjava.RxSchedulerUtils;
import com.xuexiang.rxutil2.subsciber.BaseSubscriber;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.display.ImageUtils;
import com.xuexiang.xutil.file.FileUtils;

import java.io.File;

import io.reactivex.Observable;

/**
 * 百度OCR识别
 *
 * @author xuexiang
 * @since 2020/11/28 2:35 PM
 */
public final class OCRUtils {

    private static AccessToken sAccessToken;

    private OCRUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 用明文ak，sk初始化
     */
    public static void initAccessTokenWithAkSk(Application application) {
        OCR.getInstance(application).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                sAccessToken = result;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
            }
        }, application, BuildConfig.OCR_APP_KEY, BuildConfig.OCR_SECRET_KEY);
    }

    public static boolean isOCRInitSuccess() {
        return sAccessToken != null;
    }

    /**
     * 普通识别文字
     *
     * @param filePath
     * @param listener
     */
    public static void recGeneral(String filePath, final OnResultListener<GeneralResult> listener) {
        GeneralParams param = new GeneralParams();
        param.setDetectDirection(true);
        param.setRecognizeGranularity(GeneralParams.GRANULARITY_SMALL);
        param.setImageFile(new File(filePath));
        OCR.getInstance(XUtil.getContext()).recognizeGeneral(param, listener);
    }

    public static String getOrcImageTempFilePath() {
        return FileUtils.getDiskCacheDir("orc") + File.separator + "temp.jpg";
    }

    /**
     * 识别行驶证
     *
     * @param context  上下文
     * @param bitmap   识别图片
     * @param listener 识别监听
     */
    public static void recVehicleLicense(Context context, Bitmap bitmap, final OnOCRListener<String> listener) {
        Observable.just(bitmap)
                .map(bitmap1 -> {
                    String filePath = getOrcImageTempFilePath();
                    boolean result = ImageUtils.save(bitmap1, filePath, Bitmap.CompressFormat.JPEG);
                    return result ? filePath : "";
                })
                .compose(RxSchedulerUtils._io_main_o())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onSuccess(String filePath) {
                        File imageFile = FileUtils.getFileByPath(filePath);
                        if (imageFile == null) {
                            listener.onError("文件不存在！");
                            return;
                        }
                        OcrRequestParams param = new OcrRequestParams();
                        param.setImageFile(imageFile);
                        OCR.getInstance(context).recognizeVehicleLicense(param, new OnResultListener<OcrResponseResult>() {
                            @Override
                            public void onResult(OcrResponseResult result) {
                                listener.onSuccess(result.getJsonRes());
                            }

                            @Override
                            public void onError(OCRError error) {
                                listener.onError(error.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onError(RxException e) {
                        if (listener != null) {
                            listener.onError(e.getMessage());
                        }
                    }
                });
    }

    /**
     * ocr识别监听
     */
    public interface OnOCRListener<T> {
        /**
         * 识别成功
         *
         * @param info
         */
        void onSuccess(T info);

        /**
         * 识别失败
         *
         * @param error
         */
        void onError(String error);
    }
}
