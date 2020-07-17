package dev.icerock.moko.errors.presenters

import androidx.fragment.app.FragmentActivity

actual abstract class PlatformErrorPresenter<T : Any> : ErrorPresenter<T>() {

    override fun showError(throwable: Throwable, data: T) {
        show(throwable, super.activity!!, data)
    }

    abstract fun show(throwable: Throwable, activity: FragmentActivity, data: T)
}
