package com.example.testapplication155;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.testapplication155.ui.dashboard.DashboardFragment;
import com.example.testapplication155.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.testapplication155.databinding.ActivityMainBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // stuff for camera
    public ProcessCameraProvider cameraProvider;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Camera camera;
    private ImageCapture imageCapture;
    private ImageProxy imageProxy;
    public PreviewView previewView;

    private TextView translatedTv;

    private static final String key = "AIzaSyClSfDF8l5HGXYaUbz1I9AjtmMBTsZgZDA";
    private static final String postUrl = "https://translation.googleapis.com/language/translate/v2?key=" + key;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);;

    private Context context;

    public Image img;
    public String originalText = "Go to the previous screen to translate text";
    public String translatedText = "Translated text will appear here";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView textView = (TextView) findViewById(R.id.text_dashboard);
        textView.setMovementMethod(new ScrollingMovementMethod());

        // handling the camera stuff
        imageCapture = new ImageCapture.Builder()
                .build();

        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        // creates the camera provider, used for the preview and images
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                //bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
                System.out.println("theres an error anyway");
            }
        }, ContextCompat.getMainExecutor(this));


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void translatebutton(View view) {
        // Do something in response to button click
        TextView tview = findViewById(R.id.translateview);
        translate("hola, como estas");
        //tview.setText("button pressed");
    }


    public void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
    }

    public void takePicture(View view) {
        cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, imageCapture);

        // close the image before taking a new one
        if (imageProxy != null) {
            imageProxy.close();
        }
        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy imageP) {
                System.out.println("took image");
                imageProxy = imageP;
                // from here call things to transcribe and translate things
                handleImage(imageP);
                super.onCaptureSuccess(imageP);

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                System.out.println(exception);
                super.onError(exception);
            }
        });

    }

    @SuppressLint("UnsafeOptInUsageError")
    void handleImage (ImageProxy imageP) {
        // old method for transcribing image
        //transcribe.analyzeImage(imageP.getImage(), imageP.getImageInfo().getRotationDegrees());

        img = imageP.getImage();

        InputImage image = InputImage.fromMediaImage(imageP.getImage(), imageP.getImageInfo().getRotationDegrees());

        // gets
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // call translate on success
                                System.out.println("img processed");
                                translate(visionText.getText());


                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("transcription failed");
                                    }
                                });

        //System.out.println(originalText);
    }

    void translate (String inputText) {
        originalText = inputText;
        Log.d("Translation", inputText);
        try {
            String textToTranslate = inputText;

            // Serialize request
            Gson gson = new Gson();
            String[] text = {textToTranslate};
            String postBody = gson.toJson(new TranslateRequest(text, "en"));

            Log.d("Translation", postBody);

            postRequest(postUrl,postBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

                // Updates translated text box
                Log.d("Translation", translations[0]);

                translatedText = translations[0];
                //TextView view = findViewById(R.id.translateview);
                //view.setText(translations[0]);
            }
        });
    }

}