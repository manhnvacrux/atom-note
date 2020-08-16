package tech.acruxsolutions.atomnote.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.acruxsolutions.atomenote.databinding.ListItemBinding
import tech.acruxsolutions.atomnote.interfaces.ListItemListener
import tech.acruxsolutions.atomnote.viewholders.MakeListViewHolder
import tech.acruxsolutions.atomnote.xml.ListItem
import java.util.*

class MakeListAdapter(private val context: Context, var items: ArrayList<ListItem>) :
    RecyclerView.Adapter<MakeListViewHolder>() {

    var listItemListener: ListItemListener? = null

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MakeListViewHolder, position: Int) {
        val listItem = items[position]
        holder.bind(listItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakeListViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MakeListViewHolder(
            binding,
            listItemListener
        )
    }
}