package com.lion.animalmanager.fragment

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lion.animalmanager.util.AnimalType
import com.lion.animalmanager.util.FragmentName
import com.lion.animalmanager.MainActivity
import com.lion.animalmanager.R
import com.lion.animalmanager.databinding.FragmentInputBinding
import com.lion.animalmanager.repository.AnimalRepository
import com.lion.animalmanager.util.AnimalGender
import com.lion.animalmanager.viewmodel.AnimalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class InputFragment : Fragment() {

    lateinit var fragmentInputBinding: FragmentInputBinding
    lateinit var mainActivity: MainActivity

    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDay: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentInputBinding = FragmentInputBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // CalendarView 초기 설정
        setupCalendarView()

        // 툴바를 구성하는 메서드를 호출한다.
        settingToolbar()

        return fragmentInputBinding.root
    }

    // CalendarView 초기 설정 메서드
    private fun setupCalendarView() {
        fragmentInputBinding.calendarInputView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedYear = year
            selectedMonth = month
            selectedDay = dayOfMonth
        }

        // CalendarView 초기값 설정 (현재 날짜로 초기화)
        val calendar = Calendar.getInstance()
        selectedYear = calendar.get(Calendar.YEAR)
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
    }

    // Toolbar를 설정하는 메서드
    fun settingToolbar() {
        fragmentInputBinding.apply {
            // 타이틀
            toolbarInput.title = "동물 정보 입력"
            // 네비게이션 아이콘
            toolbarInput.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarInput.setNavigationOnClickListener {
                // 이전 화면으로 돌아간다.
                mainActivity.removeFragment(FragmentName.INPUT_FRAGMENT)
            }
            // 메뉴
            toolbarInput.inflateMenu(R.menu.input_toolbar_menu)
            toolbarInput.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.input_toolbar_menu_done -> {
                        // 사용자가 입력한 데이터를 가져온다.
                        // 동물 종류
                        val animalType = when (toggleGroupInputType.checkedButtonId) {
                            // 강아지
                            R.id.buttonInputTypeDog -> AnimalType.ANIMAL_TYPE_DOG
                            // 고양이
                            R.id.buttonInputTypeCat -> AnimalType.ANIMAL_TYPE_CAT
                            // 앵무새
                            else -> AnimalType.ANIMAL_TYPE_PARROT
                        }
                        // 이름
                        val animalName = textFieldInputName.editText?.text.toString()
                        // 나이
                        val animalAge = textFieldInputAge.editText?.text.toString().toInt()
                        // 성별 설정
                        val animalGender = when (radioGroupInputGender.checkedRadioButtonId) {
                            R.id.radioButtonInputMale -> AnimalGender.GENDER_MALE
                            else -> AnimalGender.GENDER_FEMALE
                        }
                        // 생년월일
                        val animalBirth = "$selectedYear-${selectedMonth + 1}-$selectedDay"

                        // 객체에 담는다.
                        val animalViewModel = AnimalViewModel(0, animalType, animalName, animalAge, animalGender, animalBirth)

                        // 데이터를 저장하는 메서드를 코루틴으로 운영한다.
                        CoroutineScope(Dispatchers.Main).launch {
                            // 저장작업이 끝날때까지 대기한다.
                            async(Dispatchers.IO) {
                                // 저장한다.
                                AnimalRepository.insertAnimalInfo(mainActivity, animalViewModel)
                            }
                            // 저장작업이 모두 끝나면 이전 화면으로 돌아간다.
                            mainActivity.removeFragment(FragmentName.INPUT_FRAGMENT)
                        }
                    }
                }
                true
            }
        }
    }
}
