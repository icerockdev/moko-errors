/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import dev.icerock.moko.resources.desc.StringDesc

actual class ToastErrorPresenter actual constructor(
    override val exceptionMapper: (Throwable) -> StringDesc,
    private val duration: ToastDuration
) : ErrorPresenter<StringDesc>() {

    override fun show(throwable: Throwable, activity: FragmentActivity, data: StringDesc) {
        Toast.makeText(
            activity,
            exceptionMapper(throwable).toString(activity),
            duration.toAndroidCode()
        ).show()
    }
}
