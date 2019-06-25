package dev.kourosh.baseapp.infrastructure.mvvm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders

abstract class BaseShareFragment<B : ViewDataBinding, VM : BaseFragmentViewModel> : BaseFragment<B,VM>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vm = activity?.run {
            ViewModelProviders.of(this).get(viewModelInstance()::class.java)
        } ?: throw Exception("Invalid Activity")
        lifecycle.addObserver(vm)
        binding = DataBindingUtil.inflate(inflater, getLayoutID(), container, false)
        binding.lifecycleOwner = this
        binding.setVariable(getVariable(), vm)
        binding.executePendingBindings()
        return binding.root
    }

}