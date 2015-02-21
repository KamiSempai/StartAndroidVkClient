package ru.startandroid.vkclient;

import java.util.ArrayList;
import java.util.Map;

/**
 * Класс, содержащий данные об одном сообщении
 */
public class ChatMessage {

    private int id; // id сообщения
    private String body; // текст сообщения
    private boolean read; // прочитанное - true/нет - false
    private boolean out; // исходящее - true/входящее - false
    private ArrayList<Map<String,String>> attachments; // прикрепленные файлы

    public ChatMessage() {
        attachments = new ArrayList<Map<String,String>>();
    }

    public ArrayList<Map<String, String>> getAttachments() {
        return attachments;
    }

    public ChatMessage setAttachments(ArrayList<Map<String, String>> attachments) {
        this.attachments = attachments;
        return this;
    }

    public ChatMessage setId(int id) {
        this.id = id;
        return this;
    }

    public ChatMessage setBody(String body) {
        this.body = body;
        return this;
    }

    public ChatMessage setReadState(boolean read) {
        this.read = read;
        return this;
    }

    public ChatMessage setOut(boolean out) {
        this.out = out;
        return this;
    }

    public int getId() {

        return id;
    }

    public String getBody() {
        return body;
    }

    public boolean isRead() {
        return read;
    }

    public boolean isOut() {
        return out;
    }
}
