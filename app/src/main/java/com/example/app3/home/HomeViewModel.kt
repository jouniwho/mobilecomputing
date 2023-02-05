package com.example.app3.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app3.Graph.categoryRepository
import com.example.app3.data.entity.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    private val _selectedCategory = MutableStateFlow<Category?>(null)

    val state: StateFlow<HomeViewState>
        get() = _state

    fun onCategorySelected(category: Category){
        _selectedCategory.value = category
    }

    init{
        viewModelScope.launch {
            combine(
                categoryRepository.categories().onEach { list ->
                    if (list.isNotEmpty() && _selectedCategory.value == null) {
                        _selectedCategory.value = list[0]
                    }
                },
                _selectedCategory
            ) { categories, selectedCategory ->
                HomeViewState(
                    categories = categories,
                    selectedCategory = selectedCategory
                )
            }.collect { _state.value = it }
        }

        loadCategoriesFromDb()

    }

    private fun loadCategoriesFromDb() {
        val list = mutableListOf(
            Category(name = "Time"),
            Category(name = "Location"),
        )
        viewModelScope.launch {
            list.forEach { category -> categoryRepository.addCategory(category) }
        }
    }
}

data class HomeViewState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null
)