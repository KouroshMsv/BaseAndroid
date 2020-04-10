package dev.kourosh.baseapp.infrastructure.adapter.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.kourosh.baseapp.infrastructure.adapter.OnItemClickListener

abstract class BasePagingAdapter<T : Any, VB : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
    diffUtil: DiffUtil.ItemCallback<T>
) : PagedListAdapter<T, BasePagingAdapter.ViewHolder<VB>>(diffUtil) {
    private var onItemClickListener: OnItemClickListener<T>? = null
    protected lateinit var context: Context
        private set
    protected var layoutInflater: LayoutInflater? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VB> {
        context = parent.context
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context)
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

    fun setOnItemClickListener(onClicked: (T) -> (Unit)) {
        onItemClickListener = object : OnItemClickListener<T> {
            override fun onItemClicked(item: T) {
                onClicked(item)
            }
        }
    }

    class ViewHolder<VB : ViewDataBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

}

