package com.A306.runnershi.ViewModel

import androidx.lifecycle.ViewModel
import com.A306.runnershi.Repository.SingleRunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SingleRunViewModel @Inject constructor(
    val singleRunRepository: SingleRunRepository
) : ViewModel(){
}