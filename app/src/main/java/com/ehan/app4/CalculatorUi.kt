package com.ehan.app4

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorUi {
    private val _topBarChange = MutableStateFlow<TopBarList>(TopBarList.MAIN)
    val topBarChange: StateFlow<TopBarList> = _topBarChange.asStateFlow()

    fun topBarChangeSet(topBarChangeNew: TopBarList) {
        _topBarChange.value = topBarChangeNew
    }
}