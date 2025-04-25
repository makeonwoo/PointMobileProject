package com.example.pointmobileproject;


import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class FTPManager {
    static final String FTP_HOST = "file.pointmobile.co.kr";
    static final int FTP_PORT = 21;

    static final String FTP_USER = "payment";
    static final String FTP_PASS = "PMpayment123!";
    private Socket commandSocket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public boolean connect() {
        try {
            // 소켓 생성하고 버퍼 연결
            commandSocket = new Socket(FTP_HOST, FTP_PORT);
            reader = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(commandSocket.getOutputStream()));

            readResponse();

            sendCommand("USER " + FTP_USER);
            readResponse();

            sendCommand("PASS " + FTP_PASS);
            String response = readResponse();
            return response.startsWith("230"); // 로그인 성공
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() {
        try {
            if (writer != null) sendCommand("QUIT");
            if (commandSocket != null) commandSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendCommand(String cmd) throws IOException {
        Log.d("FTP", "Client: " + cmd);
        writer.write(cmd + "\r\n");
        writer.flush();
    }

    private String readResponse() throws IOException {
        String line = reader.readLine();
        Log.d("FTP", "Server: " + line);
        return line;
    }
}
