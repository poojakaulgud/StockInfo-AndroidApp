package com.example.assgn4;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Integer> deletePosition = new MutableLiveData<>();

    public void deleteItem(int position) {
        deletePosition.setValue(position);
    }

    public LiveData<Integer> getDeletePosition() {
        return deletePosition;
    }
}

