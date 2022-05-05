package com.kendis.todoapp.ui.add_edit_todo

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
import com.kendis.todoapp.databinding.FragmentAddEditTodoBinding
import com.kendis.todoapp.ui.todo_list.TodoListEvent
import com.kendis.todoapp.util.UiEvent
import com.wada811.databinding.dataBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditTodoFragment : Fragment() {

    val viewModel: AddEditTodoViewModel by viewModels()
    val binding: FragmentAddEditTodoBinding by dataBinding()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_edit_todo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        binding.fab.setOnClickListener {
            viewModel.onEvent(AddEditTodoEvent.OnSaveTodoClick)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when(event) {
                    is UiEvent.ShowSnackBar -> {
                        Snackbar.make(
                            binding.root, event.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    is UiEvent.PopBackStack -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }

    }
}
