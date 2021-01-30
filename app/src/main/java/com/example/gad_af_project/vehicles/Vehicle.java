package com.example.gad_af_project.vehicles;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Vehicle")
public class Vehicle {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int vehicleId;

    @ColumnInfo(name = "type")
    private String vehicleType;

    @ColumnInfo(name = "plateNumber")
    private String plateNumber;

    @ColumnInfo(name = "vin")
    private String vin;

    @ColumnInfo(name = "make")
    private String make;

    @ColumnInfo(name = "model")
    private String model;

    @ColumnInfo(name = "year")
    private int year;

    @ColumnInfo(name = "odometerUnit")
    private String odometerUnit; // MI, KM

    @ColumnInfo(name = "fuelType")
    private String fuelType; // DIESEL, GASOLINE, LPG, ELECTRIC

    @ColumnInfo(name = "fuelCapacity")
    private int fuelCapacity;

    @ColumnInfo(name = "engineCapacity")
    private int engineCapacity; // cmc

    @ColumnInfo(name = "enginePower")
    private int enginePower;

    public Vehicle(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    //region GETTERS
    public int getVehicleId() {
        return vehicleId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getVin() {
        return vin;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public String getOdometerUnit() {
        return odometerUnit;
    }

    public String getFuelType() {
        return fuelType;
    }

    public int getFuelCapacity() {
        return fuelCapacity;
    }

    public int getEngineCapacity() {
        return engineCapacity;
    }

    public int getEnginePower() {
        return enginePower;
    }
    //endregion

    //region SETTERS
    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setOdometerUnit(String odometerUnit) {
        this.odometerUnit = odometerUnit;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public void setFuelCapacity(int fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public void setEngineCapacity(int engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public void setEnginePower(int enginePower) {
        this.enginePower = enginePower;
    }
    //endregion

    //region BUILDER
    public static class Builder {
        private String plateNumber;
        private String vin;
        private String type;
        private String make;
        private String model;
        private int year;
        private String odometerUnit; // MI, KM
        private String fuelType; // DIESEL, GASOLINE, LPG, ELECTRIC
        private int fuelCapacity;
        private int engineCapacity; // cmc
        private int enginePower;

        public Builder(String plateNumber){
            this.plateNumber = plateNumber;
        }

        public Builder withVin(String vin){
            this.vin = vin;
            return this;
        }
        public Builder type(String type){
            this.type = type;
            return this;
        }
        public Builder makeAndModel(String make, String model){
            this.make = make;
            this.model = model;
            return this;
        }
        public Builder fromYear(int year){
            this.year = year;
            return this;
        }
        public Builder havingOdometerUnit(String odometerUnit){
            this.odometerUnit = odometerUnit;
            return this;
        }
        public Builder havingFuelTypeAndCapacity(String fuelType, int fuelCapacity){
            this.fuelType = fuelType;
            this.fuelCapacity = fuelCapacity;
            return this;
        }
        public Builder withEngineCapacityAndPower(int engineCapacity, int enginePower){
            this.engineCapacity = engineCapacity;
            this.enginePower = enginePower;
            return this;
        }
        public Vehicle build(){
            Vehicle veh = new Vehicle(plateNumber);
            veh.vin = vin;
            veh.vehicleType = type;
            veh.make = make;
            veh.model = model;
            veh.year = year;
            veh.odometerUnit = odometerUnit;
            veh.fuelType = fuelType;
            veh.fuelCapacity = fuelCapacity;
            veh.engineCapacity = engineCapacity;
            veh.enginePower = enginePower;
            return veh;
        }
    }
    //endregion

}
