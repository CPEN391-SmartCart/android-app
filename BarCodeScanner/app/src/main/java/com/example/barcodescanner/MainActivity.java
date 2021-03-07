package com.example.barcodescanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {


    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private static final int PREVIOUSLY_SCANNED_BARCODE_QUEUE_SIZE = 3;
    private static final long AUTOMATIC_BARCODE_SCAN_DELAY_MS = 1000;
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private Switch autoManualSwitch;
    private FloatingActionButton captureButton;
    private ArrayList<TextView> previouslyScannedViews;
    private LinkedList<String> previouslyScannedBarcodes;
    private String barcodeData;
    private long lastBarcodeUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);
        autoManualSwitch = findViewById(R.id.auto_manual);
        captureButton = findViewById(R.id.capture);
        previouslyScannedViews = new ArrayList<TextView>();
        previouslyScannedViews.add(findViewById(R.id.previous_scanned3));
        previouslyScannedViews.add(findViewById(R.id.previous_scanned2));
        previouslyScannedViews.add(findViewById(R.id.previous_scanned));
        previouslyScannedBarcodes = new LinkedList<String>() ;
        barcodeData = "";
        initialiseDetectorsAndSources();
    }

    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        captureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                barcodeData += "X";
                Toast.makeText(getApplicationContext(), "Barcode" + barcodeData, Toast.LENGTH_SHORT).show();
                processNewBarcode();

            }
        });

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new
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
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
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
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                            } else {

                                barcodeData = barcodes.valueAt(0).displayValue;
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                            }
                            barcodeText.setText(barcodeData);
                            if(autoManualSwitch.isChecked() && (System.currentTimeMillis()-lastBarcodeUpdate>AUTOMATIC_BARCODE_SCAN_DELAY_MS)) {
                                processNewBarcode();
                                lastBarcodeUpdate = System.currentTimeMillis();
                            }

                        }
                    });

                }
            }
        });
    }

    private void transitionToWeightActivity()
    {
        Intent intent = new Intent(this, WeightActivity.class);
        startActivity(intent);

    }

    private void processNewBarcode()
    {
        previouslyScannedBarcodes.addLast(barcodeData);
        while(previouslyScannedBarcodes.size()>PREVIOUSLY_SCANNED_BARCODE_QUEUE_SIZE)
        {
            previouslyScannedBarcodes.poll();
        }
        int i =0;
        for(String barcode : previouslyScannedBarcodes)
        {
            previouslyScannedViews.get(i).setText(barcode);
            i++;
        }
        transitionToWeightActivity();
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

}