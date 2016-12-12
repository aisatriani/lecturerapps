package com.tenilodev.lecturermaps.model;

import java.io.Serializable;

/**
 * Created by azisa on 12/12/2016.
 */

public class Dosen implements Serializable {
    private String NIDN;
    private String NIP;
    private String NAMA;
    private String TEMPAT_LAHIR;
    private String TANGGAL_LAHIR;
    private String SEX;
    private String AGAMA;
    private String KEWARGANEGARAAN;
    private String STATUSDOSEN;
    private String STATUSKERJA;
    private String JABATANKADEMIK;
    private String PENDIDIKANTERTINGGI;
    private String EMAIL;
    private double LATITUDE;
    private double LONGITUDE;

    public String getNIDN() {
        return NIDN;
    }

    public void setNIDN(String NIDN) {
        this.NIDN = NIDN;
    }

    public String getNIP() {
        return NIP;
    }

    public void setNIP(String NIP) {
        this.NIP = NIP;
    }

    public String getNAMA() {
        return NAMA;
    }

    public void setNAMA(String NAMA) {
        this.NAMA = NAMA;
    }

    public String getTEMPAT_LAHIR() {
        return TEMPAT_LAHIR;
    }

    public void setTEMPAT_LAHIR(String TEMPAT_LAHIR) {
        this.TEMPAT_LAHIR = TEMPAT_LAHIR;
    }

    public String getTANGGAL_LAHIR() {
        return TANGGAL_LAHIR;
    }

    public void setTANGGAL_LAHIR(String TANGGAL_LAHIR) {
        this.TANGGAL_LAHIR = TANGGAL_LAHIR;
    }

    public String getSEX() {
        return SEX;
    }

    public void setSEX(String SEX) {
        this.SEX = SEX;
    }

    public String getAGAMA() {
        return AGAMA;
    }

    public void setAGAMA(String AGAMA) {
        this.AGAMA = AGAMA;
    }

    public String getKEWARGANEGARAAN() {
        return KEWARGANEGARAAN;
    }

    public void setKEWARGANEGARAAN(String KEWARGANEGARAAN) {
        this.KEWARGANEGARAAN = KEWARGANEGARAAN;
    }

    public String getSTATUSDOSEN() {
        return STATUSDOSEN;
    }

    public void setSTATUSDOSEN(String STATUSDOSEN) {
        this.STATUSDOSEN = STATUSDOSEN;
    }

    public String getSTATUSKERJA() {
        return STATUSKERJA;
    }

    public void setSTATUSKERJA(String STATUSKERJA) {
        this.STATUSKERJA = STATUSKERJA;
    }

    public String getJABATANKADEMIK() {
        return JABATANKADEMIK;
    }

    public void setJABATANKADEMIK(String JABATANKADEMIK) {
        this.JABATANKADEMIK = JABATANKADEMIK;
    }

    public String getPENDIDIKANTERTINGGI() {
        return PENDIDIKANTERTINGGI;
    }

    public void setPENDIDIKANTERTINGGI(String PENDIDIKANTERTINGGI) {
        this.PENDIDIKANTERTINGGI = PENDIDIKANTERTINGGI;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public double getLATITUDE() {
        return LATITUDE;
    }

    public void setLATITUDE(double LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public double getLONGITUDE() {
        return LONGITUDE;
    }

    public void setLONGITUDE(double LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }
}
