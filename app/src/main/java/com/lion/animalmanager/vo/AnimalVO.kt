package com.lion.animalmanager.vo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AnimalTable")
data class AnimalVO (
    @PrimaryKey(autoGenerate = true)
    var animalIdx:Int = 0,
    var animalName:String = "",
    var animalType:Int = 0,
    var animalAge:Int = 0,
    var animalGender:Int = 0,
    var animalBirth:String = ""
)