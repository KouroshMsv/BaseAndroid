package dev.kourosh.baseapp.infrastructure.mvvm.activity

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import dev.kourosh.baseapp.SingleLiveEvent
import dev.kourosh.baseapp.infrastructure.mvvm.BaseView

abstract class BaseActivityViewModel : ViewModel(),LifecycleObserver,  BaseView {
    val errorMessage = SingleLiveEvent<String>()
    val successMessage = SingleLiveEvent<String>()
    val infoMessage = SingleLiveEvent<String>()
    val warningMessage = SingleLiveEvent<String>()
    val networkError = SingleLiveEvent<Boolean>()
    val hideKeyboard = SingleLiveEvent<Boolean>()
    fun showNetworkError() {
        networkError.value = true
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
        hideKeyboard.value = true
    }



}