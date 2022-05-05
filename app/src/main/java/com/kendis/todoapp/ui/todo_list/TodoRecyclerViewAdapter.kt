package com.kendis.todoapp.ui.todo_list

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.kendis.todoapp.data.Todo
import com.kendis.todoapp.databinding.FragmentTodoListBinding
import com.kendis.todoapp.util.strike

class TodoRecyclerViewAdapter(
    private var values: List<Todo>
) : RecyclerView.Adapter<TodoRecyclerViewAdapter.ViewHolder>() {

    var onClickDoneButton: ((todo: Todo, isDone: Boolean) -> Unit)? = null
    var onClickDeleteButton: ((todo: Todo) -> Unit)? = null
    var onClick: ((todo: Todo) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentTodoListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.titleView.text = item.title
        holder.titleView.strike = item.isDone
        holder.descriptionView.text = item.description
        holder.descriptionView.strike = item.isDone
        holder.isDoneView.isChecked = item.isDone
    }

    override fun getItemCount(): Int = values.size

    fun updateList(list: List<Todo>) {
        values = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: FragmentTodoListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val titleView: TextView = binding.title
        val descriptionView: TextView = binding.content
        val isDoneView: CheckBox = binding.isDone
        private val deleteView: View = binding.deleteView

        init {
            isDoneView.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != values[absoluteAdapterPosition].isDone) {
                    onClickDoneButton?.invoke(values[absoluteAdapterPosition], isChecked)
                }
            }
            deleteView.setOnClickListener {
                onClickDeleteButton?.invoke(values[absoluteAdapterPosition])
            }
            binding.root.setOnClickListener {
                onClick?.invoke(values[absoluteAdapterPosition])
            }
        }
    }

}