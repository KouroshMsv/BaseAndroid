package dev.kourosh.baseapp.dialogs

import android.os.Bundle
import android.view.View
import com.google.android.material.button.MaterialButton
import dev.kourosh.baseapp.R
import dev.kourosh.baseapp.gone
import dev.kourosh.baseapp.infrastructure.dialog.BaseDialog


open class NetworkErrorDialog : BaseDialog(R.layout.dialog_network_error) {
    protected val btnCancel :MaterialButton by lazy {
        requireView().findViewById(R.id.btnCancel)
    }
    protected val btnTryAgain :MaterialButton by lazy {
        requireView().findViewById(R.id.btnTryAgain)
    }
    var showCancel = true
    private var onRetryClickListener: View.OnClickListener? = null
    private var onCancelClickListener: View.OnClickListener? = null
    fun setOnRetryClickListener(listener: View.OnClickListener) {
        onRetryClickListener = listener
    }

    fun setOnCancelClickListener(listener: View.OnClickListener) {
        onCancelClickListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)

    }

    override fun initView(v: View) {
        if (!showCancel)
            btnCancel.gone()
        btnTryAgain.setOnClickListener(onRetryClickListener)
        btnCancel.setOnClickListener(onCancelClickListener)
    }
}