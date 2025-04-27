package com.example.pointmobileproject;


import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

    public void changeDirectory(String folderName) {
        try{
            sendCommand("CWD "+ folderName);
            String response = readResponse();
            if (response.startsWith("250")) {
                Log.d("FTP","디렉토리 이동 성공");
            } else {
                Log.d("FTP","디렉토리 이동 실패" + response);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean uploadFile(String fileName, String content) {
        try {
            connect();

            // 1. PASV 명령 전송 (데이터 연결 준비)
            sendCommand("PASV");
            String pasvResponse = readResponse();

            // 2. 데이터 포트 파싱
            int start = pasvResponse.indexOf('(') + 1;
            int end = pasvResponse.indexOf(')');
            String responseIpPort = pasvResponse.substring(start, end);

            String[] ipPort = responseIpPort.split(",");

            String ip = String.join(".", ipPort[0], ipPort[1], ipPort[2], ipPort[3]);
            //FTP 서버는 port를 상위 8 + 하위 8로 나누어서 보냄
            int port = Integer.parseInt(ipPort[4]) * 256 + Integer.parseInt(ipPort[5]);

            // 3. 데이터 소켓 연결
            Socket dataSocket = new Socket(ip, port);
            BufferedOutputStream dataOutputStream = new BufferedOutputStream(dataSocket.getOutputStream());

            // 4. STOR 명령 전송 (업로드할 파일 이름 전송)
            createDirectory();

            sendCommand("STOR Test/" + fileName);
            String response = readResponse();

            if (!response.startsWith("150")) {
                dataSocket.close();
                return false;
            }

            // 5. 텍스트 내용을 바이트 배열로 전송
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytesRead);
            }

            // 6. 데이터 소켓과 파일 스트림 닫기
            inputStream.close();
            dataOutputStream.flush();
            dataOutputStream.close();
            dataSocket.close();

            // 7. 업로드 완료 응답 처리
            response = readResponse(); // 226 Transfer complete
            disconnect();
            return response.startsWith("226");
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
            return false;
        }
    }

    // 폴더 생성 Test (없으면 생성필요) -> 지금은 있지만 없을 경우
    private void createDirectory() {
        try {
            // MKD 명령 전송 (폴더 생성)
            sendCommand("MKD " + "Test");
            readResponse(); // 응답 확인
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getFileList() {
        List<String> fileList = new ArrayList<>();
        try {
            // 1. PASV 명령 전송
            sendCommand("PASV");
            String pasvResponse = readResponse();

            // 2. 데이터 포트 파싱
            int start = pasvResponse.indexOf('(') + 1;
            int end = pasvResponse.indexOf(')');
            String responseIpPort = pasvResponse.substring(start, end);

            String[] ipPort = responseIpPort.split(",");

            String ip = String.join(".", ipPort[0], ipPort[1], ipPort[2], ipPort[3]);
            int port = Integer.parseInt(ipPort[4]) * 256 + Integer.parseInt(ipPort[5]);

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
        }
        return fileList;
    }

    public boolean connect() {
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

    public void disconnect() {
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
