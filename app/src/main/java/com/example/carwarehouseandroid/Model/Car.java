package com.example.carwarehouseandroid.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
@Entity(tableName = "car_table")
public class Car {

    @PrimaryKey
    private int registration_id;
    private int year,price,quantity,m_id;
    private int car_id;
    private String make,model,colour,image;


    public Car(int registration_id, int year, int price, int quantity, int m_id,  String make, String model, String colour, String image) {
        this.registration_id = registration_id;
        this.year = year;
        this.price = price;
        this.quantity = quantity;
        this.m_id = m_id;
        this.make = make;
        this.model = model;
        this.colour = colour;
        this.image = image;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }


   public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public int getRegistration_id() {
        return registration_id;
    }

    public void setRegistration_id(int registration_id) {
        this.registration_id = registration_id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getM_id() {
        return m_id;
    }

    public void setM_id(int m_id) {
        this.m_id = m_id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
