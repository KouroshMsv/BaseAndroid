package dev.kourosh.baseapp.infrastructure.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

abstract class BaseDialog(@LayoutRes private val layoutId: Int, private val backgroundTransparent: Boolean = true, private val cancellable: Boolean = false) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = cancellable
        if (backgroundTransparent)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initView(view)
    }

    open fun show(manager: FragmentManager) = super.show(manager, this.javaClass.simpleName)

    protected abstract fun initView(v: View)
}