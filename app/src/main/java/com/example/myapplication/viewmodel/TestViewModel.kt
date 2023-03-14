package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.mockserver.MockServer
import com.example.myapplication.model.TestDataModel
import kotlinx.coroutines.*

class TestViewModelState {

    private val loading = mutableStateOf(false)
    fun toggleLoading(isLoading: Boolean){
        loading.value = isLoading
    }
}

class TestViewModel: ViewModel() {

    private val state = TestViewModelState()
    private val list = mutableStateListOf<TestDataModel>()

    fun fetchData(offset: Int, limit: Int) = viewModelScope.launch {

        state.toggleLoading(true)

        try{

            coroutineScope {
                val fetch = async(Dispatchers.IO){
                    delay(500)
                    MockServer.fetchData(offset, limit)
                }

                val tempList = fetch.await()
                state.toggleLoading(false)
                list.addAll(tempList)
            }

        }
        catch (e: Exception) {
            e.printStackTrace()
            state.toggleLoading(false)
        }

    }
}