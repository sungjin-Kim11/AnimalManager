package com.lion.animalmanager.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.lion.animalmanager.util.FragmentName
import com.lion.animalmanager.MainActivity
import com.lion.animalmanager.R
import com.lion.animalmanager.databinding.FragmentMainBinding
import com.lion.animalmanager.databinding.RowMainBinding
import com.lion.animalmanager.repository.AnimalRepository
import com.lion.animalmanager.util.AnimalType
import com.lion.animalmanager.viewmodel.AnimalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    lateinit var fragmentMainBinding: FragmentMainBinding
    lateinit var mainActivity: MainActivity

    // 상태 변수 추가
    var currentFilter: AnimalType? = null
    var currentSortOption: String? = null
    var currentTitle: String = "동물 이름 목록" // 타이틀 상태를 저장
    var animalList = mutableListOf<AnimalViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentMainBinding = FragmentMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // Toolbar를 구성하는 메서드 호출
        settingToolbar()
        // RecyclerView를 구성하는 메서드 호출
        settingRecyclerView()
        // FAB를 구성하는 메서드 호출
        settingFAB()
        // RecyclerView 갱신 메서드를 호출한다.
        refreshRecyclerViewMain()

        // 이전에 저장된 타이틀 복원
        fragmentMainBinding.toolbarMain.title = currentTitle

        return fragmentMainBinding.root
    }

    // Toolbar를 구성하는 메서드
    fun settingToolbar() {
        fragmentMainBinding.apply {
            toolbarMain.title = currentTitle // 저장된 타이틀로 초기화
            toolbarMain.inflateMenu(R.menu.main_toolbar_menu)

            toolbarMain.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.restart -> {
                        // 전체 보기
                        currentFilter = null
                        currentSortOption = null
                        refreshRecyclerViewMain()
                        currentTitle = "동물 이름 목록" // 타이틀 업데이트
                        toolbarMain.title = currentTitle
                    }
                    R.id.choiceAgeUp -> {
                        // 나이 오름차순
                        currentSortOption = "AgeUp"
                        sortRecyclerViewByAge(ascending = true)
                        currentTitle = "동물 이름 목록 - 나이 (오름차순)"
                        toolbarMain.title = currentTitle
                    }
                    R.id.choiceAgeDown -> {
                        // 나이 내림차순
                        currentSortOption = "AgeDown"
                        sortRecyclerViewByAge(ascending = false)
                        currentTitle = "동물 이름 목록 - 나이 (내림차순)"
                        toolbarMain.title = currentTitle
                    }
                    R.id.choiceNameUp -> {
                        // 이름 오름차순
                        currentSortOption = "NameUp"
                        sortRecyclerViewByName(ascending = true)
                        currentTitle = "동물 이름 목록 - 이름 (오름차순)"
                        toolbarMain.title = currentTitle
                    }
                    R.id.choiceNameDown -> {
                        // 이름 내림차순
                        currentSortOption = "NameDown"
                        sortRecyclerViewByName(ascending = false)
                        currentTitle = "동물 이름 목록 - 이름 (내림차순)"
                        toolbarMain.title = currentTitle
                    }
                    R.id.choiceTypeDog -> {
                        // 강아지 보기
                        currentFilter = AnimalType.ANIMAL_TYPE_DOG
                        filterRecyclerViewByType(AnimalType.ANIMAL_TYPE_DOG)
                        currentTitle = "동물 이름 목록 - 강아지"
                        toolbarMain.title = currentTitle
                    }
                    R.id.choiceTypeCat -> {
                        // 고양이 보기
                        currentFilter = AnimalType.ANIMAL_TYPE_CAT
                        filterRecyclerViewByType(AnimalType.ANIMAL_TYPE_CAT)
                        currentTitle = "동물 이름 목록 - 고양이"
                        toolbarMain.title = currentTitle
                    }
                    R.id.choiceTypeParrot -> {
                        // 앵무새 보기
                        currentFilter = AnimalType.ANIMAL_TYPE_PARROT
                        filterRecyclerViewByType(AnimalType.ANIMAL_TYPE_PARROT)
                        currentTitle = "동물 이름 목록 - 앵무새"
                        toolbarMain.title = currentTitle
                    }
                }
                true
            }
        }
    }

    // RecyclerView를 구성하는 메서드
    fun settingRecyclerView() {
        fragmentMainBinding.apply {
            recyclerViewMain.adapter = RecyclerViewMainAdapter()
            recyclerViewMain.layoutManager = LinearLayoutManager(mainActivity)
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            recyclerViewMain.addItemDecoration(deco)
        }
    }

    // FAB를 구성하는 메서드
    fun settingFAB() {
        fragmentMainBinding.apply {
            fabMainAdd.setOnClickListener {
                mainActivity.replaceFragment(FragmentName.INPUT_FRAGMENT, true, null)
            }
        }
    }

    // 동물 정보를 가져와 RecyclerView를 갱신하는 메서드
    fun refreshRecyclerViewMain() {
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO) {
                currentFilter?.let { filter ->
                    AnimalRepository.selectAnimalInfoByType(mainActivity, filter)
                } ?: run {
                    AnimalRepository.selectAnimalInfoAll(mainActivity)
                }
            }
            animalList = work1.await()

            currentSortOption?.let {
                when (it) {
                    "AgeUp" -> sortRecyclerViewByAge(ascending = true)
                    "AgeDown" -> sortRecyclerViewByAge(ascending = false)
                    "NameUp" -> sortRecyclerViewByName(ascending = true)
                    "NameDown" -> sortRecyclerViewByName(ascending = false)
                }
            }

            fragmentMainBinding.recyclerViewMain.adapter?.notifyDataSetChanged()
        }
    }

    // RecyclerView를 나이 기준으로 정렬
    fun sortRecyclerViewByAge(ascending: Boolean) {
        animalList.sortBy { it.animalAge }
        if (!ascending) animalList.reverse()
        fragmentMainBinding.recyclerViewMain.adapter?.notifyDataSetChanged()
    }

    // RecyclerView를 이름 기준으로 정렬
    fun sortRecyclerViewByName(ascending: Boolean) {
        animalList.sortBy { it.animalName }
        if (!ascending) animalList.reverse()
        fragmentMainBinding.recyclerViewMain.adapter?.notifyDataSetChanged()
    }

    fun filterRecyclerViewByType(animalType: AnimalType) {
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO) {
                val result = AnimalRepository.selectAnimalInfoByType(mainActivity, animalType)
                if (result.isEmpty()) {
                    println("No data found for animalType: ${animalType.str}")
                }
                result
            }
            animalList = work1.await()
            println("Filtered list size: ${animalList.size} for type: ${animalType.str}")
            fragmentMainBinding.recyclerViewMain.adapter?.notifyDataSetChanged()
        }
    }


    // RecyclerView의 어댑터
    inner class RecyclerViewMainAdapter : RecyclerView.Adapter<RecyclerViewMainAdapter.ViewHolderMain>() {
        inner class ViewHolderMain(val rowMainBinding: RowMainBinding) : RecyclerView.ViewHolder(rowMainBinding.root),
            OnClickListener {
            override fun onClick(v: View?) {
                val dataBundle = Bundle()
                dataBundle.putInt("animalIdx", animalList[adapterPosition].animalIdx)
                mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT, true, dataBundle)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMain {
            val rowMainBinding = RowMainBinding.inflate(layoutInflater, parent, false)
            val viewHolderMain = ViewHolderMain(rowMainBinding)
            rowMainBinding.root.setOnClickListener(viewHolderMain)
            return viewHolderMain
        }

        override fun getItemCount(): Int {
            return animalList.size
        }

        override fun onBindViewHolder(holder: ViewHolderMain, position: Int) {
            holder.rowMainBinding.textViewRowMainStudentName.text = animalList[position].animalName
        }
    }
}
