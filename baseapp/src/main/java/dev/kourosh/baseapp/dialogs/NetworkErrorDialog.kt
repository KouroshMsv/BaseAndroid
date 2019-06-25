package dev.kourosh.baseapp.dialogs

import android.os.Bundle
import android.view.View
import dev.kourosh.baseapp.R
import dev.kourosh.baseapp.gone
import dev.kourosh.baseapp.infrastructure.dialog.BaseDialog
import kotlinx.android.synthetic.main.dialog_network_error.*


open class NetworkErrorDialog : BaseDialog() {
    var showCancel = true
    var onRetryClickListener: View.OnClickListener? = null
    var onCancelClickListener: View.OnClickListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)

    }

    override fun getLayout() = R.layout.dialog_network_error

    override fun initView(v: View) {
        if (!showCancel)
            btnCancel.gone()
        btnTryAgain.setOnClickListener(onRetryClickListener)
        btnCancel.setOnClickListener(onCancelClickListener)
    }
}