package com.example.medidordeparametrosacusticos.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.medidordeparametrosacusticos.models.AudioRecorder;
import com.example.medidordeparametrosacusticos.repositories.Repository;

import java.util.ArrayList;

public class SharedViewModel extends AndroidViewModel {
    private Repository mRepository;
    private MutableLiveData<ArrayList<String>> mResults;
    private MutableLiveData<ArrayList<String>> mFileList;
    private MutableLiveData<Boolean> mIsRecording;

    public SharedViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadCurrentMeasures() {
        if (mFileList != null) {
            return;
        }
        mRepository = Repository.getInstance(getApplication());
        mFileList = mRepository.getFileList();
        mResults = mRepository.getResults();
    }

    public void initRecorder(final AudioRecorder audioRecorder) {
        audioRecorder.startRecording();
        mIsRecording = audioRecorder.getStatus();
    }

    public LiveData<ArrayList<String>> getFileList() {
        return mFileList;
    }

    public LiveData<ArrayList<String>> getResults() {
        return mResults;
    }

    public LiveData<Boolean> getIsRecording() {
        return mIsRecording;
    }

}
