package com.danielkim.soundrecorder.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.danielkim.soundrecorder.DBHelper;
import com.danielkim.soundrecorder.Helper;
import com.danielkim.soundrecorder.MySharedPreferences;
import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.asyncs.ZipFileAsync;
import com.danielkim.soundrecorder.listeners.ZipFileCallbackListener;

import java.util.ArrayList;

public class UploadFileFragment extends DialogFragment implements ZipFileCallbackListener {

    private ArrayList<String> selectedFolders = new ArrayList<>();
    String outputFile = Helper.getAppFolderPath() + "/" + String.valueOf(System.currentTimeMillis()) + ".zip";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        selectedFolders = getArguments().getStringArrayList("selectedFolders");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new ZipFileAsync(outputFile, selectedFolders, this, MySharedPreferences.getUserName(getActivity())).execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        if (activity instanceof BlankFragment.OnFragmentInteractionListener) {
//            mListener = (BlankFragment.OnFragmentInteractionListener) activity;
//        } else {
//            throw new RuntimeException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    public static UploadFileFragment newInstance(ArrayList<String> selectedFolders) {
        UploadFileFragment fileFragment = new UploadFileFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedFolders", selectedFolders);
        fileFragment.setArguments(bundle);
        return fileFragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onFinishZipProcess(String message) {
        Log.e("ZipMessage", message);
        if (message.contains("File uploaded successfully")) {
            final DBHelper database = new DBHelper(getActivity());
            database.deleteAllFilesFromFolder(selectedFolders);
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
