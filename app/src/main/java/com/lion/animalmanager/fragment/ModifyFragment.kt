package com.lion.animalmanager.fragment

import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lion.animalmanager.util.FragmentName
import com.lion.animalmanager.MainActivity
import com.lion.animalmanager.R
import com.lion.animalmanager.databinding.FragmentModifyBinding
import com.lion.animalmanager.repository.AnimalRepository
import com.lion.animalmanager.util.AnimalGender
import com.lion.animalmanager.util.AnimalType
import com.lion.animalmanager.viewmodel.AnimalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ModifyFragment : Fragment() {
    lateinit var fragmentModifyBinding: FragmentModifyBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentModifyBinding = FragmentModifyBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // Toolbar 설정 메서드를 호출한다.
        settingToolbar()
        // 입력 요소들 초기 설정
        settingInput()

        return fragmentModifyBinding.root
    }

    // Toolbar 설정 메서드
    fun settingToolbar(){
        fragmentModifyBinding.apply {
            // 타이틀
            toolbarModify.title = "동물 정보 수정"
            // 네비게이션
            toolbarModify.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarModify.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.MODIFY_FRAGMENT)
            }
            // 메뉴
            toolbarModify.inflateMenu(R.menu.modify_toolbar_menu)
            toolbarModify.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.modify_toolbar_menu_done ->{
                        // mainActivity.removeFragment(FragmentName.MODIFY_FRAGMENT)
                        modifyDone()
                    }
                }
                true
            }
        }
    }

    // 입력 요소들 초기 설정
    fun settingInput(){
        fragmentModifyBinding.apply {
            // 동물 번호를 가져온다.
            val animalIdx = arguments?.getInt("animalIdx")!!
            // 동물 데이터를 가져온다.
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO){
                    AnimalRepository.selectAnimalInfoByAnimalIdx(mainActivity, animalIdx)
                }
                val animalViewModel = work1.await()
                // 동물 종류
                when(animalViewModel.animalType){
                    AnimalType.ANIMAL_TYPE_DOG -> {
                        toggleGroupModifyType.check(R.id.buttonModifyTypeDog)
                    }
                    AnimalType.ANIMAL_TYPE_CAT -> {
                        toggleGroupModifyType.check(R.id.buttonModifyTypeCat)
                    }
                    AnimalType.ANIMAL_TYPE_PARROT -> {
                        toggleGroupModifyType.check(R.id.buttonModifyTypeParrot)
                    }
                }
                // 이름
                textFieldModifyName.editText?.setText(animalViewModel.animalName)
                // 나이
                textFieldModifyAge.editText?.setText(animalViewModel.animalAge.toString())
                // 성별
                when(animalViewModel.animalGender){
                    AnimalGender.GENDER_MALE -> {
                        radioGroupModifyGender.check(R.id.radioButtonModifyMale)
                    }
                    AnimalGender.GENDER_FEMALE -> {
                        radioGroupModifyGender.check(R.id.radioButtonModifyFemale)
                    }
                }
                // 생년월일 설정
                val date = animalViewModel.animalBirth.split("-")
                val year = date[0].toInt()
                val month = date[1].toInt() - 1 // Month는 0부터 시작
                val day = date[2].toInt()

                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)
                calendarModifyView.date = calendar.timeInMillis

                // 날짜 변경 리스너 추가
                calendarModifyView.setOnDateChangeListener { _, selectedYear, selectedMonth, selectedDay ->
                    val updatedCalendar = Calendar.getInstance()
                    updatedCalendar.set(selectedYear, selectedMonth, selectedDay)
                    fragmentModifyBinding.calendarModifyView.date = updatedCalendar.timeInMillis
                }
            }
        }
    }

    // 수정 처리 메서드
    fun modifyDone(){
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(mainActivity)
        materialAlertDialogBuilder.setTitle("수정")
        materialAlertDialogBuilder.setMessage("이전 데이터로 복원할 수 없습니다")
        materialAlertDialogBuilder.setNeutralButton("취소", null)
        materialAlertDialogBuilder.setPositiveButton("수정"){ dialogInterface: DialogInterface, i: Int ->
            // 수정할 데이터
            // 인덱스
            val animalIdx = arguments?.getInt("animalIdx")!!
            // 동물 종류
            val animalType = when(fragmentModifyBinding.toggleGroupModifyType.checkedButtonId){
                R.id.buttonModifyTypeDog -> AnimalType.ANIMAL_TYPE_DOG
                R.id.buttonModifyTypeCat -> AnimalType.ANIMAL_TYPE_CAT
                else -> AnimalType.ANIMAL_TYPE_PARROT
            }
            // 이름
            val animalName = fragmentModifyBinding.textFieldModifyName.editText?.text.toString()
            // 나이
            val animalAge = fragmentModifyBinding.textFieldModifyAge.editText?.text.toString().toInt()
            // 성별
            val animalGender = when(fragmentModifyBinding.radioGroupModifyGender.checkedRadioButtonId){
                R.id.radioButtonModifyMale -> AnimalGender.GENDER_MALE
                else -> AnimalGender.GENDER_FEMALE
            }
            // 생년월일
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = fragmentModifyBinding.calendarModifyView.date
            val animalBirth =
                "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"

            val animalViewModel = AnimalViewModel(animalIdx, animalType, animalName, animalAge, animalGender, animalBirth)

            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO){
                    AnimalRepository.updateAnimalInfo(mainActivity, animalViewModel)
                }
                work1.join()
                mainActivity.removeFragment(FragmentName.MODIFY_FRAGMENT)
            }
        }
        materialAlertDialogBuilder.show()
    }
}