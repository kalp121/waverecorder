package com.danielkim.soundrecorder.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.danielkim.soundrecorder.Helper;
import com.danielkim.soundrecorder.MySharedPreferences;
import com.danielkim.soundrecorder.listeners.SelectFileListener;
import com.danielkim.soundrecorder.model.ServerFile;
import com.danielkim.soundrecorder.network.APIClient;
import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.adapters.FileSelecterAdapter;
import com.danielkim.soundrecorder.network.NetworkCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectFilesFragment extends DialogFragment implements SelectFileListener {

    private OnFragmentInteractionListener mListener;
    private FileSelecterAdapter adapter;
    private ProgressBar pbFileList;

    public SelectFilesFragment() {
        // Required empty public constructor
    }

    public static SelectFilesFragment newInstance() {
        SelectFilesFragment fragment = new SelectFilesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(OnFragmentInteractionListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // initialize parameters here
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_files, container, false);
        RecyclerView rvFileList = (RecyclerView) view.findViewById(R.id.rvFileList);
        pbFileList = (ProgressBar) view.findViewById(R.id.pbFileList);
        String path = MySharedPreferences.getSelectedFilePath(getActivity());
        adapter = new FileSelecterAdapter(this, path.substring(path.lastIndexOf("/") + 1));
        rvFileList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFileList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NetworkCall client = APIClient.getClient().create(NetworkCall.class);
        pbFileList.setVisibility(View.VISIBLE);
        client.getFileList().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(response.body());
                        if (responseObject.getString("response").equalsIgnoreCase("success")) {
                            JSONArray filesArray = responseObject.getJSONArray("files");
                            int length = filesArray.length();
                            int i;
                            ArrayList<ServerFile> fileList = new ArrayList<>();
                            JSONObject fileObject;
                            for (i = 0; i < length; i++) {
                                fileObject = new JSONObject(filesArray.get(i).toString());
                                fileList.add(new ServerFile(fileObject.getString("fileName"), fileObject.getString("path")));
                            }
                            fileList.add(0, new ServerFile("Default", ""));
                            adapter.setFilesList(fileList);
                        } else {
                            Toast.makeText(getActivity(), "List retrieval failed", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "List retrieval failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "List retrieval failed due to server problem", Toast.LENGTH_SHORT).show();
                    SelectFilesFragment.this.dismiss();
                }

                pbFileList.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                pbFileList.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "List retrieval failed due to network problem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void fileSelected(final ServerFile serverFile) {
        if (serverFile.fileName.equalsIgnoreCase("Default")) {
            MySharedPreferences.setSelectedFilePath(getActivity(), "");
            MySharedPreferences.setSelectedFileName(getActivity(), "Default");
            MySharedPreferences.setLastReadIndex(getActivity(), 0);
            Toast.makeText(getActivity(), "File selected successfully", Toast.LENGTH_SHORT).show();
            this.dismiss();
            if (mListener != null) {
                mListener.onFragmentInteraction(null);
            }
            return;
        }
        NetworkCall client = APIClient.getClient().create(NetworkCall.class);
        this.setCancelable(false);
        pbFileList.setVisibility(View.VISIBLE);
        client.downloadFile(serverFile.path).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pbFileList.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(), serverFile.path);
                    if (writtenToDisk) {
                        Toast.makeText(getActivity(), "File selected successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong while downloading file", Toast.LENGTH_SHORT).show();
                    }
                    SelectFilesFragment.this.dismiss();
                    if (mListener != null) {
                        mListener.onFragmentInteraction(null);
                    }
                } else {
                    SelectFilesFragment.this.setCancelable(true);
                    Toast.makeText(getActivity(), "Something went wrong with server, while downloading file", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pbFileList.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Something went wrong with network connection, while downloading file", Toast.LENGTH_SHORT).show();
                SelectFilesFragment.this.setCancelable(true);
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String path) {
        try {
            File downloadDirectory = new File(Helper.getDownloadFilePath());
            if (!downloadDirectory.exists()) {
                downloadDirectory.mkdirs();
            }
            File downloadedFile = new File(Helper.getDownloadFilePath() + File.separator + path.substring(path.lastIndexOf("/") + 1));

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(downloadedFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

//                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();


//                Toast.makeText(getActivity(), "Selected file: " + downloadedFile.getName(), Toast.LENGTH_SHORT).show();
                MySharedPreferences.setSelectedFilePath(getActivity(), downloadedFile.getPath());
                MySharedPreferences.setSelectedFileName(getActivity(), downloadedFile.getName());
                MySharedPreferences.setLastReadIndex(getActivity(), 0);


                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

}
