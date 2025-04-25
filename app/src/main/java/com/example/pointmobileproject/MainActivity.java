package com.example.pointmobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnConnect;
    private Button btnDisConnect;
    private TextView txtResult;

    private FTPManager ftpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btnConnect);
        btnDisConnect = findViewById(R.id.btnDisConnect);
        txtResult = findViewById(R.id.txtResult);

        ftpManager = new FTPManager();


        btnConnect.setOnClickListener(v -> {
            new Thread(() -> {
                boolean result = ftpManager.connect();
                runOnUiThread(() -> Toast.makeText(this,
                        result ? "연결 성공" : "연결 실패", Toast.LENGTH_SHORT).show());
            }).start();
        });

        btnDisConnect.setOnClickListener(v -> {
            new Thread(() -> {
                ftpManager.disconnect();
                runOnUiThread(() -> Toast.makeText(this,
                         "연결 해제", Toast.LENGTH_SHORT).show());
            }).start();
        });
    }
}