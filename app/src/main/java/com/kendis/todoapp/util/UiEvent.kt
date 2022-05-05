package com.kendis.todoapp.util

import android.os.Bundle

sealed class UiEvent {
    object PopBackStack: UiEvent()
    class Navigate(val route: String, val bundle: Bundle = Bundle()): UiEvent()
    class ShowSnackBar(
        val message: String,
        val action: String?= null
    ): UiEvent()
}