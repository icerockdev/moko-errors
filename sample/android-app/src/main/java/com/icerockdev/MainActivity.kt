/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.icerockdev.databinding.ActivityMainBinding
import com.icerockdev.library.SimpleViewModel
import com.icerockdev.library.createSimpleViewModel
import com.icerockdev.library.initExceptionStorage
import dev.icerock.moko.mvvm.MvvmActivity
import dev.icerock.moko.mvvm.createViewModelFactory

class MainActivity : MvvmActivity<ActivityMainBinding, SimpleViewModel>() {

    override val layoutId: Int = R.layout.activity_main
    override val viewModelClass: Class<SimpleViewModel> = SimpleViewModel::class.java
    override val viewModelVariableId: Int = BR.viewModel

    override fun viewModelFactory(): ViewModelProvider.Factory {
        return createViewModelFactory {
            createSimpleViewModel()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initExceptionStorage()

        viewModel.exceptionHandler.bind(
            lifecycleOwner = this,
            activity = this
        )

        binding.alertButton.setOnClickListener {
            viewModel.onAlertButtonClick()
        }
    }
}
