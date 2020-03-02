package com.danielkim.soundrecorder.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.danielkim.soundrecorder.Helper;
import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.RecordingItem;
import com.danielkim.soundrecorder.WrapContentLinearLayoutManager;
import com.danielkim.soundrecorder.activities.MainActivity;
import com.danielkim.soundrecorder.activities.SettingsActivity;
import com.danielkim.soundrecorder.adapters.FileViewerAdapter;
import com.danielkim.soundrecorder.listeners.FolderListener;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * Created by Daniel on 12/23/2014.
 */
public class FileViewerFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = "FileViewerFragment";

    private int position;
    private FileViewerAdapter mFileViewerAdapter;
    private FolderListener listener;
    private LinearLayout llUploadFile;

    public static FileViewerFragment newInstance(int position) {
        FileViewerFragment f = new FileViewerFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        observer.startWatching();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FolderListener) {
            this.listener = (FolderListener) activity;
        }
    }

    @Override
    public void onDetach() {
        this.listener = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);

        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        WrapContentLinearLayoutManager llm = new WrapContentLinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //newest to oldest order (database stores from oldest to newest)
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        llUploadFile = (LinearLayout) v.findViewById(R.id.llUploadFile);
        mFileViewerAdapter = new FileViewerAdapter(getActivity(), llm, listener);
        mRecyclerView.setAdapter(mFileViewerAdapter);

//        File file = new File(Helper.getAppFolderPath());
//        File[] files = file.listFiles(new FileFilter() {
//            @Override
//            public boolean accept(File pathname) {
//                return pathname.isDirectory();
//            }
//        });
//        ArrayList<RecordingItem> items = new  ArrayList<>();
//        for(File f : files){
//            items.add(new RecordingItem(f.getName()));
//        }
//        mFileViewerAdapter.addList(items);

        llUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mFileViewerAdapter.getSelectedFolders().isEmpty()) {
                    Toast.makeText(getActivity(), "No folders selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                UploadFileFragment fragment = UploadFileFragment.newInstance(mFileViewerAdapter.getSelectedFolders());
                fragment.show(((MainActivity) getActivity()).getSupportFragmentManager(), "dialog_upload_file");
                llUploadFile.setVisibility(View.GONE);
                listener.folderChecked(false);
            }
        });
        return v;
    }

    FileObserver observer =
            new FileObserver(Helper.getAppFolderPath()) {
                // set up a file observer to watch this directory on sd card
                @Override
                public void onEvent(int event, String file) {
                    if (event == FileObserver.DELETE) {
                        // user deletes a recording file out of the app

                        String filePath = Helper.getAppFolderPath() + file + "]";

                        Log.d(LOG_TAG, "File deleted ["
                                + Helper.getAppFolderPath() + file + "]");

                        // remove file from database and recyclerview
                        mFileViewerAdapter.removeOutOfApp(filePath);
                    }
                }
            };

    public void goBackFolder() {
        if (mFileViewerAdapter != null)
            mFileViewerAdapter.goBackFolder();
    }

    public void setFolderChecked(Boolean folderChecked) {
        if (folderChecked) {
            llUploadFile.setVisibility(View.VISIBLE);
        } else {
            llUploadFile.setVisibility(View.GONE);
        }
    }
}




