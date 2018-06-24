package com.example.nirma.moes5.model;

/**
 * Created by mvryan on 13/06/18.
 * this class is model for penduduk API
 */

public class Penduduk {
    private String name;
    private String nik;
    private String gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
