package com.example.smartcart.ui.camera;

import android.Manifest;
import android.app.Activity;
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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.smartcart.HomeActivity;
import com.example.smartcart.R;
import com.example.smartcart.ui.shopping.ShoppingListItem;
import com.example.smartcart.ui.shopping.ShoppingViewModel;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

public class CameraFragment extends Fragment {

    private ShoppingViewModel shoppingViewModel;
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
        shoppingViewModel =
                new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
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

        /*
        HomeActivity.btt.addItemChangedCallback(item -> {
            // add to camera view list
            previouslyScannedBarcodes.addLast(item.name_);
            while(previouslyScannedBarcodes.size() > PREVIOUSLY_SCANNED_BARCODE_QUEUE_SIZE) {
                previouslyScannedBarcodes.poll();
            }
            int i = 0;
            for(String barcode : previouslyScannedBarcodes) {
                previouslyScannedViews.get(i).setText(barcode);
                i++;
            }

            if(item.byWeight_) {
                Bundle bundle = new Bundle();
                bundle.putString("itemName", item.name_);
                bundle.putDouble("itemPricePerGrams", item.price_);
                Activity act = this.getActivity();
                NavController navController = Navigation.findNavController(this.getActivity(), R.id.nav_host_fragment);
                if (navController.getCurrentDestination().getId() == R.id.navigation_camera) {
                    navController.navigate(R.id.action_navigation_camera_to_navigation_weight, bundle);
                }
            }
            else {
                HomeActivity.btt.write("ic:" + item.price_);
                shoppingViewModel.addShoppingListItem(new ShoppingListItem(1, item.name_, item.price_));
                Toast.makeText(getContext(), "Added " + item.name_ + " to your cart", Toast.LENGTH_SHORT).show();
            }
        });
         */

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


        // adapted from https://medium.com/analytics-vidhya/creating-a-barcode-scanner-using-android-studio-71cff11800a2
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

        HomeActivity.bluetooth.send("sc:" + barcodeData);
//        HomeActivity.btt.clearLastLookupItem();
//        HomeActivity.btt.write("sc:" + barcodeData);

        //HomeActivity.handleReadMessage("in:Apple");
        //HomeActivity.handleReadMessage("pw:129");
    }
}