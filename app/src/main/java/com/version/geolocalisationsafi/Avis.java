package com.version.geolocalisationsafi;

public class Avis {
    private float nbrestars;
    private String id;
    private String compte;
    private String avis;
    private String placeid;
    public Avis() {
    }

    public Avis(float nbrestars, String id, String compte, String avis, String placeid) {
        this.nbrestars = nbrestars;
        this.id = id;
        this.compte = compte;
        this.avis = avis;
        this.placeid = placeid;
    }

    public float getNbrestars() {
        return nbrestars;
    }

    public void setNbrestars(float nbrestars) {
        this.nbrestars = nbrestars;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompte() {
        return compte;
    }

    public void setCompte(String compte) {
        this.compte = compte;
    }

    public String getAvis() {
        return avis;
    }

    public void setAvis(String avis) {
        this.avis = avis;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }
}
