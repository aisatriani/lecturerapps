package com.tenilodev.lecturermaps.model;

import java.io.Serializable;

/**
 * Created by azisa on 12/10/2016.
 */

public class Mahasiswa implements Serializable{

    private String NAMA;
    private String TEMPAT_LAHIR;
    private String TANGGAL_LAHIR;
    private String SEX;
    private String AGAMA;
    private String KEWARGANEGARAAN;
    private String KODEFAK;
    private String FAKULTAS;
    private String PRODI;
    private String JENJANG;
    private String ANGKATAN;
    private String KELAS;
    private String TIPEKELAS;
    private String SELEKSI;
    private String JALUR;
    private AlamatTetap ALAMATTETAP;
    private String EMAIL;

    public class AlamatTetap {
        String ALAMAT;
        String PROV;
        String KAB;
        String KEC;
        String DESA;
        String KODEPOS;
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

    public String getKODEFAK() {
        return KODEFAK;
    }

    public void setKODEFAK(String KODEFAK) {
        this.KODEFAK = KODEFAK;
    }

    public String getFAKULTAS() {
        return FAKULTAS;
    }

    public void setFAKULTAS(String FAKULTAS) {
        this.FAKULTAS = FAKULTAS;
    }

    public String getPRODI() {
        return PRODI;
    }

    public void setPRODI(String PRODI) {
        this.PRODI = PRODI;
    }

    public String getJENJANG() {
        return JENJANG;
    }

    public void setJENJANG(String JENJANG) {
        this.JENJANG = JENJANG;
    }

    public String getANGKATAN() {
        return ANGKATAN;
    }

    public void setANGKATAN(String ANGKATAN) {
        this.ANGKATAN = ANGKATAN;
    }

    public String getKELAS() {
        return KELAS;
    }

    public void setKELAS(String KELAS) {
        this.KELAS = KELAS;
    }

    public String getTIPEKELAS() {
        return TIPEKELAS;
    }

    public void setTIPEKELAS(String TIPEKELAS) {
        this.TIPEKELAS = TIPEKELAS;
    }

    public String getSELEKSI() {
        return SELEKSI;
    }

    public void setSELEKSI(String SELEKSI) {
        this.SELEKSI = SELEKSI;
    }

    public String getJALUR() {
        return JALUR;
    }

    public void setJALUR(String JALUR) {
        this.JALUR = JALUR;
    }

    public AlamatTetap getALAMATTETAP() {
        return ALAMATTETAP;
    }

    public void setALAMATTETAP(AlamatTetap ALAMATTETAP) {
        this.ALAMATTETAP = ALAMATTETAP;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }
}
