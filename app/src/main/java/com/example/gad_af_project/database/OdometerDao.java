package com.example.gad_af_project.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gad_af_project.vehicles.OdometerHistory;
import com.example.gad_af_project.vehicles.Vehicle;

import java.util.List;

@Dao
public interface OdometerDao {
    @Query("SELECT * FROM Odometer")
    List<OdometerHistory> getAllOdometerEntries();

    @Query("SELECT * FROM Odometer WHERE vehicleId = :vehicleId")
    List<OdometerHistory> getOdometerForVehicle(int vehicleId);

    @Query("SELECT * FROM Odometer WHERE vehicleId = :vehicleId ORDER BY timestamp DESC LIMIT 1")
    OdometerHistory getLastOdometerEntryForVehicle(int vehicleId);

    @Insert
    void insertOdometer(OdometerHistory odometerHistory);

    @Update
    void updateOdometer(OdometerHistory odometerHistory);

    @Delete
    void deleteOdometer(OdometerHistory odometerHistory);

}
