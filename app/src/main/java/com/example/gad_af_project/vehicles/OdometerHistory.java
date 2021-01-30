package com.example.gad_af_project.vehicles;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "Odometer",
        indices = @Index(value = "vehicleId", unique = true),
        foreignKeys = @ForeignKey(entity = Vehicle.class,
        parentColumns = "id",
        childColumns = "vehicleId",
        onDelete = ForeignKey.CASCADE))
public class OdometerHistory {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int entryId;

    @ColumnInfo(name = "vehicleId")
    private int vehicleId;

    @ColumnInfo(name = "timestamp")
    @TypeConverters(DateConverter.class)
    private Date date;

    @ColumnInfo(name = "value")
    private int value;

    @ColumnInfo(name = "unit")
    private String unit;

    //region GETTERS
    public int getEntryId() {
        return entryId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public Date getDate() {
        return date;
    }

    public int getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }
    //endregion

    //region SETTERS
    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    //endregion
}
