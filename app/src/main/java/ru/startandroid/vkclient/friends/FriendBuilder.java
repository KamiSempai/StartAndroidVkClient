package ru.startandroid.vkclient.friends;

/**
 * @author Samofal Vitaliy
 * Хранит информацию о конкретном друге.
 * Организованно с помощью паттерна Builder.
 */
public class FriendBuilder {
    private Integer id = null;
    private String firstName = null;
    private String lastName = null;
    private Boolean online = null;

    // TODO организовать загрузку и кеширование аватарок
    private String photoLink = null;

    public FriendBuilder(int id, String firstName, String lastName) {
        this(id, firstName, lastName, null, null);
    }

    public FriendBuilder(int id, String firstName, String lastName, Boolean online) {
        this(id, firstName, lastName, online, null);
    }

    public FriendBuilder(int id, String firstName, String lastName, String photoLink) {
        this(id, firstName, lastName, null, photoLink);
    }

    public FriendBuilder(int id, String firstName, String lastName, Boolean online, String photoLink) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.online = online;
        this.photoLink = photoLink;
    }

    public FriendBuilder id(int id) {
        this.id = id;
        return this;
    }

    public FriendBuilder firstName(String first_name) {
        this.firstName = first_name;
        return this;
    }

    public FriendBuilder lastName(String first_name) {
        this.firstName = first_name;
        return this;
    }

    public FriendBuilder photoLink(String photoLink) {
        this.photoLink = photoLink;
        return this;
    }

    public FriendBuilder online(Boolean online) {
        this.online = online;
        return this;
    }
    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Boolean getOnline() {
        return online;
    }

    public String getPhotoLink() {
        return photoLink;
    }


}