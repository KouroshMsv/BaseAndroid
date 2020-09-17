package dev.kourosh.baseapp.infrastructure.mvvm.fragment

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.kourosh.baseapp.SingleLiveEvent
import dev.kourosh.baseapp.enums.MessageType
import dev.kourosh.baseapp.infrastructure.mvvm.BaseView
import dev.kourosh.baseapp.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseFragmentViewModel : ViewModel(), LifecycleObserver, BaseView {
    internal val messageEvent = SingleLiveEvent<Message>()
    internal val hideKeyboard = SingleLiveEvent<Boolean>()
    internal val networkError = SingleLiveEvent<Boolean?>()
    open fun showNetworkError(showCancel: Boolean = true) {
        networkError.value = showCancel
    }

    override fun showInfo(message: String) {
        messageEvent.value = Message(MessageType.INFO, message)
    }

    override fun showWarning(message: String) {
        messageEvent.value = Message(MessageType.WARNING, message)
    }

    override fun showError(message: String) {
        messageEvent.value = Message(MessageType.ERROR, message)
    }

    override fun showSuccess(message: String) {
        messageEvent.value = Message(MessageType.SUCCESS, message)
    }

    override fun hideKeyboard() {
        hideKeyboard.value = true
    }

    fun launchIO(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block(this)
        }
    }

}
