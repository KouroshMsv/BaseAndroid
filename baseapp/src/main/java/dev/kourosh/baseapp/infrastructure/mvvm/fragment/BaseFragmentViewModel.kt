package dev.kourosh.baseapp.infrastructure.mvvm.fragment

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import dev.kourosh.baseapp.SingleLiveEvent
import dev.kourosh.baseapp.infrastructure.mvvm.BaseView
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseFragmentViewModel : ViewModel(), KoinComponent, LifecycleObserver, BaseView {
    val errorMessage = SingleLiveEvent<String>()
    val successMessage = SingleLiveEvent<String>()
    val infoMessage = SingleLiveEvent<String>()
    val warningMessage = SingleLiveEvent<String>()
    val hideKeyboard=SingleLiveEvent<Boolean>()
    val networkError = SingleLiveEvent<Boolean?>()
    fun showNetworkError(showCancel: Boolean = true) {
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

}
