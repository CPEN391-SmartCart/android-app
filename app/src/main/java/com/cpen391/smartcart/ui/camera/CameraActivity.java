package com.cpen391.smartcart.ui.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cpen391.smartcart.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

/**
 * Adapted from: https://medium.com/analytics-vidhya/creating-a-barcode-scanner-using-android-studio-71cff11800a2
 */

/**
 * This activity represents the barcode scanner and camera
 */
public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 201;

    private SurfaceView surfaceView;
    private TextView barcodeText;

    private FloatingActionButton captureButton;

    private CameraSource cameraSource;
    private ToneGenerator toneGenerator;

    private String barcodeData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // set up member variables
        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);
        captureButton = findViewById(R.id.capture);

        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        initialiseDetectorsAndSources();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportActionBar().hide();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
        initialiseDetectorsAndSources();
    }

    /**
     * Initializes the barcode scanner
     */
    private void initialiseDetectorsAndSources()
    {
        captureButton.setOnClickListener((view) -> {
            if (barcodeData != null) {
                Intent intent = new Intent();
                intent.putExtra("BARCODE", barcodeData);
                setResult(2, intent);
                finish();
            }
        });

        // make a new barcode detector
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // we need camera permissions
                try {
                    if (ActivityCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(CameraActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    barcodeText.post(new Runnable() {

                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                            } else {
                                barcodeData = barcodes.valueAt(0).displayValue;
                            }
                            if (!barcodeText.getText().toString().equals(barcodeData)) {
                                // we found a bar code
                                barcodeText.setText(barcodeData);
                                toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            }
                        }
                    });

                }
            }
        });
    }
}
