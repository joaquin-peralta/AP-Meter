package com.example.medidordeparametrosacusticos.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.medidordeparametrosacusticos.models.AudioProcessor;
import com.example.medidordeparametrosacusticos.models.AudioRecorder;
import com.example.medidordeparametrosacusticos.repositories.Repository;

import java.util.ArrayList;

public class SharedViewModel extends AndroidViewModel {
    private Repository mRepository;
    private AudioRecorder mRecorder;
    private AudioProcessor mAudioProcessor;
    private MutableLiveData<ArrayList<String>> mResults;
    private MutableLiveData<ArrayList<String>> mFileList;
    private MutableLiveData<Boolean> mIsRecording;
    private MutableLiveData<ArrayList<Short>> mAudioData;
    private MutableLiveData<ArrayList<ArrayList<Double>>> mReverbTimes;

    public SharedViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        if (mFileList == null) {
            mRepository = Repository.getInstance(getApplication());
            mFileList = mRepository.getFileList();
            mResults = mRepository.getResults();
        }
        if (mIsRecording == null) {
            mRecorder = AudioRecorder.getInstance();
            mIsRecording = mRecorder.getIsRecording();
            mAudioData = mRecorder.getAudioData();
        }
        mAudioProcessor = AudioProcessor.getInstance();
        mReverbTimes = mAudioProcessor.getReverbTimes();
    }

    public void initRecorder() {
        mRecorder.startRecording();
    }

    public void finishRecorder() {
        mRecorder.stopRecording();
    }

    public void processData(ArrayList<Short> shorts) {
        mAudioProcessor.process(shorts);
    }

    public void saveData(ArrayList<ArrayList<Double>> reverbTimes) {
        double[][] data = new double[reverbTimes.size()][reverbTimes.get(0).size()];
        for (int i = 0; i < reverbTimes.size() ; i++) {
            for (int j = 0; j < reverbTimes.get(i).size() ; j++) {
                data[i][j] = reverbTimes.get(i).get(j);
            }
        }
        mRepository.save(data);
    }

    public void deleteData(int position) {
        mRepository.delete(position);
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

    public LiveData<ArrayList<Short>> getAudioData() {
        return mAudioData;
    }

    public LiveData<ArrayList<ArrayList<Double>>> getReverbTimes() {
        return mReverbTimes;
    }

}
