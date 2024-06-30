package uz.duol.sizscanner.presentation.ui.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.duol.sizscanner.R
import uz.duol.sizscanner.data.model.TaskItemInfo
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.databinding.TaskItemBinding

class TaskItemListAdapter : RecyclerView.Adapter<TaskItemListAdapter.ViewHolder>() {

    var clickItem:((TaskItemResponse) -> Unit)? = null
    private var loader: (() -> Unit)? = null
    fun setLoader(block: () -> Unit) {
        loader = block
    }

    private val diffUtilCallback = object : DiffUtil.ItemCallback<TaskItemResponse>() {
        override fun areItemsTheSame(oldItem: TaskItemResponse, newItem: TaskItemResponse) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskItemResponse, newItem: TaskItemResponse) =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffUtilCallback)

    inner class ViewHolder(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val taskItemInfo = differ.currentList[position]
            binding.productName.text = taskItemInfo.productName?:""
            binding.gtinName.text = taskItemInfo.gtin?:""
            binding.allCountText.text = (taskItemInfo.totalKM?:"").toString()
            if (taskItemInfo.totalKM!=null && taskItemInfo.soldKM!=null){
                binding.failedCountText.text = (taskItemInfo.totalKM-taskItemInfo.soldKM).toString()
            }
            binding.successCountText.text = (taskItemInfo.soldKM?:"").toString()
            binding.llTaskItem.setOnClickListener {
                clickItem?.invoke(taskItemInfo)
            }

            if (adapterPosition + 1 >= itemCount) {
                loader?.invoke()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }



}