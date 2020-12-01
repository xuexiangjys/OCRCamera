/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.ocrcamerademo.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;

import com.google.android.cameraview.CameraImpl;
import com.xuexiang.ocrcamerademo.R;
import com.xuexiang.ocrcamerademo.core.BaseFragment;
import com.xuexiang.ocrcamerademo.utils.CameraUtils;
import com.xuexiang.ocrcamerademo.utils.OCRUtils;
import com.xuexiang.ocrcamerademo.utils.XToastUtils;
import com.xuexiang.xaop.annotation.MainThread;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.common.StringUtils;

import butterknife.BindView;
import me.pqpo.smartcameralib.MaskView;
import me.pqpo.smartcameralib.SmartCameraView;
import me.pqpo.smartcameralib.SmartScanner;

import static android.app.Activity.RESULT_OK;

/**
 * @author xuexiang
 * @since 2020/11/28 1:54 PM
 */
@Page(name = "OCR识别")
public class OCRCameraFragment extends BaseFragment {

    public static final String KEY_OCR_RESULT = "key_ocr_result";

    @BindView(R.id.camera_view)
    SmartCameraView mCameraView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_orc_camera;
    }

    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected void initArgs() {
        CameraUtils.fullScreen(getActivity());
    }

    @Override
    protected void initViews() {
        initMaskView();
        initScannerParams();
        initCameraView();
    }


    /**
     * 初始化遮罩层的大小
     */
    private void initMaskView() {
        final MaskView maskView = (MaskView) mCameraView.getMaskView();
        maskView.setMaskLineColor(0xff00adb5);
        maskView.setShowScanLine(true);
        maskView.setScanLineGradient(0xff00adb5, 0x0000adb5);
        maskView.setMaskLineWidth(2);
        maskView.setMaskRadius(5);
        maskView.setScanSpeed(6);
        maskView.setScanGradientSpread(80);
        mCameraView.setMaskView(maskView);
    }


    private void initScannerParams() {
        SmartScanner.DEBUG = true;
        /*
          canny 算符阈值
          1. 低于阈值1的像素点会被认为不是边缘；
          2. 高于阈值2的像素点会被认为是边缘；
          3. 在阈值1和阈值2之间的像素点,若与第2步得到的边缘像素点相邻，则被认为是边缘，否则被认为不是边缘。
         */
        SmartScanner.cannyThreshold1 = 20;
        SmartScanner.cannyThreshold2 = 50;
        /*
         * 霍夫变换检测线段参数
         * 1. threshold: 最小投票数，要检测一条直线所需最少的的曲线交点，增大该值会减少检测出的线段数量。
         * 2. minLinLength: 能组成一条直线的最少点的数量, 点数量不足的直线将被抛弃。
         * 3. maxLineGap: 能被认为在一条直线上的点的最大距离，若出现较多断断续续的线段可以适当增大该值。
         */
        SmartScanner.houghLinesThreshold = 130;
        SmartScanner.houghLinesMinLineLength = 80;
        SmartScanner.houghLinesMaxLineGap = 10;
        /*
         * 高斯模糊半径，用于消除噪点，必须为正奇数。
         */
        SmartScanner.gaussianBlurRadius = 3;

        // 检测范围比例, 比例越小表示待检测物体要更靠近边框
        SmartScanner.detectionRatio = 0.1f;
        // 线段最小长度检测比例
        SmartScanner.checkMinLengthRatio = 0.8f;
        // 为了提高性能，检测的图片会缩小到该尺寸之内
        SmartScanner.maxSize = 300;
        // 检测角度阈值
        SmartScanner.angleThreshold = 5;
        // don't forget reload params
        SmartScanner.reloadParams();
    }


    private void initCameraView() {
        mCameraView.getSmartScanner().setPreview(true);
        mCameraView.setOnScanResultListener((smartCameraView, result, yuvData) -> {
            Bitmap previewBitmap = smartCameraView.getPreviewBitmap();
            return false;
        });

        mCameraView.addCallback(new CameraImpl.Callback() {

            @Override
            public void onCameraOpened(CameraImpl camera) {
                super.onCameraOpened(camera);
            }

            @Override
            public void onPictureTaken(CameraImpl camera, byte[] data) {
                super.onPictureTaken(camera, data);
                mCameraView.cropJpegImage(data, cropBitmap -> {
                    if (cropBitmap != null) {
                        doOCRAction(cropBitmap);
                    }
                });
            }
        });
    }

    /**
     * 执行orc识别操作
     */
    private void doOCRAction(Bitmap bitmap) {
        getMessageLoader("识别中...").show();
        OCRUtils.recVehicleLicense(getContext(), bitmap, new OCRUtils.OnOCRListener<String>() {
            @MainThread
            @Override
            public void onSuccess(String result) {
                getMessageLoader().dismiss();
                if (!StringUtils.isEmpty(result)) {
                    Intent intent = new Intent();
                    intent.putExtra(KEY_OCR_RESULT, result);
                    setFragmentResult(RESULT_OK, intent);
                    popToBack();
                } else {
                    if (mCameraView != null) {
                        mCameraView.startScan();
                    }
                }

            }

            @MainThread
            @Override
            public void onError(String error) {
                getMessageLoader().dismiss();
                XToastUtils.error(error);
                if (mCameraView != null) {
                    mCameraView.startScan();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mCameraView != null) {
            mCameraView.start();
            mCameraView.startScan();
        }
    }


    @Override
    public void onPause() {
        if (mCameraView != null) {
            mCameraView.stop();
            mCameraView.stopScan();
        }
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetMaskViewSize();
    }

    private void resetMaskViewSize() {
        if (mCameraView == null) {
            return;
        }
        mCameraView.postDelayed(() -> {
            final MaskView maskView = (MaskView) mCameraView.getMaskView();
            maskView.setMaskSize(-1, -1);
            mCameraView.setMaskView(maskView);
        }, 100);

    }

    @Override
    public void onDestroyView() {
        CameraUtils.cancelFullScreen(getActivity());
        super.onDestroyView();
    }
}
