package uz.duol.sizscanner.presentation.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.duol.sizscanner.R
import uz.duol.sizscanner.data.remote.response.TaskResponse
import uz.duol.sizscanner.databinding.TasksItemBinding
import uz.duol.sizscanner.utils.TaskStatus
import uz.duol.sizscanner.utils.gone
import uz.duol.sizscanner.utils.visible

class TasksListAdapter : RecyclerView.Adapter<TasksListAdapter.ViewHolder>() {

    var clickItem: ((TaskResponse) -> Unit)? = null

    private var loader: (() -> Unit)? = null
    fun setLoader(block: () -> Unit) {
        loader = block
    }

    private val diffUtilCallback = object : DiffUtil.ItemCallback<TaskResponse>() {
        override fun areItemsTheSame(oldItem: TaskResponse, newItem: TaskResponse) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskResponse, newItem: TaskResponse) = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffUtilCallback)

    inner class ViewHolder(private val binding: TasksItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val taskInfo = differ.currentList[position]
            binding.taskIdNumber.text = "#${taskInfo.id}"
            binding.numberOfProduct.text = (taskInfo.countKM ?: "").toString()
            binding.date.text = taskInfo.dateStr?.substring(0,10)?:""

            when (taskInfo.conditionStatus){

                TaskStatus.NEW.name -> {
                    binding.taskStatus.visible()
                    binding.taskStatus.setBackgroundResource(R.drawable.new_status_back)
                    binding.taskStatus.text = binding.root.context.getString(R.string.new_status)
                    binding.taskStatus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.new_status_text))

                }

                TaskStatus.PROCESS.name -> {
                    binding.taskStatus.visible()
                    binding.taskStatus.setBackgroundResource(R.drawable.process_status_back)
                    binding.taskStatus.text = binding.root.context.getString(R.string.process)
                    binding.taskStatus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.process_status_text))
                }

                TaskStatus.CLOSED.name -> {
                    binding.taskStatus.visible()
                    binding.taskStatus.setBackgroundResource(R.drawable.closed_status_back)
                    binding.taskStatus.text = binding.root.context.getString(R.string.closed)
                    binding.taskStatus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.closed_status_text))
                }

                else -> {
                    binding.taskStatus.gone()
                }

            }

            binding.moreBtn.setOnClickListener {
                clickItem?.invoke(taskInfo)
            }

            binding.llTaskItem.setOnClickListener {
                clickItem?.invoke(taskInfo)
            }



            if (adapterPosition + 1 >= itemCount) {
                loader?.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TasksItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}