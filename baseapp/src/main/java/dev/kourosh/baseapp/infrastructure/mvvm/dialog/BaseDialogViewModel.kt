package dev.kourosh.baseapp.infrastructure.mvvm.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.kourosh.baseapp.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseDialogViewModel : ViewModel() {
    val errorMessage = SingleLiveEvent<String>()
    fun showMessage(message: String) {
        errorMessage.value = message
    }
    fun launchIO(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block(this)
        }
    }
}