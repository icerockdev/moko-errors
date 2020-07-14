/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import dev.icerock.moko.errors.ErrorPresenter
import dev.icerock.moko.resources.desc.StringDesc

actual class ToastErrorPresenter actual constructor(
    private val exceptionMapper: (Throwable) -> StringDesc,
    private val duration: ToastDuration
) : ErrorPresenter {

    override fun show(exception: Throwable, activity: FragmentActivity) {
        Toast.makeText(
            activity,
            exceptionMapper(exception).toString(activity),
            duration.toAndroidCode()
        ).show()
    }
}
