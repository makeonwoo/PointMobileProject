package com.example.pointmobileproject;


import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FTPManager {
    // server 정보
    static final String FTP_HOST = "file.pointmobile.co.kr";
    static final int FTP_PORT = 21;

    // user 정보
    static final String FTP_USER = "payment";
    static final String FTP_PASS = "PMpayment123!";

    private Socket ftpSocket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public List<String> getFileList() {
        List<String> fileList = new ArrayList<>();
        try {
            connect();

            // 1. PASV 명령 전송
            sendCommand("PASV");
            String pasvResponse = readResponse();

            // 2. 데이터 포트 파싱
            String ipPortPart = pasvResponse.substring(pasvResponse.indexOf('(') + 1, pasvResponse.indexOf(')'));
            String[] parts = ipPortPart.split(",");
            String ip = String.join(".", Arrays.copyOfRange(parts, 0, 4));
            int port = (Integer.parseInt(parts[4]) << 8) + Integer.parseInt(parts[5]);

            // 3. 데이터 소켓 연결
            Socket dataSocket = new Socket(ip, port);
            BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

            // 4. LIST 명령 전송
            sendCommand("LIST");
            readResponse();

            // 5. 데이터 읽기
            String line;
            while ((line = dataReader.readLine()) != null) {
                fileList.add(line);
            }

            dataReader.close();
            dataSocket.close();

            readResponse();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return fileList;
    }

    private boolean connect() {
        try {
            // 소켓 생성하고 버퍼 연결
            ftpSocket = new Socket(FTP_HOST, FTP_PORT);
            reader = new BufferedReader(new InputStreamReader(ftpSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(ftpSocket.getOutputStream()));

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

    private void disconnect() {
        try {
            if (writer != null) sendCommand("QUIT");
            if (ftpSocket != null) ftpSocket.close();
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
