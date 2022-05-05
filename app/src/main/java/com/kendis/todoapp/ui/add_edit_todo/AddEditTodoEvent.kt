package com.kendis.todoapp.ui.add_edit_todo

sealed class AddEditTodoEvent {
    class OnTitleChanged(val title: String): AddEditTodoEvent()
    class OnDescriptionChanged(val description: String): AddEditTodoEvent()
    object OnSaveTodoClick: AddEditTodoEvent()
}