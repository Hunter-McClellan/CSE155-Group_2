package com.example.translationapp;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.*;
import androidx.camera.video.VideoCapture;
import androidx.core.content.PermissionChecker;
import androidx.lifecycle.LifecycleOwner;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import android.provider.MediaStore;
import android.media.Image;

public class MainActivity extends AppCompatActivity {

    private EditText inputToTranslate;
    private TextView translatedTv;
    private String originalText;
    private String translatedText;


    private static final String key = "AIzaSyClSfDF8l5HGXYaUbz1I9AjtmMBTsZgZDA";
    private static final String postUrl = "https://translation.googleapis.com/language/translate/v2?key=" + key;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // camera vars
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        // camera handling from here on out
        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
                System.out.println("camera should be working now");
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
                System.out.println("Theres an issue");
            }
        }, ContextCompat.getMainExecutor(this));




        // update this to use real layout later
        /*inputToTranslate = findViewById(R.id.inputToTranslate);
        translatedTv = findViewById(R.id.translatedTv);
        Button translateButton = findViewById(R.id.translateButton);



        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String textToTranslate = inputToTranslate.getText().toString();

                    // Serialize request
                    Gson gson = new Gson();
                    String[] text = {textToTranslate};
                    String postBody = gson.toJson(new TranslateRequest(text, "en"));

                    Log.d("MyActivity", postBody);

                    postRequest(postUrl,postBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/


    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
    }


    void postRequest(String postUrl,String postBody) throws IOException {
        // Create request and post to Google Cloud Translate API
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(postBody, JSON);
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Convert response body to string
                String responseBody = response.body().string();

                // Deserialize JSON
                Gson gson = new Gson();
                TranslationResponse res = gson.fromJson(responseBody, TranslationResponse.class);
                String[] translations = res.getData().getTranslatedStrings();
                // Updates translated text box from UIThread
                translatedTv.post(new Runnable() {
                                      @Override
                                      public void run() {
                                          translatedTv.setText(translations[0]);
                                      }
                                  });
            }
        });
    }
}