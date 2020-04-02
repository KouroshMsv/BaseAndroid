package dev.kourosh.baseapp.infrastructure.mvvm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider

abstract class BaseShareFragment<B : ViewDataBinding, VM : BaseFragmentViewModel>(
    @LayoutRes private val layoutId: Int,
    @IdRes private val variable: Int,
    private val viewModelInstance: VM
) : BaseFragment<B, VM>(layoutId, variable, viewModelInstance) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = activity?.run {
            ViewModelProvider(this).get(viewModelInstance::class.java)
        } ?: throw Exception("Invalid Activity")
        lifecycle.addObserver(vm)
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        binding.setVariable(variable, vm)
        binding.executePendingBindings()
        return binding.root
    }

}