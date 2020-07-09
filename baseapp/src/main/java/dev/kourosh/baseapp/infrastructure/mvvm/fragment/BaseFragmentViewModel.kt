package dev.kourosh.baseapp.infrastructure.mvvm.fragment

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dev.kourosh.baseapp.SingleLiveEvent
import dev.kourosh.baseapp.infrastructure.mvvm.BaseView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseFragmentViewModel : ViewModel(), LifecycleObserver, BaseView {
    val errorMessage = SingleLiveEvent<String>()
    val successMessage = SingleLiveEvent<String>()
    val infoMessage = SingleLiveEvent<String>()
    val warningMessage = SingleLiveEvent<String>()
    val hideKeyboard=SingleLiveEvent<Boolean>()
    val networkError = SingleLiveEvent<Boolean?>()
   open fun showNetworkError(showCancel: Boolean = true) {
        networkError.value = showCancel
    }

    override fun showInfo(message: String) {
        infoMessage.value = message
    }

    override fun showWarning(message: String) {
        warningMessage.value = message
    }

    override fun showError(message: String) {
        errorMessage.value = message
    }

    override fun showSuccess(message: String) {
        successMessage.value = message
    }

    override fun hideKeyboard() {
        hideKeyboard.value=true
    }
    fun launchIO(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block(this)
        }
    }

}
