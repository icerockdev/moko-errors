/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import dev.icerock.moko.errors.ErrorPresenter
import dev.icerock.moko.resources.desc.StringDesc

actual class SnackBarErrorPresenter actual constructor(
    private val exceptionMapper: (Throwable) -> StringDesc,
    private val duration: SnackBarDuration
) : ErrorPresenter {

    override fun show(exception: Throwable, activity: FragmentActivity) {
        val rootView = activity.findViewById<View>(android.R.id.content)?.rootView
            ?: activity.window?.decorView?.findViewById<View>(android.R.id.content)
        if(rootView != null) {
            Snackbar.make(rootView, exceptionMapper(exception).toString(activity), duration.code)
                .show()
        }
    }
}
