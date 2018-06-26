package com.app.mimamori;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends Activity {

    CameraManager mCameraManager;
    CameraDevice mCameraDevice;
    List<Surface> mSurfaceList;
    SurfaceView mCameraPreviewSurface;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mContext = getApplicationContext();
        mSurfaceList = new ArrayList<>();
        mCameraPreviewSurface = findViewById(R.id.camera_preview);
        mSurfaceList.add(mCameraPreviewSurface.getHolder().getSurface());

        mCameraManager = (CameraManager) mContext.getSystemService(Service.CAMERA_SERVICE);


        if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            requestPermissions(new String[]{Manifest.permission.CAMERA},PackageManager.PERMISSION_GRANTED);
            return;
        }else{
            openCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            openCamera();
        }
    }

    @SuppressLint("MissingPermission")
    private void openCamera(){
        try {
            mCameraManager.openCamera("0", new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    mCameraDevice = cameraDevice;
                    createCameraSession();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {

                }

                @Override
                public void onError(@NonNull CameraDevice cameraDevice, int i) {

                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void createCameraSession(){


        try {
            CaptureRequest.Builder previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(mSurfaceList.get(0));
            CaptureRequest previewRequest = previewRequestBuilder.build();

            new Handler().postDelayed(() -> {
                try {
                    mCameraDevice.createCaptureSession(mSurfaceList, new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Log.d("Camera_debugger","configured");
                            try {
                                cameraCaptureSession.setRepeatingRequest(previewRequest, new CameraCaptureSession.CaptureCallback() {
                                    @Override
                                    public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                                        super.onCaptureStarted(session, request, timestamp, frameNumber);

                                    }
                                },null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            },1000);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
