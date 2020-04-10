package dev.kourosh.baseapp.infrastructure.adapter.normal

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import dev.kourosh.baseapp.infrastructure.adapter.BaseBindingViewHolder
import dev.kourosh.baseapp.infrastructure.adapter.OnItemClickListener

abstract class BaseRecyclerAdapter<T, VB : ViewDataBinding>(@LayoutRes private val layoutId: Int) :
    RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder<VB>>() {
    open var items: MutableList<T> = mutableListOf()
        set(value) {
            this.items.clear()
            this.items.addAll(value)
            notifyDataSetChanged()
        }

    var layoutInflater: LayoutInflater? = null
    var onItemClickListener: OnItemClickListener<T>? = null
    var context: Context?=null

    val isEmpty: Boolean
        get() = itemCount == 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VB> {
        context = parent.context
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
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onItemClickListener!!.onItemClicked(items[position])
            }
        }
    }


    class ViewHolder<VB:ViewDataBinding>(binding: VB) : RecyclerView.ViewHolder(binding.root)


    override fun getItemCount() = items.size

    open fun add(item: T) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    open fun addFirst(item: T) {
        items.add(0, item)
        notifyItemInserted(0)

    }

    open fun addAll(items: List<T>) {
        this.items.addAll(items)
        notifyItemRangeInserted(this.items.size - items.size, items.size)
    }

    open fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    open fun remove(item: T) {
        val position = items.indexOf(item)
        if (position > -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}
