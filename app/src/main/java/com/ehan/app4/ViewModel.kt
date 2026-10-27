package com.ehan.app4

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    val calculatorUi = CalculatorUi()

    val topBarChange = calculatorUi.topBarChange

    fun topBarChangeSet(topBarChangeNew: TopBarList) {
        calculatorUi.topBarChangeSet(topBarChangeNew = topBarChangeNew)
    }
}