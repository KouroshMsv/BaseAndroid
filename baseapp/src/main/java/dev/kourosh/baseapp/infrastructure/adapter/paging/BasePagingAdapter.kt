package dev.kourosh.baseapp.infrastructure.adapter.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import dev.kourosh.baseapp.infrastructure.adapter.BaseBindingViewHolder
import dev.kourosh.baseapp.infrastructure.adapter.OnItemClickListener

abstract class BasePagingAdapter<T : Any, VB : ViewDataBinding, VH : BaseBindingViewHolder<VB>>(
    diffUtil: DiffUtil.ItemCallback<T>
) :PagedListAdapter<T, VH>(diffUtil) {
    lateinit var context:Context
    var onItemClickListener: OnItemClickListener<T>? = null
    var layoutInflater: LayoutInflater? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        context=parent.context
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

    abstract fun getViewHolder(vb: VB): VH

    @LayoutRes
    abstract fun getRootLayout(): Int

}

