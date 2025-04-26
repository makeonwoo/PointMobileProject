package com.example.pointmobileproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnUpload;
    private Button btnGetList;

    private TextView txtResult;

    private FTPManager ftpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUpload = findViewById(R.id.btnUpload);
        btnGetList = findViewById(R.id.btnGetList);

        txtResult = findViewById(R.id.txtResult);

        ftpManager = new FTPManager();

        btnGetList.setOnClickListener(v -> {
            new Thread(() -> {
                List<String> files = ftpManager.getFileList();
                runOnUiThread(() -> {
                    if (files.isEmpty()) {
                        Toast.makeText(this, "목록 없음 또는 실패", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("서버 파일 목록");
                        builder.setItems(files.toArray(new String[0]), null);
                        builder.setPositiveButton("닫기", null);
                        builder.show();
                    }
                });
            }).start();
        });

        String fileName = "text1.txt";
        String fileContent = "test 1";

        btnUpload.setOnClickListener(v -> {
            new Thread(() -> {
                ftpManager.uploadFile(fileName,fileContent);
            }).start();
        });
    }
}