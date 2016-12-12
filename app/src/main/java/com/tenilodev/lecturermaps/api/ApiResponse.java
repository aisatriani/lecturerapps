package com.tenilodev.lecturermaps.api;

/**
 * Created by azisa on 12/10/2016.
 */

public class ApiResponse<T> {
    private int err_no;
    private String err_teks;
    private T data;

    public int getErr_no() {
        return err_no;
    }

    public void setErr_no(int err_no) {
        this.err_no = err_no;
    }

    public String getErr_teks() {
        return err_teks;
    }

    public void setErr_teks(String err_teks) {
        this.err_teks = err_teks;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
