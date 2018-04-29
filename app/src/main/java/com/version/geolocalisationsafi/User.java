package com.version.geolocalisationsafi;

import java.io.Serializable;

public class User implements Serializable{

      private String id;
      private String pseudo;
      private String email;
      private String photo;

    public User() {
    }

    public User(String id,String pseudo, String email, String photo) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
