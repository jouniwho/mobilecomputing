package com.example.app3.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app3.Graph
import com.example.app3.data.entity.Category
import com.example.app3.data.entity.Payment
import com.example.app3.data.repository.CategoryRepository
import com.example.app3.data.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val paymentRepository: PaymentRepository = Graph.paymentRepository,
    private val categoryRepository: CategoryRepository = Graph.categoryRepository
): ViewModel() {
    private val _state = MutableStateFlow(PaymentViewState())

    val state: StateFlow<PaymentViewState>
        get() = _state

    suspend fun savePayment(payment: Payment): Long {
        return paymentRepository.addPayment(payment)
    }

    init {
        viewModelScope.launch {
            categoryRepository.categories().collect { categories ->
                _state.value = PaymentViewState(categories)
            }
        }
    }
}

data class PaymentViewState(
    val categories: List<Category> = emptyList()
)