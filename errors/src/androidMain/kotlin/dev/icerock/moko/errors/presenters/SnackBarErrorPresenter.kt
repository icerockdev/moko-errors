/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import dev.icerock.moko.resources.desc.StringDesc

actual class SnackBarErrorPresenter actual constructor(
    override val exceptionMapper: (Throwable) -> StringDesc,
    private val duration: SnackBarDuration
) : ErrorPresenter<StringDesc>() {

    override fun show(throwable: Throwable, activity: FragmentActivity, data: StringDesc) {
        val rootView = activity.findViewById<View>(android.R.id.content)?.rootView
            ?: activity.window?.decorView?.findViewById<View>(android.R.id.content)
        if (rootView != null) {
            Snackbar.make(
                rootView,
                data.toString(activity),
                duration.toAndroidCode()
            ).show()
        }
    }
}
