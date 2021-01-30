package com.example.gad_af_project.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gad_af_project.vehicles.Vehicle;

import java.util.List;

@Dao
public interface VehicleDao {
    @Query("SELECT * FROM Vehicle")
    List<Vehicle> getAllVehicles();

    @Query("SELECT * FROM Vehicle WHERE id = :vehicle_id")
    Vehicle getVehicleById(int vehicle_id);

    @Insert
    long insertVehicle(Vehicle vehicle);

    @Update
    void updateVehicle(Vehicle vehicle);

    @Delete
    void deleteVehicle(Vehicle vehicle);
}
