package com.android.mhike;

import java.io.Serializable;
import java.util.Objects;

public class Hike implements Serializable {

    private int id;
    private String name;
    private String location;
    private String date;
    private int parkingAvailable;
    private int length;
    private int level;
    private String description;

    public Hike() {}

    public Hike(int id, String name, String location, String date, int parkingAvailable, int length, int level, String description) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.parkingAvailable = parkingAvailable;
        this.length = length;
        this.level = level;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(int parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }




    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hike hike = (Hike) o;
        return id == hike.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Hike{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", parkingAvailable=" + parkingAvailable +
                ", length=" + length +
                ", level=" + level +
                ", description='" + description + '\'' +
                '}';
    }
}
