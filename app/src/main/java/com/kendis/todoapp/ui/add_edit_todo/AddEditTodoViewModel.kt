package com.kendis.todoapp.ui.add_edit_todo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kendis.todoapp.data.Todo
import com.kendis.todoapp.data.TodoRepository
import com.kendis.todoapp.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val repository: TodoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val todo = MutableStateFlow<Todo?>(null)

    val title = MutableStateFlow("")

    val description = MutableStateFlow("")

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val todoId = savedStateHandle.get<Int>("todoId") ?: -1
        if (todoId != -1) {
            viewModelScope.launch {
                repository.getTodoById(todoId)?.let { todo ->
                    title.value = todo.title
                    description.value = todo.description
                    this@AddEditTodoViewModel.todo.value = todo
                }
            }
        }
    }

    fun onEvent(event: AddEditTodoEvent) {
        when (event) {
            is AddEditTodoEvent.OnTitleChanged -> {
                title.value = event.title
            }
            is AddEditTodoEvent.OnDescriptionChanged -> {
                description.value = event.description
            }
            is AddEditTodoEvent.OnSaveTodoClick -> {
                viewModelScope.launch {
                    if (title.value.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackBar(
                            message = "The title can't be empty"
                        ))
                        return@launch
                    }
                    repository.insertTodo(
                        Todo(
                            title = title.value,
                            description = description.value,
                            isDone = todo.value?.isDone?: false,
                            id = todo.value?.id
                        ))
                    sendUiEvent(UiEvent.PopBackStack)
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}