package tech.acruxsolutions.atomnote.viewholders

import androidx.recyclerview.widget.RecyclerView
import tech.acruxsolutions.atomenote.databinding.LabelItemBinding

class LabelsViewHolder(private val binding: LabelItemBinding,
                       onLabelClicked: ((position: Int) -> Unit)?,
                       onLabelLongClicked: ((position: Int) -> Unit)?) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onLabelClicked?.invoke(adapterPosition)
        }

        binding.root.setOnLongClickListener {
            onLabelLongClicked?.invoke(adapterPosition)
            return@setOnLongClickListener true
        }
    }

    fun bind(label: String) {
        binding.DisplayLabel.text = label
    }
}