package com.example.niguliyev.terminal.Model;

public class WagonModel {
    private   int  wagon_id;
    private  String wagon_no;
    private  String wagon_type_abbr;

    public  WagonModel()
    {

    }

    public  WagonModel(int id, String wagon_no, String wagon_type_abbr)
    {
        this.wagon_id = id;
        this.wagon_no = wagon_no;
        this.wagon_type_abbr = wagon_type_abbr;
    }

    public  WagonModel(int id, String wagon_no){
        this.wagon_id = id;
        this.wagon_no = wagon_no;
    }

    public int getWagon_id() {
        return wagon_id;
    }

    public void setWagon_id(int wagon_id) {
        this.wagon_id = wagon_id;
    }

    public String getWagon_no() {
        return wagon_no;
    }

    public void setWagon_no(String wagon_no) {
        this.wagon_no = wagon_no;
    }

    public String getWagon_type_abbr() {
        return wagon_type_abbr;
    }

    public void setWagon_type_abbr(String wagon_type_abbr) {
        this.wagon_type_abbr = wagon_type_abbr;
    }
}
