package com.example.app3.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app3.Graph
import com.example.app3.data.entity.User
import com.example.app3.data.repository.UserRepository
import kotlinx.coroutines.flow.*
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

    suspend fun getUser(userId: Long) : User? {
        return userRepository.getUserWithId(userId)
    }

    suspend fun updateUser(user: User) {
        return userRepository.editUser(user)
    }

    suspend fun deleteUser(user: User) {
        return userRepository.deleteUser(user)
    }

    suspend fun getUsersList(){
        userRepository.users()
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
            User(username = "test", password = "test123", loggedIn = false),
            User(username = "test2", password = "test123", loggedIn = false),
        )
        viewModelScope.launch {
            list.forEach { user -> Graph.userRepository.addUser(user) }
        }
    }

}

data class UserViewState(
    val users: List<User> = emptyList()
)