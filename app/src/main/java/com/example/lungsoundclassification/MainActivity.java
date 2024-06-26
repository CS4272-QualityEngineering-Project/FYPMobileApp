package com.example.lungsoundclassification;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout progressBarOverlay;

    private static final int FILE_PICKER_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBarOverlay = findViewById(R.id.progress_bar_overlay);


        findViewById(R.id.up_upload_audio_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                openFilePicker();

            }
        });
    }

    // TODO: TEST - DONE (UI test)
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/x-wav");
        Intent chooserIntent = Intent.createChooser(intent, "Select WAV audio");
        startActivityForResult(chooserIntent, FILE_PICKER_REQUEST_CODE);
    }


    // TODO: TEST - SEEMS DIFFICULT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the selected file's URI
            Uri fileUri = data.getData();
            // Check if the file is accessible
            if (isFileAccessible(fileUri, this)) {
                // Read the data from the file
                byte[] wavData = readDataFromFile(fileUri, this);
                if (wavData == null) {
                    Toast.makeText(MainActivity.this, "Error reading file", Toast.LENGTH_SHORT).show();
                    return;
                }
                //  Create a RequestBody from the data
                RequestBody requestFile = createRequestBody(wavData, fileUri, this);
                // Send the data to the server
                sendWavDataToServer(requestFile);
            } else {
                Toast.makeText(MainActivity.this, "File is not accessible", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // TODO: TEST - DONE
    public static byte[] readDataFromFile(Uri fileUri, Context _context) {
        byte[] wavData = null;

        try {
            // Create an InputStream from the URI
            InputStream inputStream = _context.getContentResolver().openInputStream(fileUri);

            // Create a ByteArrayOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // Create a buffer for reading data
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Read data from the InputStream and write it to the ByteArrayOutputStream
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // Close the InputStream
            inputStream.close();

            // Get the byte array
            wavData = byteArrayOutputStream.toByteArray();

            // Close the ByteArrayOutputStream
            byteArrayOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return wavData;
    }

    // TODO: TEST - DONE
    protected static   RequestBody createRequestBody(byte[] wavData, Uri fileUri, Context _context) {
        if (wavData == null) {
            return null;
        }
        String mimeType = _context.getContentResolver().getType(fileUri);
        if (mimeType == null) {
            return  null;
        }
        return RequestBody.create(MediaType.parse(mimeType), wavData);
    }

    // TODO: TEST - DONE (WITHOUT ASYNC)
    protected void sendWavDataToServer(RequestBody requestFile) {
        RetrofitAPICall apiService = RetrofitClient.getRetrofitInstance().create(RetrofitAPICall.class);
        Call<ResponseObject> call = apiService.sendWav(requestFile);
        showProgressBar();

        // async call
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, retrofit2.Response<ResponseObject> response) {
                RetrofitResponseWrapper responseWrapper = new RetrofitResponseWrapper(response.isSuccessful(), response.body());
                handleResponse(responseWrapper);
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                hideProgressBar();
                Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: TEST
    protected void handleResponse(RetrofitResponseWrapper response) {
        if(response.isSuccessful()){
            hideProgressBar();
            Toast.makeText(MainActivity.this, "Request successful", Toast.LENGTH_SHORT).show();
            ResponseObject responseObject = response.body();
            Intent intent = new Intent(MainActivity.this, DiagnosisActivity.class);
            // TODO: remove this (why?)
            intent.putExtra("response_object", responseObject);
            startActivity(intent);
        } else {
            hideProgressBar();
            Toast.makeText(MainActivity.this, "Request unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: TEST - DONE
    public static boolean isFileAccessible(Uri uri, Context _context) {
        try {
            // Open the file using FileInputStream
//            FileInputStream inputStream = new FileInputStream(_context.getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor());

            InputStream inputStream = _context.getContentResolver().openInputStream(uri);

            // Check if the file is open and ready for reading
            if (inputStream.available() > 0) {
                inputStream.close();
                return true;
            } else {
                inputStream.close();
                return false;
            }
        } catch (IOException e) {
            // Handle the IOException
            e.printStackTrace();
            return false;
        }
    }

    private void showProgressBar() {
        progressBarOverlay.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBarOverlay.setVisibility(View.GONE);
    }
}
