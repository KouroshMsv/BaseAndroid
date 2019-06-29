package dev.kourosh.baseapp.infrastructure.mvvm.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dev.kourosh.baseapp.dialogs.NetworkErrorDialog
import dev.kourosh.baseapp.enums.MessageType
import dev.kourosh.baseapp.hideKeyboard
import io.github.inflationx.viewpump.ViewPumpContextWrapper


abstract class BaseActivity<B : ViewDataBinding, VM : BaseActivityViewModel> : AppCompatActivity() {

    protected val vm: VM by lazy {
        ViewModelProviders.of(this)
            .get(viewModelInstance()::class.java)
    }

    lateinit var binding: B
        private set

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        bind()
        vm.hideKeyboard.observe(this, Observer {
            if (it) {
                hideKeyboard(this)
            }
        })
        vm.errorMessage.observe(this, Observer {
            showSnackBar(it!!, MessageType.ERROR)
        })
        vm.successMessage.observe(this, Observer {
            showSnackBar(it!!, MessageType.SUCCESS)
        })
        vm.infoMessage.observe(this, Observer {
            showSnackBar(it!!, MessageType.INFO)
        })
        vm.warningMessage.observe(this, Observer {
            showSnackBar(it!!, MessageType.WARNING)
        })
        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard(this)
            return@setOnTouchListener false
        }
        val dialog = NetworkErrorDialog()
        var showingDialog = false
        vm.networkError.observe(this, Observer {
            if (it != null && !showingDialog) {
                showingDialog = true
                dialog.showCancel=it
                dialog.onRetryClickListener = View.OnClickListener {
                    tryAgain()
                    vm.networkError.value = null
                    showingDialog = false
                    dialog.dismiss()
                }
                dialog.onCancelClickListener = View.OnClickListener {
                    vm.networkError.value = null
                    showingDialog = false
                    dialog.dismiss()
                }
                dialog.show(supportFragmentManager)
            }


        })
    }

    private fun bind() {
        binding = DataBindingUtil.setContentView(this, getLayoutID())
        binding.lifecycleOwner = this
        binding.setVariable(getVariable(), vm)
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

    @LayoutRes
    abstract fun getLayoutID(): Int

    @IdRes
    abstract fun getVariable(): Int

    abstract fun viewModelInstance(): VM

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    abstract fun tryAgain()


}
