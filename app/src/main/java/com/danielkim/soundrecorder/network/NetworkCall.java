package com.danielkim.soundrecorder.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface NetworkCall {
    @Multipart
    @POST("uploadAudioDataFiles")
    Call<String> uploadAudioDataFiles(@Part MultipartBody.Part file);

    @POST("getDataFileList")
    Call<String> getFileList();

    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
