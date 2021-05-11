package com.A306.runnershi.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.A306.runnershi.Model.Run
import com.A306.runnershi.Repository.SingleRunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleRunViewModel @Inject constructor(
    val singleRunRepository: SingleRunRepository
) : ViewModel(){
    fun insertRun(run: Run) = viewModelScope.launch {
        singleRunRepository.insertRun(run)
    }
}