package com.example.socketserver;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class ServerThread implements Runnable {

    private Context _context;
    public int _serverPort;

    private ServerSocket _serverSocket;
    private Socket _tempClientSocket;

    public ServerThread(Context context, int serverPort) {

        _context = context;
        _serverPort = serverPort;
    }

    public void sendMessage(JSONObject jsonData) {
        try {

            if (null != _tempClientSocket) {
                new Thread(() -> {

                    try {

                        DataOutputStream dataOutputStream = new DataOutputStream(_tempClientSocket.getOutputStream());
                        dataOutputStream.writeUTF(jsonData.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Socket socket;
        try {
            _serverSocket = new ServerSocket(_serverPort);
            ((IMessage)_context).setVisibility(R.id.start_server, View.GONE);

        } catch (IOException e) {
            e.printStackTrace();
            ((IMessage)_context).showMessage("Error Starting Server : " + e.getMessage(), Color.RED);
        }
        if (null != _serverSocket) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = _serverSocket.accept();
                    CommunicationThread commThread = new CommunicationThread(_context, socket);
                    _tempClientSocket = commThread.getTempClientSocket();
                    new Thread(commThread).start();
                } catch (IOException e) {
                    e.printStackTrace();
                    ((IMessage)_context).showMessage("Error Communicating to Client :" + e.getMessage(), Color.RED);
                }
            }
        }
    }
}