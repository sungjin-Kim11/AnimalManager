package com.lion.animalmanager.viewmodel

import com.lion.animalmanager.util.AnimalGender
import com.lion.animalmanager.util.AnimalType


data class AnimalViewModel (
    var animalIdx:Int,
    var animalType: AnimalType,
    var animalName:String,
    var animalAge:Int,
    var animalGender: AnimalGender,
    var animalBirth:String
)
