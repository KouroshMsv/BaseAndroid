package dev.kourosh.baseapp.infrastructure.adapter.normal

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import dev.kourosh.baseapp.infrastructure.adapter.OnItemClickListener

abstract class BaseRecyclerAdapter<T, VB : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
    private val autoAssignRootClickListener: Boolean = true
) : RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder<VB>>() {

    protected var onItemClickListener: OnItemClickListener<T>? = null

    fun setOnItemClickListener(onClicked: (T) -> (Unit)) {
        onItemClickListener = object : OnItemClickListener<T> {
            override fun onItemClicked(item: T) {
                onClicked(item)
            }
        }
    }

    protected var layoutInflater: LayoutInflater? = null
    protected var context: Context? = null

    protected var elements: MutableList<T> = mutableListOf()

    open fun setItems(items: List<T>) {
        val itemsSize = items.size
        this.elements.clear()
        this.elements.addAll(items)
        notifyItemRangeInserted(0, itemsSize)
    }

    open fun getItems(): List<T> {
        return elements
    }

    val isEmpty: Boolean
        get() = itemCount == 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VB> {
        context = parent.context
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        return ViewHolder(DataBindingUtil.inflate(layoutInflater!!, layoutId, parent, false) as VB)
    }

    override fun onBindViewHolder(holder: ViewHolder<VB>, position: Int) {
        if (onItemClickListener != null && autoAssignRootClickListener) {
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClicked(elements[holder.bindingAdapterPosition])
            }
        }
    }

    override fun getItemCount() = elements.size


    open fun add(item: T) {
        elements.add(item)
        notifyItemInserted(elements.size - 1)
    }

    open fun addFirst(item: T) {
        elements.add(0, item)
        notifyItemInserted(0)

    }

    open fun addAll(items: List<T>) {
        this.elements.addAll(items)
        notifyItemRangeInserted(this.elements.size - items.size, items.size)
    }

    open fun clear() {
        val itemLength = elements.size
        elements.clear()
        notifyItemRangeRemoved(0, itemLength)
    }

    open fun remove(item: T) {
        val position = elements.indexOf(item)
        if (position > -1) {
            elements.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class ViewHolder<VB : ViewDataBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

}
