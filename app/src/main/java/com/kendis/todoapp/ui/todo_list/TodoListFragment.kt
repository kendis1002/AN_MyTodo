package com.kendis.todoapp.ui.todo_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kendis.todoapp.R
import com.kendis.todoapp.databinding.FragmentTodoListListBinding
import com.kendis.todoapp.ui.add_edit_todo.AddEditTodoFragment
import com.kendis.todoapp.ui.add_edit_todo.AddEditTodoFragmentArgs
import com.kendis.todoapp.util.Routes
import com.kendis.todoapp.util.UiEvent
import com.wada811.databinding.dataBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TodoListFragment : Fragment() {

    private val viewModel: TodoListViewModel by viewModels()
    private val binding: FragmentTodoListListBinding by dataBinding()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_todo_list_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when(event) {
                    is UiEvent.ShowSnackBar -> {
                        this@TodoListFragment.view?.let {
                            Snackbar.make(
                                it, event.message,
                                Snackbar.LENGTH_LONG
                            ).setAction(event.action) {
                                viewModel.onEvent(TodoListEvent.OnUndoDeleteClick)
                            }.show()
                        }
                    }
                    is UiEvent.Navigate -> {
                        val args = AddEditTodoFragmentArgs.fromBundle(event.bundle)
                        findNavController().navigate(TodoListFragmentDirections.actionTodoListFragmentToAddEditTodoFragment(args.todoId))
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.todos.collect {
                binding.list.adapter.apply {
                    if (this == null) {
                        val adapter = TodoRecyclerViewAdapter(it)
                        adapter.onClickDoneButton = { todo, isCheck ->
                            viewModel.onEvent(TodoListEvent.OnDoneChange(todo, isCheck))
                        }
                        adapter.onClickDeleteButton = { todo ->
                            viewModel.onEvent(TodoListEvent.OnDeleteTodoClick(todo))
                        }
                        adapter.onClick = { todo ->
                            viewModel.onEvent(TodoListEvent.OnTodoClick(todo))
                        }
                        binding.list.adapter = adapter
                    } else {
                        (this as TodoRecyclerViewAdapter).updateList(it)
                    }
                }
            }
        }

        binding.fab.setOnClickListener {
            viewModel.onEvent(TodoListEvent.OnAddTodoClick)
        }
    }
}
