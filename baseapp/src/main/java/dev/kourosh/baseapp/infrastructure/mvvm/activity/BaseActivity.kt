package dev.kourosh.baseapp.infrastructure.mvvm.activity

import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.kourosh.baseapp.dialogs.NetworkErrorDialog
import dev.kourosh.baseapp.enums.MessageType
import dev.kourosh.baseapp.hideKeyboard
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


abstract class BaseActivity<B : ViewDataBinding, VM : BaseActivityViewModel>(@LayoutRes private val layoutId: Int, @IdRes private val variable: Int, private val viewModelInstance: VM) : AppCompatActivity() {

    protected val vm: VM by lazy {
        ViewModelProvider(this)[viewModelInstance::class.java]
    }

    private var _binding: B? = null

    protected val binding: B
        get() = _binding!!


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind()
        vm.hideKeyboard.observe(this) {
            if (it) {
                hideKeyboard(this)
            }
        }
        vm.messageEvent.observe(this) {
            if (it != null)
                showSnackBar(it.message, it.messageType)
        }
        binding.root.setOnTouchListener { view, _ ->
            hideKeyboard(this)
            view.performClick()
            return@setOnTouchListener false
        }
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
                dialog.show(supportFragmentManager)
            }


        }
        observeVMVariable()
    }

    private fun bind() {
        _binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        binding.setVariable(variable, vm)
        binding.executePendingBindings()
        lifecycle.addObserver(vm)
    }

    fun showSnackBar(message: String, messageType: MessageType) {
        dev.kourosh.baseapp.showSnackBar(
                binding.root,
                applicationContext,
                message,
                messageType
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding?.unbind()
        _binding = null
    }
    abstract fun observeVMVariable()
    abstract fun onNetworkErrorTryAgain()
    abstract fun onNetworkErrorCancel()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    fun launchIO(block: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            block(this)
        }
    }
    fun launchMain(block: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            block(this)
        }
    }

}
