package com.taqsiim.compusconnect.mvi

/**
 * Generic UI state wrapper used across all MVI ViewModels.
 */
sealed class UiState<out T> {
    /** No operation has been requested yet. */
    data object Idle : UiState<Nothing>()

    /** A loading operation is in progress. */
    data object Loading : UiState<Nothing>()

    /** The operation completed successfully with [data]. */
    data class Success<T>(val data: T) : UiState<T>()

    /** The operation failed with an [message]. */
    data class Error(val message: String) : UiState<Nothing>()
}
