package com.version.geolocalisationsafi;

import android.media.Image;

import java.io.Serializable;
import java.util.Date;

public class Data implements Serializable {

    private String id;
    private String compte;
    private String description;
    private double lattitude;
    private double longitude;
    private String dateAjout;
    private String picture;
    private String tags;
    float nbrestars;
    String avis;

    public Data() {
    }

    public Data(String id,String compte, String description, double lattitude, double longitude, String dateAjout, String picture , String tags,float nbrestars
            ,String avis) {
        this.compte = compte;
        this.description = description;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.dateAjout = dateAjout;
        this.picture = picture;
        this.id = id;
        this.tags = tags;
        this.nbrestars = nbrestars;
        this.avis = avis;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCompte() {
        return compte;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCompte(String compte) {
        this.compte = compte;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(String dateAjout) {
        this.dateAjout = dateAjout;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public float getNbrestars() {
        return nbrestars;
    }

    public void setNbrestars(float nbrestars) {
        this.nbrestars = nbrestars;
    }

    public String getAvis() {
        return avis;
    }

    public void setAvis(String avis) {
        this.avis = avis;
    }
}
