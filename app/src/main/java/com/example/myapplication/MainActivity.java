package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements Camera2APIs.Camera2Interface, TextureView.SurfaceTextureListener {

    private TextureView mTextureView;
    private Camera2APIs mCamera;
    private final int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (allPermissionsGranted()) {
            startCamera(); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        mTextureView = (TextureView)findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(this);

        mCamera = new Camera2APIs(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_PERMISSIONS:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //동의 했을 경우 .....
                }else{
                    //거부했을 경우
                 Toast toast=Toast.makeText(this,"기능 사용을 위한 권한 동의가 필요합니다.", Toast.LENGTH_SHORT);
                 toast.show();
                }
                break;
            default:
                break;
        }

/*
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }*/
    }

    // 유틸 함수
    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void startCamera() {
        if (mTextureView.isAvailable()) {
            openCamera();
        } else {
            mTextureView.setSurfaceTextureListener(this);
        }

    }


    // Open Camera

    private void openCamera() {
        CameraManager cameraManager = mCamera.CameraManager_1(this);
        String cameraId = mCamera.CameraCharacteristics_2(cameraManager);
        mCamera.CameraDevice_3(cameraManager, cameraId);
    }

    @Override
    public void onCameraDeviceOpened(CameraDevice cameraDevice, Size cameraSize) {
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        texture.setDefaultBufferSize(cameraSize.getWidth(), cameraSize.getHeight());
        Surface surface = new Surface(texture);

        mCamera.CaptureSession_4(cameraDevice, surface);
        mCamera.CaptureRequest_5(cameraDevice, surface);
    }

    @Override
    protected void onResume() {
        super.onResume();
/*
        if (mTextureView.isAvailable()) {
            openCamera();
        } else {
            mTextureView.setSurfaceTextureListener(this);
        }

 */
    }

    /* Surface Callbacks */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    // Close Camera

    private void closeCamera() {
        mCamera.closeCamera();
    }

    @Override
    protected void onPause() {
        closeCamera();
        super.onPause();
    }
}