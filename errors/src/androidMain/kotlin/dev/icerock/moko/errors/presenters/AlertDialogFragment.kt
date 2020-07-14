/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.presenters

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dev.icerock.moko.errors.R
import kotlinx.android.parcel.Parcelize
import java.lang.IllegalStateException

class AlertDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val settings: DialogSettings = arguments?.getParcelable<DialogSettings>(ARGS_KEY)
            ?: DialogSettings(
                title = getString(R.string.moko_errors_presenters_alertDialogTitle),
                positiveButtonText = getString(R.string.moko_errors_presenters_alertPositiveButton),
                messageText = getString(R.string.moko_errors_unknownError)
            )

        return activity?.let {
            AlertDialog.Builder(it)
                .setTitle(settings.title)
                .setMessage(settings.messageText)
                .setPositiveButton(settings.positiveButtonText) { _, _ -> }
                .create()
        } ?: throw IllegalStateException("Activity can't be null.")
    }

    @Parcelize
    data class DialogSettings(
        val title: String,
        val positiveButtonText: String,
        val messageText: String
    ) : Parcelable

    companion object {
        const val ALERT_DIALOG_FRAGMENT_TAG = "alert_dialog_fragment"
        internal const val ARGS_KEY = "dialog_settings"
    }
}
