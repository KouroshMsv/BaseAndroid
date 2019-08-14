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

abstract class BaseRecyclerAdapter<T, VB : ViewDataBinding, VH : BaseBindingViewHolder<VB>> :RecyclerView.Adapter<VH>() {
    open var items: MutableList<T> = mutableListOf()
        set(value) {
            this.items.clear()
            this.items.addAll(value)
            notifyDataSetChanged()
        }

    var layoutInflater: LayoutInflater? = null

    var onItemClickListener: OnItemClickListener<T>? = null
    lateinit var context: Context

    val isEmpty: Boolean
        get() = itemCount == 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        context = parent.context!!
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        return getViewHolder(
            DataBindingUtil.inflate(
                layoutInflater!!,
                getRootLayout(),
                parent,
                false
            ) as VB
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onItemClickListener!!.onItemClicked(items[position])
            }
        }
    }

    @LayoutRes
    abstract fun getRootLayout(): Int

    abstract fun getViewHolder(vb: VB): VH

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
