package com.cs121.finalproject;


public class pickedDate {
    private static pickedDate instance = null;

    private pickedDate(){}

    private String date;


    public String getDate(){
        return date;
    }

    public void setDate(String _date){
        date = _date;
    }

    public static pickedDate getPickedDate(){
        if(instance == null){
            instance = new pickedDate();
        }
        return instance;
    }
}
