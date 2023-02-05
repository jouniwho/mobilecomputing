package com.example.app3.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app3.Graph
import com.example.app3.data.entity.User
import com.example.app3.data.repository.UserRepository
import com.example.app3.home.HomeViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val userRepository: UserRepository = Graph.userRepository,
): ViewModel() {
    private val _state = MutableStateFlow(UserViewState())

    val state: StateFlow<UserViewState>
        get() = _state

    suspend fun saveUser(user: User): Long {
        return userRepository.addUser(user)
    }

    init {
        viewModelScope.launch {
            userRepository.users().collect { users ->
                _state.value = UserViewState(users)
            }
        }

        loadTestUserFromDb()
    }

    private fun loadTestUserFromDb() {
        val list = mutableListOf(
            User(username = "test", password = "test123"),
            User(username = "test2", password = "test123"),
        )
        viewModelScope.launch {
            list.forEach { user -> Graph.userRepository.addUser(user) }
        }
    }

}

data class UserViewState(
    val users: List<User> = emptyList()
)