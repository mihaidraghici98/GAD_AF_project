package com.example.gad_af_project.vehicles;

public class Vehicle{

    private String plate_number;
    private String vin;
    private int mileage;
    private String mileageUnit; // "mi" / "km"
    private int engineCap; // cmc
    private String make;
    private String model;

    public String getPlateNo(){
        return this.plate_number;
    }

    public static class Builder {
        private String plate_number = "";
        private String vin = "";
        private int mileage = -1;
        private String mileageUnit = ""; // "mi" / "km"
        private int engineCap = 0; // cmc
        private String make = "";
        private String model = "";

        public Builder(String plate_number){
            this.plate_number = plate_number;
        }

        public Builder withVin(String vin){
            this.vin = vin;
            return this;
        }
        public Builder withMileageAndUnit(int mileage, String mileageUnit){
            this.mileage = mileage;
            this.mileageUnit = mileageUnit;
            return this;
        }
        public Builder withEngineCap(int engineCap){
            this.engineCap = engineCap;
            return this;
        }
        public Builder withMakeAndModel(String make, String model){
            this.make = make;
            this.model = model;
            return this;
        }
        public Vehicle build(){
            Vehicle veh = new Vehicle(plate_number);
            veh.vin = vin;
            veh.mileage = mileage;
            veh.mileageUnit = mileageUnit;
            veh.engineCap = engineCap;
            veh.make = make;
            veh.model = model;
            return veh;
        }
    }

    private Vehicle(String plate_number) {
        this.plate_number = plate_number;
    }
}
