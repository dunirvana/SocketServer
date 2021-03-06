package com.example.socketserver;

import android.content.Context;
import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

class CommunicationThread implements Runnable {

    private DataInputStream dataInputStream;
    private Context _context;
    private Socket _tempClientSocket;
    private int _greenColor = Color.parseColor("#52FF33");

    public CommunicationThread(Context context, Socket clientSocket) {

        _context = context;
        _tempClientSocket = clientSocket;

        try {
            dataInputStream = new DataInputStream(clientSocket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
            ((IMessage)_context).showMessage("Error Connecting to Client!!", Color.RED);
        }
        ((IMessage)_context).showMessage("Connected to Client!!", _greenColor);
    }

    public Socket getTempClientSocket() {

        return _tempClientSocket;
    }

    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            try {
                //String messageFromClient = dataInputStream.readUTF();
                String messageFromClient = Cryptography.decrypt(dataInputStream.readUTF(), null);

                final JSONObject jsonData = new JSONObject(messageFromClient);
                String read = jsonData.getString("id") + " - " + jsonData.getString("message");

                if ("Disconnect".contentEquals(read)) {

                    Thread.currentThread().interrupt();

                    read = "Client Disconnected";
                    ((IMessage)_context).showMessage("Client : " + read, _greenColor);
                    break;
                }
                if (!jsonData.has("isConfirmationMessage") || !jsonData.getString("isConfirmationMessage").equals("true")) {

                    // se nao for uma mensagem de confirmacao coloca texto no display
                    ((IMessage)_context).showMessage("Client : " + read, _greenColor);

                    // envia para o servidor a confirmacao
                    ((IMessage)_context).sendMessage("Server recebeu mensagem: " + jsonData.getString("id"), true);
                }
                else {
                    ((IMessage)_context).setLastMessageSentConfirmed('Y');
                }


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
