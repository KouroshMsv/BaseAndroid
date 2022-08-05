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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.kourosh.baseapp.dialogs.NetworkErrorDialog
import dev.kourosh.baseapp.enums.MessageType
import dev.kourosh.baseapp.hideKeyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseFragment<B : ViewDataBinding, VM : BaseFragmentViewModel>(@LayoutRes private val layoutId: Int, @IdRes private val variable: Int, private val viewModelInstance: VM) : Fragment() {

    lateinit var vm: VM
    protected var _binding: B? = null

    val binding: B
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vm = ViewModelProvider(this)[viewModelInstance::class.java]
        lifecycle.addObserver(vm)
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        binding.setVariable(variable, vm)
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.messageEvent.observe(this) {
            if (it != null)
                showSnackBar(it.message, it.messageType)
        }
        vm.hideKeyboard.observe(viewLifecycleOwner) { if (it) hideKeyboard(requireActivity()) }
        binding.root.setOnClickListener { hideKeyboard(requireActivity()) }

        val dialog = NetworkErrorDialog()
        var showingDialog = false
        vm.networkError.observe(this) {
            if (it != null && !showingDialog) {
                showingDialog = true
                dialog.showCancel = it
                dialog.setOnRetryClickListener {
                    onNetworkErrorTryAgain()
                    vm.networkError.value = null
                    showingDialog = false
                    dialog.dismiss()
                }
                dialog.setOnCancelClickListener {
                    onNetworkErrorCancel()
                    vm.networkError.value = null
                    showingDialog = false
                    dialog.dismiss()
                }
                dialog.show(childFragmentManager)
            }
        }
        observeVMVariable()
    }

    fun showSnackBar(message: String, messageType: MessageType) {
        dev.kourosh.baseapp.showSnackBar(binding.root, requireContext(), message, messageType)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.unbind()
        _binding = null
    }

    abstract fun observeVMVariable()
    abstract fun onNetworkErrorTryAgain()
    abstract fun onNetworkErrorCancel()

    fun launchIO(block: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            block(this)
        }
    }
}