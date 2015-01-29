package ru.startandroid.vkclient;

/**
 * Класс, содержащий данные об одном сообщении
 */
public class ChatMessage {

    private int id; // id сообщения
    private String body; // текст сообщения
    private boolean read; // прочитанное - true/нет - false
    private boolean out; // исходящее - true/входящее - false

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
