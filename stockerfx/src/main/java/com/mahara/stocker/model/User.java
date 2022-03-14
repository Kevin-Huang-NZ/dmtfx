package com.mahara.stocker.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private final LongProperty id;
    private final StringProperty userName;
    private final StringProperty avatar;
    private final StringProperty gender;
    private final StringProperty birthday;
    private final StringProperty phone;
    private final StringProperty password;
    private final StringProperty roles;
    private final StringProperty status;

    public User() {
        this(0l);
    }

    public User(Long id) {
        this.id = new SimpleLongProperty(id);
        this.userName = new SimpleStringProperty("");
        this.avatar = new SimpleStringProperty(null);
        this.gender = new SimpleStringProperty(null);
        this.birthday = new SimpleStringProperty(null);
        this.phone = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        this.roles = new SimpleStringProperty("");
        this.status = new SimpleStringProperty("1");
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public String getUserName() {
        return userName.get();
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public String getAvatar() {
        return avatar.get();
    }

    public StringProperty avatarProperty() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar.set(avatar);
    }

    public String getGender() {
        return gender.get();
    }

    public StringProperty genderProperty() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public String getBirthday() {
        return birthday.get();
    }

    public StringProperty birthdayProperty() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday.set(birthday);
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getRoles() {
        return roles.get();
    }

    public StringProperty rolesProperty() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles.set(roles);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}
