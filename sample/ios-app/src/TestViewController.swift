/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import UIKit
import MultiPlatformLibrary

class TestViewController: UIViewController {
    
    private var viewModel: SimpleViewModel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        ExceptionRegistryKt.doInitExceptionRegistry()
        
        viewModel = SimpleViewModelKt.createSimpleViewModel(errorEventsDispatcher: EventsDispatcher())
        viewModel.exceptionHandler.bind(viewController: self)
    }
    
    @IBAction func onRunAlertButtonPressed() {
        viewModel.onAlertButtonClick()
    }
}
