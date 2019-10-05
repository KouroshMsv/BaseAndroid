package dev.kourosh.baseapp.infrastructure.mvvm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dev.kourosh.baseapp.dialogs.NetworkErrorDialog
import dev.kourosh.baseapp.enums.MessageType
import dev.kourosh.baseapp.hideKeyboard

abstract class BaseFragment<B : ViewDataBinding, VM : BaseFragmentViewModel>(
    @LayoutRes private val layoutId: Int,
    @IdRes private val variable: Int,
    val viewModelInstance: VM
) : Fragment() {
    lateinit var vm: VM
    lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProviders.of(this)
            .get(viewModelInstance::class.java)
        lifecycle.addObserver(vm)
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        binding.setVariable(variable, vm)
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.errorMessage.observe(viewLifecycleOwner, Observer {
            showSnackBar(it!!, MessageType.ERROR)
        })
        vm.successMessage.observe(viewLifecycleOwner, Observer {
            showSnackBar(it!!, MessageType.SUCCESS)
        })
        vm.infoMessage.observe(viewLifecycleOwner, Observer {
            showSnackBar(it!!, MessageType.INFO)
        })
        vm.warningMessage.observe(viewLifecycleOwner, Observer {
            showSnackBar(it!!, MessageType.WARNING)
        })
        vm.hideKeyboard.observe(
            viewLifecycleOwner,
            Observer { if (it) hideKeyboard(requireActivity()) })
        binding.root.setOnClickListener {
            hideKeyboard(requireActivity())
        }

        val dialog = NetworkErrorDialog()
        var showingDialog = false
        vm.networkError.observe(this, Observer {
            if (it != null && !showingDialog) {
                showingDialog = true
                dialog.showCancel = it
                dialog.onRetryClickListener = View.OnClickListener {
                    onNetworkErrorTryAgain()
                    vm.networkError.value = null
                    showingDialog = false
                    dialog.dismiss()
                }
                dialog.onCancelClickListener = View.OnClickListener {
                    onNetworkErrorCancel()
                    vm.networkError.value = null
                    showingDialog = false
                    dialog.dismiss()
                }
                dialog.show(childFragmentManager)
            }
        })
        observeVMVariable()
    }

    fun showSnackBar(message: String, messageType: MessageType) {
        dev.kourosh.baseapp.showSnackBar(
            binding.root, requireContext(), message, messageType
        )
    }

    abstract fun observeVMVariable()
    abstract fun onNetworkErrorTryAgain()
    abstract fun onNetworkErrorCancel()
}