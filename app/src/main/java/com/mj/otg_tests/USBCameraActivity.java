package com.mj.otg_tests;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jiangdg.S.FileUtils;

import com.jiangdg.S.MCC;
import com.serenegiant.usb.WCD;
import com.serenegiant.usb.SI;
import com.serenegiant.usb.UM;
import com.serenegiant.usb.common.ACHS;
import com.serenegiant.usb.encoder.WRP;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * AndroidUSBCamera引擎使用Demo
 *
 * Created by jiangdongguo on 2017/9/30.
 */

public class USBCameraActivity extends AppCompatActivity implements WCD.CameraDialogParent{
    @BindView(R.id.camera_view)
    public View mTextureView;
    @BindView(R.id.btn_capture_pic)
    public Button mBtnCapture;
    @BindView(R.id.btn_rec_video)
    public Button mBtnRecord;
    @BindView(R.id.btn_update_resolution)
    public Button mBtnUpdateResultion;
    @BindView(R.id.btn_restart_camera)
    Button mBtnRestartCamera;
//    @BindView(R.id.btn_contrast)
//    Button mBtnContrast;
//    @BindView(R.id.btn_brightness)
//    Button mBtnBrightness;

    private MCC muvaaH;

    private CameraViewInterface mUVCCameraView;

    private boolean isRequest;
    private boolean isPreview;

    /**
     * USB设备事件监听器
     * */
    private MCC.OnMyDevConnectListener listener = new MCC.OnMyDevConnectListener() {
        // 插入USB设备
        @Override
        public void onAttachDev(UsbDevice device) {
            if(muvaaH == null || muvaaH.getUsbDeviceCount() == 0){
                showShortMsg("****");
                return;
            }
            // 请求打开摄像头
            if(! isRequest){
                isRequest = true;
                if(muvaaH != null){
                    muvaaH.requestPermission(0);
                }
            }
        }

        // 拔出USB设备
        @Override
        public void onDettachDev(UsbDevice device) {
            if(isRequest){
                // 关闭摄像头
                isRequest = false;
                muvaaH.closeCamera();
                showShortMsg(device.getDeviceName()+"已拨出");
            }
        }

        // 连接USB设备成功
        @Override
        public void onConnectDev(UsbDevice device,boolean isConnected) {
            if(! isConnected) {
                showShortMsg("连接失败，请检查分辨率参数是否正确");
                isPreview = false;
            }else{
                isPreview = true;
            }
        }

        // 与USB设备断开连接
        @Override
        public void onDisConnectDev(UsbDevice device) {
            showShortMsg("连接失败");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbcamera);
        ButterKnife.bind(this);
        mUVCCameraView = (CameraViewInterface) mTextureView;
        mUVCCameraView.setCallback(new CameraViewInterface.Callback() {
            @Override
            public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
                if(!isPreview && muvaaH.isCameraOpened()) {
                    muvaaH.startPreview(mUVCCameraView, new ACHS.OnPreViewResultListener() {
                        @Override
                        public void onPreviewResult(boolean result) {

                        }
                    });
                    isPreview = true;
                }
            }

            @Override
            public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

            }

            @Override
            public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
                if(isPreview && muvaaH.isCameraOpened()) {
                    muvaaH.stopPreview();
                    isPreview = false;
                }
            }
        });
        // 初始化引擎
        muvaaH = MCC.getInstance();
        muvaaH.initUSBMonitor(this,listener);
        muvaaH.createUVCCamera(mUVCCameraView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(muvaaH == null)
            return;
        // 注册USB事件广播监听器
        muvaaH.registerUSB();
        mUVCCameraView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 注销USB事件广播监听器
        if(muvaaH != null){
            muvaaH.unregisterUSB();
        }
        mUVCCameraView.onPause();
    }

    @OnClick({  R.id.btn_capture_pic, R.id.btn_rec_video,R.id.btn_update_resolution,R.id.btn_restart_camera})
    public void onViewClick(View view) {
        int vId = view.getId();
        switch (vId) {
            // 对比度
//            case R.id.btn_contrast:
//                if(muvaaH == null || !muvaaH.isCameraOpened())
//                    return;
//                int contrast = muvaaH.getModelValue(MCC.MODE_CONTRAST);
//                muvaaH.setModelValue(MCC.MODE_CONTRAST,contrast++);
//                break;
//            // 亮度
//            case R.id.btn_brightness:
//                if(muvaaH == null || !muvaaH.isCameraOpened())
//                    return;
//                int brightness = muvaaH.getModelValue(MCC.MODE_BRIGHTNESS);
//                muvaaH.setModelValue(MCC.MODE_BRIGHTNESS,brightness++);
//                break;
            // 重启Camera
            case R.id.btn_restart_camera:

                break;
            // 切换分辨率
            case R.id.btn_update_resolution:
                if(muvaaH == null || !muvaaH.isCameraOpened())
                    return;
                muvaaH.updateResolution(320, 240, new MCC.OnPreviewListener() {
                    @Override
                    public void onPreviewResult(boolean isSuccess) {
                        if(! isSuccess) {
                            showShortMsg("预览失败，不支持该分辨率");
                        }else {
                            showShortMsg("以切换到分辨率为320x240");
                        }
                    }
                });
                break;
            // 点击后自动对焦
            case R.id.camera_view:
                if(muvaaH == null)
                    return;
//                muvaaH.startCameraFoucs();
//                showShortMsg("对焦相机");
                List<SI> list = muvaaH.getSupportedPreviewSizes();
                if(list == null) {
                    return;
                }

                StringBuilder sb = new StringBuilder();
                for(SI size:list){
                    sb.append(size.width+"x"+size.height);
                    sb.append("\n");
                }
                showShortMsg(sb.toString());
                break;
            case R.id.btn_capture_pic:
                if(muvaaH == null || ! muvaaH.isCameraOpened()){
                    showShortMsg("抓拍异常，摄像头未开启");
                    return;
                }
                String picPath = MCC.ROOT_PATH+System.currentTimeMillis()
                        + MCC.SUFFIX_PNG;
                muvaaH.capturePicture(picPath, new ACHS.OnCaptureListener() {
                    @Override
                    public void onCaptureResult(String path) {
                        showShortMsg("保存路径："+path);
                    }
                });
                break;
            case R.id.btn_rec_video:
                if(muvaaH == null || ! muvaaH.isCameraOpened()){
                    showShortMsg("录制异常，摄像头未开启");
                    return;
                }

                if(! muvaaH.isRecording()){
                    String videoPath = MCC.ROOT_PATH+System.currentTimeMillis();
                    FileUtils.createfile(FileUtils.ROOT_PATH+"test666.h264");
                    WRP params = new WRP();
                    params.setRecordPath(videoPath);
                    params.setRecordDuration(0);    // 设置为0，不分割保存
                    params.setVoiceClose(false);    // 不屏蔽声音
                    muvaaH.startRecording(params, new ACHS.OnEncodeResultListener() {
                        @Override
                        public void onEncodeResult(byte[] data, int offset, int length, long timestamp, int type) {
                            // type = 0,aac格式音频流
                            // type = 1,h264格式视频流
                            if(type == 1){
                                FileUtils.putFileStream(data,offset,length);
                            }
                        }

                        @Override
                        public void onRecordResult(String videoPath) {
                            showShortMsg(videoPath);
                        }
                    });

                    mBtnRecord.setText("正在录制");
                } else {
                    FileUtils.releaseFile();
                    muvaaH.stopRecording();
                    mBtnRecord.setText("开始录制");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(muvaaH != null){
            muvaaH.release();
        }
    }

    private void showShortMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public UM getUSBMonitor() {
        return muvaaH.getUSBMonitor();
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if(canceled){
            showShortMsg("取消操作");
        }
    }

    public boolean isCameraOpened() {
        return muvaaH.isCameraOpened();
    }
}
