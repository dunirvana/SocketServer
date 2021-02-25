package com.example.socketserver;

public interface IMessage {

    void showMessage(final String message, final int color);
    void sendMessage(final String message, boolean isConfirmationMessage);

    void setVisibility(int id, int visibility);

    void setLastMessageSentConfirmed(char value);
}
