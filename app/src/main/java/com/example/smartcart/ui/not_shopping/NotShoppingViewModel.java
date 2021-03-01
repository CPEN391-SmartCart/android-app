package com.example.smartcart.ui.not_shopping;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotShoppingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NotShoppingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the not shopping fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}