/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dev.icerock.moko.resources.desc.StringDesc

actual class AlertErrorPresenter actual constructor(
    override val exceptionMapper: (Throwable) -> StringDesc,
    private val alertTitle: StringDesc,
    private val positiveButtonText: StringDesc
) : ErrorPresenter<StringDesc>() {

    override fun show(exception: Throwable, activity: FragmentActivity, data: StringDesc) {
        AlertDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(
                    AlertDialogFragment.ARGS_KEY,
                    AlertDialogFragment.DialogSettings(
                        title = alertTitle.toString(activity),
                        positiveButtonText = positiveButtonText.toString(activity),
                        messageText = data.toString(activity)
                    )
                )
            }
        }.show(activity.supportFragmentManager, AlertDialogFragment.ALERT_DIALOG_FRAGMENT_TAG)
    }
}
