package com.kendis.todoapp.ui.todo_list

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kendis.todoapp.data.Todo
import com.kendis.todoapp.data.TodoRepository
import com.kendis.todoapp.util.Routes
import com.kendis.todoapp.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    val todos = repository.getTodos()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deletedTodo: Todo? = null

    init {
        Log.d("Viewmode", "recreate")
    }

    fun onEvent(event: TodoListEvent) {
        when (event) {
            is TodoListEvent.OnTodoClick -> {
                val bundle = Bundle()
                bundle.putInt("todoId", event.todo.id?:-1)
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO, bundle))
            }
            is TodoListEvent.OnAddTodoClick -> {
                val bundle = Bundle()
                bundle.putInt("todoId", -1)
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO, bundle))
            }
            is TodoListEvent.OnDeleteTodoClick -> {
                deletedTodo = event.todo
                viewModelScope.launch {
                    deletedTodo = event.todo
                    repository.deleteTodo(event.todo)
                    sendUiEvent(UiEvent.ShowSnackBar(
                        message = "Todo Deleted",
                        action = "Undo"
                    ))
                }
            }
            is TodoListEvent.OnDoneChange -> {
                viewModelScope.launch {
                    repository.insertTodo(
                        event.todo.copy(
                            isDone = event.isDone
                        )
                    )
                }
            }
            is TodoListEvent.OnUndoDeleteClick -> {
                viewModelScope.launch {
                    deletedTodo?.let { repository.insertTodo(it) }
                }
            }
            else -> {}
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}