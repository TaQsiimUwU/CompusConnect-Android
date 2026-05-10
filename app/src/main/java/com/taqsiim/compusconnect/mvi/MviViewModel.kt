package com.taqsiim.compusconnect.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base MVI ViewModel providing a unidirectional data-flow contract.
 *
 * @param State   Immutable data class representing the full screen state.
 * @param Intent  Sealed class/interface representing user actions.
 * @param Effect  Sealed class/interface representing one-shot side-effects
 *                (snackbar, navigation, toast, etc.).
 */
abstract class MviViewModel<State, Intent, Effect> : ViewModel() {

    /** The initial state the screen starts with. */
    protected abstract fun createInitialState(): State

    private val _state: MutableStateFlow<State> by lazy { MutableStateFlow(createInitialState()) }

    /** Observable screen state. Collect in the composable via `collectAsState()`. */
    val state: StateFlow<State> by lazy { _state.asStateFlow() }

    private val _effect = Channel<Effect>(Channel.BUFFERED)

    /** One-shot side-effects. Collect in the composable via `LaunchedEffect`. */
    val effect = _effect.receiveAsFlow()

    /** Entry point for the UI to send user actions. */
    fun processIntent(intent: Intent) {
        handleIntent(intent)
    }

    /** Subclasses implement this to react to each [Intent]. */
    protected abstract fun handleIntent(intent: Intent)

    /** Thread-safe state update using a reducer lambda. */
    protected fun setState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }

    /** Emit a one-shot side-effect to the UI. */
    protected fun sendEffect(effectValue: Effect) {
        viewModelScope.launch {
            _effect.send(effectValue)
        }
    }

    /** Convenience: current snapshot of state (non-flow). */
    protected val currentState: State get() = _state.value
}
