package com.example.qrcodereader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class MainActivity extends AppCompatActivity {

    private int CAMERA_REQUEST_CODE = 101;

    private CodeScanner codeScanner;
    private TextView textResult;

    private void startScanner()
    {
        CodeScannerView codeScannerView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(this, codeScannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        textResult.setText(result.getText());
                        //http://www.google.com
                        if(result.getText().contains("http") || result.getText().contains("https"))
                        {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getText()+""));
                            startActivity(browserIntent);
                        }else{
                            Toast.makeText(MainActivity.this, "Url non valido", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textResult = findViewById(R.id.textResult);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }else if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            startScanner();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startScanner();
            }else{
                textResult.setText("Accesso alla fotocamera negato");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(codeScanner != null)
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        if(codeScanner != null)
        codeScanner.releaseResources();
        super.onPause();
    }
}