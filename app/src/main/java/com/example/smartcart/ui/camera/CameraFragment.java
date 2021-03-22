package com.example.smartcart.ui.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.smartcart.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class CameraFragment extends Fragment {

    private CameraViewModel cameraViewModel;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cameraViewModel =
                new ViewModelProvider(this).get(CameraViewModel.class);
        View root = inflater.inflate(R.layout.fragment_camera, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = root.findViewById(R.id.surface_view);
        barcodeText = root.findViewById(R.id.barcode_text);
        autoManualSwitch = root.findViewById(R.id.auto_manual);
        captureButton = root.findViewById(R.id.capture);
        previouslyScannedViews = new ArrayList<TextView>();
        previouslyScannedViews.add(root.findViewById(R.id.previous_scanned3));
        previouslyScannedViews.add(root.findViewById(R.id.previous_scanned2));
        previouslyScannedViews.add(root.findViewById(R.id.previous_scanned));
        previouslyScannedBarcodes = new LinkedList<String>() ;
        barcodeData = "";
        initialiseDetectorsAndSources();
        return root;
    }

    private void initialiseDetectorsAndSources() {

        captureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                processNewBarcode();
            }
        });

        barcodeDetector = new BarcodeDetector.Builder(requireActivity())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(requireActivity(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

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

    private void processNewBarcode()
    {
        Toast.makeText(getContext(), "Scanned barcode " + barcodeData, Toast.LENGTH_SHORT).show();
        previouslyScannedBarcodes.addLast(barcodeData);
        while(previouslyScannedBarcodes.size() > PREVIOUSLY_SCANNED_BARCODE_QUEUE_SIZE) {
            previouslyScannedBarcodes.poll();
        }
        int i = 0;
        for(String barcode : previouslyScannedBarcodes) {
            previouslyScannedViews.get(i).setText(barcode);
            i++;
        }
        if(barcodeData.toLowerCase().contains("apple")) {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_navigation_camera_to_weight_fragment);
        }
        else {
            Toast.makeText(getContext(), "Added " + barcodeData + " to your cart", Toast.LENGTH_SHORT).show();
        }
    }
}