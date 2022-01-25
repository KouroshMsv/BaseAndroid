package dev.kourosh.baseapp.infrastructure.mvvm.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.text.inSpans
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dev.kourosh.baseapp.dp
import dev.kourosh.baseapp.enums.Duration
import dev.kourosh.baseapp.enums.MessageType
import dev.kourosh.baseapp.hideKeyboard
import io.github.inflationx.calligraphy3.CalligraphyTypefaceSpan
import io.github.inflationx.calligraphy3.CalligraphyUtils
import io.github.inflationx.calligraphy3.TypefaceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


abstract class BaseDialog<B : ViewDataBinding, VM : BaseDialogViewModel>(@LayoutRes private val layoutId: Int, @IdRes private val variable: Int, private val viewModelInstance: VM) : DialogFragment() {
    protected lateinit var vm: VM
    protected lateinit var binding: B
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vm = ViewModelProvider(this)[viewModelInstance::class.java]
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        binding.setVariable(variable, vm)
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        vm.hideKeyboard.observe(this) {
            if (it) {
                hideKeyboard(requireActivity())
            }
        }
        vm.messageEvent.observe(this) {
            if (it != null)
                showSnackBar(it.message, it.messageType)
        }
        initialize()
    }

    protected fun showSnackBar(message: String, messageType: MessageType) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        val view = toast.view!!
        view.setPadding(16.dp, 16.dp, 16.dp, 16.dp)
        ViewCompat.setLayoutDirection(view, ViewCompat.LAYOUT_DIRECTION_RTL)
        val text = view.findViewById<TextView>(android.R.id.message)
        CalligraphyUtils.applyFontToTextView(context, text, "fonts/isM.ttf")
        text.setTextColor(ContextCompat.getColor(requireContext(), messageType.textColor))
        view.background = ContextCompat.getDrawable(requireContext(), messageType.drawable)
        toast.view = view
        toast.show()
    }

    open fun show(manager: FragmentManager) = super.show(manager, this.javaClass.simpleName)

    protected abstract fun initialize()
    protected fun launchIO(block: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            block(this)
        }
    }
}
