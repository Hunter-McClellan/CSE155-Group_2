package com.example.translationapp;

import android.content.Context;
import android.graphics.Point;
import android.media.Image;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
//import com.google.mlkit.vision.demo.GraphicOverlay;
//import com.google.mlkit.vision.demo.java.VisionProcessorBase;
//import com.google.mlkit.vision.demo.preference.PreferenceUtils;
import com.google.mlkit.vision.text.*;
import com.google.mlkit.vision.text.Text.Element;
import com.google.mlkit.vision.text.Text.Line;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import java.util.List;

public class TextFromImage {
    private final TextRecognizer recognizer;

    private String result_text;

    public TextFromImage() {
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    // haven't tested this but it should work in theory
    public String analyzeImage (Image mediaImage) {
        // this may need to be changed later to fit the input from the camera
        InputImage image = InputImage.fromMediaImage(mediaImage, 0);

        // gets
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                 result_text = visionText.getText();
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });


        return result_text;
    }
}
