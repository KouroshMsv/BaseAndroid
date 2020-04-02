package dev.kourosh.baseapp.infrastructure.adapter.normal

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import dev.kourosh.baseapp.autoNotify
import dev.kourosh.baseapp.infrastructure.adapter.BaseBindingViewHolder
import dev.kourosh.baseapp.infrastructure.adapter.OnItemClickListener
import kotlin.properties.Delegates

abstract class BaseRecyclerAdapterWithDiffUtil<T, VB : ViewDataBinding, VH : BaseBindingViewHolder<VB>> :
    RecyclerView.Adapter<VH>() {
    abstract val compare: (T, T) -> Boolean
    var items: List<T> by Delegates.observable(emptyList()) { prop, oldList, newList ->
        autoNotify(oldList, newList, compare)
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

}
