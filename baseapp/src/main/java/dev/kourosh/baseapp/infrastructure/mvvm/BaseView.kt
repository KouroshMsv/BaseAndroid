package dev.kourosh.baseapp.infrastructure.mvvm

interface BaseView {
    fun showInfo(message: String)
    fun showWarning(message: String)
    fun showError(message: String)
    fun showSuccess(message: String)
    fun hideKeyboard()
}