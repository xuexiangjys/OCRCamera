/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xuexiang.ocrcamerademo.R;
import com.xuexiang.ocrcamerademo.core.BaseFragment;
import com.xuexiang.ocrcamerademo.utils.XToastUtils;
import com.xuexiang.xaop.annotation.MainThread;
import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.ClickUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.xuexiang.ocrcamerademo.fragment.OCRCameraFragment.KEY_OCR_RESULT;
import static com.xuexiang.xaop.consts.PermissionConsts.CAMERA;

/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(name = "OCRCamera", anim = CoreAnim.none)
public class MainFragment extends BaseFragment implements ClickUtils.OnClick2ExitListener {

    private static final int REQUEST_CODE_OCR = 10000;

    @BindView(R.id.tv_result_info)
    TextView tvResultInfo;

    @Override
    protected TitleBar initTitle() {
        return super.initTitle().setLeftClickListener(view -> ClickUtils.exitBy2Click(2000, this));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initViews() {
        tvResultInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @SingleClick
    @OnClick(R.id.btn_start_ocr)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_ocr:
                openOCRCamera();
                break;
            default:
                break;
        }
    }

    /**
     * 打开OCRCamera
     */
    @Permission(CAMERA)
    private void openOCRCamera() {
        openPageForResult(OCRCameraFragment.class, REQUEST_CODE_OCR);
    }


    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OCR && resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            String result = data.getStringExtra(KEY_OCR_RESULT);
            showResult(result);
        }
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickUtils.exitBy2Click(2000, this);
        }
        return true;
    }


    @Override
    public void onRetry() {
        XToastUtils.toast("再按一次退出程序");
    }

    @Override
    public void onExit() {
        XUtil.exitApp();
    }

    @MainThread
    private void showResult(String result) {
        tvResultInfo.setText("识别结果:\n\r" + result);
    }

    private void clearLog() {
        tvResultInfo.setText("");
    }

}
