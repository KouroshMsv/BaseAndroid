package dev.kourosh.baseapp.infrastructure.adapter.normal

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import dev.kourosh.baseapp.autoNotify
import dev.kourosh.baseapp.infrastructure.adapter.OnItemClickListener
import kotlin.properties.Delegates

abstract class BaseRecyclerAdapterWithDiffUtil<T, VB : ViewDataBinding>(@LayoutRes private val layoutId: Int) :
    RecyclerView.Adapter<BaseRecyclerAdapterWithDiffUtil.ViewHolder<VB>>() {
    protected var onItemClickListener: OnItemClickListener<T>? = null
    protected abstract val areItemsTheSameCompare: (T, T) -> Boolean
    protected open val areContentsTheSameCompare: (T, T) -> Boolean = { t1, t2 -> t1 == t2 }
    protected var layoutInflater: LayoutInflater? = null
    protected lateinit var context: Context
    var items: List<T> by Delegates.observable(emptyList()) { prop, oldList, newList ->
        autoNotify(oldList, newList, areItemsTheSameCompare, areContentsTheSameCompare)
    }

    var withAutoAssignClickListener: Boolean = true
    val isEmpty: Boolean
        get() = itemCount == 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VB> {
        context = parent.context!!
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        return ViewHolder(
            DataBindingUtil.inflate(
                layoutInflater!!,
                layoutId,
                parent,
                false
            ) as VB
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<VB>, position: Int) {
        if (onItemClickListener != null && withAutoAssignClickListener) {
            holder.itemView.setOnClickListener {
                onItemClickListener!!.onItemClicked(items[holder.adapterPosition])
            }
        }
    }


    override fun getItemCount() = items.size

    fun setOnItemClickListener(onClicked: (T) -> (Unit)) {
        onItemClickListener = object : OnItemClickListener<T> {
            override fun onItemClicked(item: T) {
                onClicked(item)
            }
        }
    }

    class ViewHolder<VB : ViewDataBinding>(val binding: VB) :
        RecyclerView.ViewHolder(binding.root)

}
