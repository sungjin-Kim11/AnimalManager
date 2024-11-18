package com.lion.animalmanager.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lion.animalmanager.vo.AnimalVO

// 복붙 후...
// entities의 VO 클래스 이름 변경 혹은 추가
// 클래스 이름 변경
// dao를 반환하는 메서드의 이름과 반환 타입 변경
// companion object 내의 데이터 베이스 변수명과 타입 변경
// 빨간색으로 떠있는 변수의 이름과 클래스 타입을 변경
// 데이터 베이스 파일 이름 변경
@Database(entities = [AnimalVO::class], version = 1, exportSchema = true)
abstract class AnimalDatabase : RoomDatabase(){
    abstract fun animalDAO() : AnimalDAO

    companion object{
        var animalDatabase:AnimalDatabase? = null
        @Synchronized
        fun getInstance(context: Context) : AnimalDatabase??{
            synchronized(AnimalDatabase::class){
                animalDatabase = Room.databaseBuilder(
                    context.applicationContext, AnimalDatabase::class.java,
                    "Animal.db"
                ).build()
            }
            return animalDatabase
        }

        fun destroyInstance(){
            animalDatabase = null
        }
    }
}