package com.tenilodev.lecturermaps.model;

import android.support.annotation.StyleableRes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by azisa on 12/12/2016.
 */

public class Fakultas implements Serializable {
    private String KODEFAK;
    private String FAKULTAS;
    private String KODEJUR;
    private String JURUSAN;
    private String KODEPRODI;
    private String PRODI;
    private String JENJANG;
    private List<Dosen> DOSEN;

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

    public String getKODEJUR() {
        return KODEJUR;
    }

    public void setKODEJUR(String KODEJUR) {
        this.KODEJUR = KODEJUR;
    }

    public String getJURUSAN() {
        return JURUSAN;
    }

    public void setJURUSAN(String JURUSAN) {
        this.JURUSAN = JURUSAN;
    }

    public String getKODEPRODI() {
        return KODEPRODI;
    }

    public void setKODEPRODI(String KODEPRODI) {
        this.KODEPRODI = KODEPRODI;
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

    public List<Dosen> getDOSEN() {
        return DOSEN;
    }

    public void setDOSEN(List<Dosen> DOSEN) {
        this.DOSEN = DOSEN;
    }
}
