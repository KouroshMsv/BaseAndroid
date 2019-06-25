package dev.kourosh.baseapp.infrastructure.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class BaseBindingViewHolder<VB : ViewDataBinding>(val binding: VB) :
    RecyclerView.ViewHolder(binding.root)