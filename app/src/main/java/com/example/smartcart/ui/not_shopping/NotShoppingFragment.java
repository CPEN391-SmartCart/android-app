package com.example.smartcart.ui.not_shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartcart.R;

public class NotShoppingFragment extends Fragment {

    private NotShoppingViewModel notShoppingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notShoppingViewModel =
                new ViewModelProvider(this).get(NotShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_not_shopping, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        final TextView textView = root.findViewById(R.id.text_not_shopping);
        notShoppingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}