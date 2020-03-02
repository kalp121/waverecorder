package com.danielkim.soundrecorder.asyncs;

import android.os.AsyncTask;

import com.danielkim.soundrecorder.network.APIClient;
import com.danielkim.soundrecorder.Helper;
import com.danielkim.soundrecorder.listeners.ZipFileCallbackListener;
import com.danielkim.soundrecorder.network.NetworkCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class ZipFileAsync extends AsyncTask<Void, Void, String> {

    private int BUFFER = 1024;
    private String outputFile;
    private ArrayList<String> selectedFolders;
    private ZipFileCallbackListener listener;
    private String userName;


    public ZipFileAsync(String outputFile, ArrayList<String> selectedFolders, ZipFileCallbackListener listener, String userName) {
        this.outputFile = outputFile;
        this.selectedFolders = selectedFolders;
        this.listener = listener;
        this.userName = userName;
    }

    @Override
    protected String doInBackground(Void... voids) {


        try {
            FileOutputStream dest = new FileOutputStream(outputFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            for (String str : selectedFolders) {
                final File file = new File(Helper.getAppFolderPath() + "/" + str);
                zip(file, out);
            }
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "Something went wrong, while checking files";
        } catch (IOException e) {
            e.printStackTrace();
            return "Something went wrong, while checking files";
        }

        final File f = new File(outputFile);
        if (f.exists()) {
            try {
                Response<String> response = uploadDataFile(f.getPath()).execute();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONObject response1 = new JSONObject(jsonObject.getString("entity"));
                        String response2 = response1.getString("response").toString();
                        if (response2.equalsIgnoreCase("Success")) {
                            f.delete();
                            return "File uploaded successfully";
                        } else {
                            return "Something went wrong from server";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Something went wrong with data";
                    }
                } else {
                    return "Something went wrong, While uploading file";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Something went wrong";
            }
        } else {
            return "Something went wrong";
        }
    }

    @Override
    protected void onPostExecute(String message) {
        super.onPostExecute(message);
        listener.onFinishZipProcess(message);
    }

    private void zip(File file, ZipOutputStream out) {
        try {

            if (file.isDirectory()) {
                zipFolder(file, out);
            } else {
                zipFile(file, out);
            }
            file.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void zipFolder(File folder, ZipOutputStream out) throws Exception {
        String folderZip = folder.getParent() + "/" + folder.getName() + "_" + folder.getParentFile().getName() + ".zip";
        FileOutputStream dest = new FileOutputStream(folderZip);
        ZipOutputStream outLocal = new ZipOutputStream(new BufferedOutputStream(dest));

        for (String fileName : folder.list()) {
            zip(new File(folder.getPath() + "/" + fileName), outLocal);
        }
        outLocal.close();
        zip(new File(folderZip), out);
    }

    private void zipFile(File file, ZipOutputStream out) throws Exception {
        FileInputStream fi = new FileInputStream(file);
        BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
        ZipEntry entry = new ZipEntry(file.getName());
        out.putNextEntry(entry);

        int count;
        byte data[] = new byte[BUFFER];
        while ((count = origin.read(data, 0, BUFFER)) != -1) {
            out.write(data, 0, count);
        }
        origin.close();
    }


    public Call<String> uploadDataFile(String filePath) {
        NetworkCall service = APIClient.getClient().create(NetworkCall.class);
        return service.uploadAudioDataFiles(createMultipartRequestBody(filePath));
    }

    private MultipartBody.Part createMultipartRequestBody(String filePath) {
        File file = new File(filePath);
        RequestBody requestBody = createRequestBody(file);
        return MultipartBody.Part.createFormData("dataFile", userName + "_" + file.getName(), requestBody);
    }

    private RequestBody createRequestBody(File file) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }
}
