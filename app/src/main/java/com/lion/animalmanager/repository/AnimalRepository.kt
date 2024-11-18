package com.lion.animalmanager.repository

import android.content.Context
import com.lion.animalmanager.util.AnimalType
import com.lion.animalmanager.dao.AnimalDatabase
import com.lion.animalmanager.dao.AnimalDatabase.Companion.animalDatabase
import com.lion.animalmanager.util.AnimalGender
import com.lion.animalmanager.viewmodel.AnimalViewModel
import com.lion.animalmanager.vo.AnimalVO

class AnimalRepository {

    companion object{

        // 동물 정보를 저장하는 메서드
        fun insertAnimalInfo(context: Context, animalViewModel: AnimalViewModel){
            // 데이터베이스 객체를 가져온다.
            val animalDatabase = AnimalDatabase.getInstance(context)
            // ViewModel에 있는 데이터를 VO에 담아준다.
            val animalType = animalViewModel.animalType.number
            val animalName = animalViewModel.animalName
            val animalAge = animalViewModel.animalAge
            val animalGender = animalViewModel.animalGender.number
            val animalBirth = animalViewModel.animalBirth

            val animalVO = AnimalVO(animalType = animalType, animalName = animalName, animalAge = animalAge, animalGender = animalGender, animalBirth = animalBirth)

            animalDatabase?.animalDAO()?.insertAnimalData(animalVO)
        }

        // 동물 정보 전체를 가져오는 메서드
        fun selectAnimalInfoAll(context: Context) : MutableList<AnimalViewModel>{
            // 데이터 베이스 객체
            val animalDatabase = AnimalDatabase.getInstance(context)
            // 동물 데이터 전체를 가져온다
            val animalVoList = animalDatabase?.animalDAO()?.selectAnimalDataAll()
            // 동물 데이터를 담을 리스트
            val animalViewModelList = mutableListOf<AnimalViewModel>()
            // 동물의 수 만큼 반복한다.
            animalVoList?.forEach {
                // 동물 데이터를 추출한다.
                // 동물 종류
                val animalType = when(it.animalType){
                    AnimalType.ANIMAL_TYPE_DOG.number -> AnimalType.ANIMAL_TYPE_DOG
                    AnimalType.ANIMAL_TYPE_CAT.number -> AnimalType.ANIMAL_TYPE_CAT
                    else -> AnimalType.ANIMAL_TYPE_PARROT
                }
                // 이름
                val animalName = it.animalName
                // 나이
                val animalAge = it.animalAge
                // 인덱스
                val animalIdx = it.animalIdx
                // 성별
                val animalGender = when(it.animalGender){
                    AnimalGender.GENDER_MALE.number -> AnimalGender.GENDER_MALE
                    else -> AnimalGender.GENDER_FEMALE
                }
                // 생년월일
                val animalBirth = it.animalBirth
                // 객체에 담는다.
                val animalViewModel = AnimalViewModel(animalIdx, animalType, animalName, animalAge, animalGender, animalBirth)
                // 리스트에 담는다.
                animalViewModelList.add(animalViewModel)
            }
            return animalViewModelList
        }

        fun selectAnimalInfoByAnimalIdx(context: Context, animalIdx:Int) : AnimalViewModel{
            val animalDatabase = AnimalDatabase.getInstance(context)
            // 동물의 정보를 가져온다.
            val animalVO = animalDatabase?.animalDAO()?.selectAnimalDataByAnimalIdx(animalIdx)
            // 동물 객체에 담는다
            // 동물 종류
            val animalType = when(animalVO?.animalType){
                AnimalType.ANIMAL_TYPE_DOG.number -> AnimalType.ANIMAL_TYPE_DOG
                AnimalType.ANIMAL_TYPE_CAT.number -> AnimalType.ANIMAL_TYPE_CAT
                else -> AnimalType.ANIMAL_TYPE_PARROT
            }
            // 이름
            val animalName = animalVO?.animalName
            // 나이
            val animalAge = animalVO?.animalAge
            // 성별
            val animalGender = when(animalVO?.animalGender){
                AnimalGender.GENDER_MALE.number -> AnimalGender.GENDER_MALE
                else -> AnimalGender.GENDER_FEMALE
            }
            // 생년월일
            val animalBirth = animalVO?.animalBirth

            val animalViewModel = AnimalViewModel(animalIdx, animalType, animalName!!, animalAge!!, animalGender, animalBirth!!)

            return animalViewModel
        }

        // 동물 정보 삭제
        fun deleteAnimalInfoByAnimalIdx(context: Context, animalIdx: Int){
            val animalDatabase = AnimalDatabase.getInstance(context)
            // 삭제할 동물 번호를 담고 있을 객체를 생성한다.
            val animalVO = AnimalVO(animalIdx = animalIdx)
            // 삭제한다
            animalDatabase?.animalDAO()?.deleteAnimalData(animalVO)
        }

        // 동물 정보를 수정하는 메서드
        fun updateAnimalInfo(context: Context, animalViewModel: AnimalViewModel){
            val animalDatabase = AnimalDatabase.getInstance(context)
            // VO에 객체에 담아준다
            val animalIdx = animalViewModel.animalIdx
            val animalType = animalViewModel.animalType.number
            val animalName = animalViewModel.animalName
            val animalAge = animalViewModel.animalAge
            val animalGender = animalViewModel.animalGender.number
            val animalBirth = animalViewModel.animalBirth
            val animalVO = AnimalVO(animalIdx, animalName, animalType, animalAge, animalGender, animalBirth)
            // 수정한다.
            animalDatabase?.animalDAO()?.updateAnimalData(animalVO)
        }

        fun selectAnimalInfoByType(context: Context, animalType: AnimalType): MutableList<AnimalViewModel> {
            // 데이터베이스 인스턴스를 가져옵니다.
            val db = AnimalDatabase.getInstance(context)
                ?: throw IllegalStateException("Database instance is null")

            val animalList = mutableListOf<AnimalViewModel>()

            // SQL 쿼리를 통해 데이터를 가져옵니다. `animalType.number`로 조건을 걸어 필터링.
            val cursor = db.openHelper.readableDatabase.query(
                "SELECT * FROM AnimalTable WHERE animalType = ?",
                arrayOf(animalType.number.toString()) // Enum의 number 값을 조건으로 전달
            )

            println("Query executed for animalType: ${animalType.number}, cursor count: ${cursor.count}")

            // Cursor에서 데이터를 읽어 AnimalViewModel 객체로 변환합니다.
            while (cursor.moveToNext()) {
                val animalIdx = cursor.getInt(cursor.getColumnIndexOrThrow("animalIdx"))
                val animalTypeValue = cursor.getInt(cursor.getColumnIndexOrThrow("animalType"))
                val animalName = cursor.getString(cursor.getColumnIndexOrThrow("animalName"))
                val animalAge = cursor.getInt(cursor.getColumnIndexOrThrow("animalAge"))
                val animalGenderValue = cursor.getInt(cursor.getColumnIndexOrThrow("animalGender"))
                val animalBirth = cursor.getString(cursor.getColumnIndexOrThrow("animalBirth"))

                // animalType와 animalGender를 Enum으로 변환
                val resolvedAnimalType = AnimalType.values().find { it.number == animalTypeValue }
                val resolvedAnimalGender = AnimalGender.values().find { it.number == animalGenderValue }

                if (resolvedAnimalType != null && resolvedAnimalGender != null) {
                    val animal = AnimalViewModel(
                        animalIdx,
                        resolvedAnimalType,
                        animalName,
                        animalAge,
                        resolvedAnimalGender,
                        animalBirth
                    )
                    animalList.add(animal)
                } else {
                    println("Invalid data: animalType=$animalTypeValue, animalGender=$animalGenderValue")
                }
            }
            cursor.close() // Cursor를 닫아 리소스를 해제합니다.

            return animalList
        }
    }
}