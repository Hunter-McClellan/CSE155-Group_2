package com.example.translationapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executors;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.*;
import androidx.camera.video.VideoCapture;
import androidx.core.content.PermissionChecker;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import android.provider.MediaStore;
import android.media.Image;


public class CameraHandler {
    private ImageCapture imageCapture;
    private Camera camera;
    private ImageProxy imagep;
    public Image image; // actual image that needs to be sent to the thing
    public int rot; // rotation of the image

    public void cameraHandler (View view) { // view of the camera preview
        imageCapture = new ImageCapture.Builder()
            .setTargetRotation(view.getDisplay().getRotation())
            .build();

        //cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, imageAnalysis, preview);
    }

}
