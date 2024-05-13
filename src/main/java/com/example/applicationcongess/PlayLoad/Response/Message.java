package com.example.applicationcongess.PlayLoad.Response;

import lombok.Data;

@Data
public class Message {
    private String messageContent;

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
