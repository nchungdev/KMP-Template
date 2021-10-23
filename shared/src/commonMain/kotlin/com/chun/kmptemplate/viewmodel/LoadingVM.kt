package com.chun.kmptemplate.viewmodel

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class SimpleLoadingScreen {
    data class Data<T>(val data: T) : SimpleLoadingScreen()
    object Loading : SimpleLoadingScreen()
    data class Error(val errorMessage: String) : SimpleLoadingScreen()
    data class NoData(val message: String) : SimpleLoadingScreen()
}

data class SingleLoadingState(val screen: SimpleLoadingScreen) : BaseState() {
    // Need secondary constructor to initialize with no args in SwiftUI
    constructor() : this(SimpleLoadingScreen.Loading)
}

private fun MutableStateFlow<SingleLoadingState>.update(
    screen: SimpleLoadingScreen,
) {
    value = value.copy(screen = screen)
}

sealed class LoadingEffect : BaseEffect()

interface LoadingEvent : BaseEvent {
    fun retry()
    fun refresh()
    fun loadData(isRefresh: Boolean = false)
}

abstract class LoadingVM<T> : BaseMVIViewModel(), LoadingEvent {
    private val _state = MutableStateFlow(SingleLoadingState())
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoadingEffect>()
    override val effect = _effect.asSharedFlow()

    override val event: LoadingEvent
        get() = this

    init {
        // init of base class is called before the injection of child class
        // which lead to crash when calling loadData()
        // so delay a little bit
        clientScope.launch {
            delay(50)
            loadData()
        }
    }

    override fun retry() {
        loadData()
    }

    override fun refresh() {
        loadData(isRefresh = true)
    }

    override fun loadData(isRefresh: Boolean) {
        clientScope.launch {
            if (isRefresh) {
                _state.update(SimpleLoadingScreen.Loading)
            }
            getData()
                .onSuccess {
                    if (it == null) {
                        _state.update(SimpleLoadingScreen.NoData("No Data"))
                    } else {
                        _state.update(SimpleLoadingScreen.Data(it))
                    }
                }
                .onFailure {
                    _state.update(SimpleLoadingScreen.Error(it.message.orEmpty()))
                }
        }
    }

    abstract suspend fun getData(): Result<T>
}
