package com.lion.animalmanager.util

// 프래그먼트를 나타내는 값
enum class FragmentName(var number:Int, var str:String){
    // 첫 화면
    MAIN_FRAGMENT(1, "MainFragment"),
    // 입력 화면
    INPUT_FRAGMENT(2, "InputFragment"),
    // 출력 화면
    SHOW_FRAGMENT(3, "ShowFragment"),
    // 수정 화면
    MODIFY_FRAGMENT(4, "ModifyFragment")
}

// 동물 종류를 나타내는 값
enum class AnimalType(var number:Int, var str:String){
    // 강아지
    ANIMAL_TYPE_DOG(1, "강아지"),
    // 고양이
    ANIMAL_TYPE_CAT(2, "고양이"),
    // 앵무새
    ANIMAL_TYPE_PARROT(3, "앵무새")
}

// 동물 성별을 나타내는 값
enum class AnimalGender(var number:Int, var str:String) {
    // 수컷
    GENDER_MALE(1, "수컷"),
    // 암컷
    GENDER_FEMALE(2, "암컷")
}