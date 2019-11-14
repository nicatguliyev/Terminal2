package com.example.niguliyev.terminal.Model;

import android.os.Parcelable;

public class SeatModel {
    private int seat_id;
    private  String seatLabel;
    private int seat_status_id;
    private  int e_ticket_status;
    private int ticket_checkin_status;


    public SeatModel() {

    }

    public SeatModel( int seat_id, String seatLabel, int seat_status_id, int e_ticket_status, int ticket_checkin_status) {
        this.seat_id = seat_id;
        this.seatLabel = seatLabel;
        this.seat_status_id = seat_status_id;
        this.e_ticket_status = e_ticket_status;
        this.ticket_checkin_status = ticket_checkin_status;
    }

    public String getSeatLabel() {
        return seatLabel;
    }

    public int getSeat_id() {
        return seat_id;
    }

    public void setSeat_id(int seat_id) {
        this.seat_id = seat_id;
    }

    public void setSeatLabel(String seatLabel) {
        this.seatLabel = seatLabel;
    }

    public int getSeat_status_id() {
        return seat_status_id;
    }

    public void setSeat_status_id(int seat_status_id) {
        this.seat_status_id = seat_status_id;
    }

    public int getE_ticket_status() {
        return e_ticket_status;
    }

    public void setE_ticket_status(int e_ticket_status) {
        this.e_ticket_status = e_ticket_status;
    }

    public int getTicket_checkin_status() {
        return ticket_checkin_status;
    }

    public void setTicket_checkin_status(int ticket_checkin_status) {
        this.ticket_checkin_status = ticket_checkin_status;
    }
}
