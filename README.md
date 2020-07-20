![moko-errors](img/logo.png)

[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Download](https://api.bintray.com/packages/icerockdev/moko/moko-errors/images/download.svg) ](https://bintray.com/icerockdev/moko/moko-errors/_latestVersion) ![kotlin-version](https://img.shields.io/badge/kotlin-1.3.72-orange)

# Mobile Kotlin errors
This is a Kotlin MultiPlatform library that provides automatic exception handling and
automatic error displaying to a screen.

## Table of Contents
- [Features](#features)
- [Requirements](#requirements)
- [Versions](#versions)
- [Installation](#installation)
- [Usage](#usage)
- [Samples](#samples)
- [Set Up Locally](#setup-locally)
- [Contributing](#contributing)
- [License](#license)

## Features
- **ExceptionHandler** implements safe code execution and automatic exception display using **ErrorPresenter**.
- **ExceptionMappersStorage** singleton object, storage that stores a set of exception converters
to error classes required for **ErrorPresenter** objects.
- **ErrorPresenter** classes implements a strategy for displaying exceptions in a user-friendly form
on the platforms. Converts the exception class to an error object to display. There are several
`ErrorPresenter` implementations: `AlertErrorPresenter` - displays errors text in alert dialogs,
`ToastErrorPresenter` - displays errors text in toasts for Android and in alert dialog for iOS,
`SnackBarErrorPresenter` - displays errors text in snackbar for Android and in alert dialog for iOS,
`SelectorErrorPresenter` - for selecting error presenter by some custom condition.

## Requirements
- Gradle version 5.6.4+
- Android API 16+
- iOS version 9.0+

## Versions
- kotlin 1.3.72
  - 0.1.0
  - 0.2.0
  - 0.2.1

## Installation
root build.gradle  
```groovy
allprojects {
    repositories {
        maven { url = "https://dl.bintray.com/icerockdev/moko" }
    }
}
```

project build.gradle
```groovy
dependencies {
    commonMainApi("dev.icerock.moko:errors:0.2.1")
}
```

## Usage

#### ExceptionMappersStorage

Registration of simple custom exceptions mapper in the singleton storage:

```kotlin
ExceptionMappersStorage
    .register<IllegalArgumentException, StringDesc> {   // Will map all IllegalArgumentException instances to StringDesc
        "Illegal argument was passed!".desc()
    }
    .register<HttpException, Int> {                     // Will map all HttpException instances to Int
        it.code
    }
```

Registration of custom exception mapper with condition:

```kotlin
ExceptionMappersStorage.condition<StringDesc>(              // Registers exception mapper Throwable -> StringDesc
    condition = { it is CustomException && it.code == 10 }, // Condition that maps Throwable -> Boolean
    mapper = { "Custom error happened!".desc() }            // Mapper for Throwable that matches to the condition
)
```

For every error type you should to set fallback (default) value using method `setFallbackValue`  
(except `StringDesc` class which already has default value).

```kotlin
ExceptionMappersStorage
    .setFallbackValue<Int>(520) // Sets for Int error type default value as 520

// Creates new mapper that for any unregistered exception will return the fallback value - 520
val throwableToIntMapper: (Throwable) -> Int = ExceptionMappersStorage.throwableMapper()
```

Using factory method `throwableMapper` you can create exception mappers automaticlly:

```kotlin
val throwableToIntMapper: (Throwable) -> Int = ExceptionMappersStorage.throwableMapper()
```

If a default value is not found when creating a mapper using factory method `throwableMapper`, an 
exception will be thrown `FallbackValueNotFoundException`   

The registration can be done in the form of an endless chain:

```kotlin
ExceptionMappersStorage
    .condition<StringDesc>(
        condition = { it is CustomException && it.code == 10 },
        mapper = { "Custom error happened!".desc() }
    )
    .register<IllegalArgumentException, StringDesc> {
        "Illegal argument was passed!".desc()
    }
    .register<HttpException, Int> {
        it.code
    }
    .setFallbackValue<Int>(520)
```

#### ExceptionHandler

E.g. declare `ExceptionHandler` property in some `ViewModel` class:

```kotlin
class SimpleViewModel(
    val exceptionHandler: ExceptionHandler
) : ViewModel() {
    // ...
}
```

Bind `ExceptionHandler` in the platform code.

On Android in an `Activity` of `Fragment`: 

```kotlin
viewModel.exceptionHandler.bind(
    lifecycleOwner = this,
    activity = this
)
```

On iOS in a `ViewController`:

```swift
viewModel.exceptionHandler.bind(viewController: self)
```

Creating instances of `ExceptionHandler` class which uses `(Throwable) -> StringDesc` mappers:

```kotlin
ExceptionHandler<StringDesc>(
    errorPresenter = errorsPresenterInstance,                    // Concrete ErrorPresenter implementation
    exceptionMapper = ExceptionMappersStorage.throwableMapper(), // Create mapper (Throwable) -> StringDesc from ExceptionMappersStorage
    onCatch = {                                                  // Optional global catcher
        println("Got exception: $it")                            // E.g. here we can log all exceptions that are handled by ExceptionHandler
    }
)
```

And use it to safe requests in `ViewModel`:

```kotlin
fun onSendRequest() {
    viewModelScope.launch {
        exceptionHandler.handle {
            serverRequest()     // Some dangerous code that can throw an exception
        }.finally {             // Optional finally block
            // Some code        
        }.execute()             // Starts code execution in `handle` lambda
    }
}
```

Also you can add some custom `catch` handlers for `ExceptionHandler`:

```kotlin
fun onSendRequest() {
    viewModelScope.launch {
        exceptionHandler.handle {
            serverRequest()
        }.catch<IllegalArgumentException> {     // Specifying a specific exception class
            // Some custom handler code
            false                               // true - cancels ErrorPresenter; false - allows execution of ErrorsPresenter
        }.execute()                             // Starts code execution in `handle` lambda
    }
}
```

#### ErrorPresenter

There are `ErrorPresenter` interface implementations:
* `AlertErrorPresenter` - displays errors text in alert dialogs;
* `ToastErrorPresenter` - displays errors text in toasts for Android (for iOS shows alert dialog);
* `SnackBarErrorPresenter` - displays errors text in snackbar for Android (for iOS shows alert dialog);
* `SelectorErrorPresenter` - for selecting error presenter by some custom condition.

You need to pass some `ErrorPresenter` to `ErrorHandler` instance. E.g. creation of error presenters
in common code:

```kotlin
val alertErrorPresenter = AlertErrorPresenter(
    alertTitle = "Error".desc(),
    positiveButtonText = "OK".desc()
)
val toastErrorPresenter = ToastErrorPresenter(
    duration = ToastDuration.LONG
)
```

`SelectorErrorPresenter` - special presenter that select some error presenter by custom condition lambda 
which should return some `ErrorPresenter` to be used for showing errors:

```kotlin
val selectorErrorPresenter = SelectorErrorPresenter { throwable ->
    when (throwable) {
        is CustomException -> alertErrorPresenter
        else -> toastErrorPresenter
    }
}
```

And pass some `ErrorPresenter` to `ErrorHandler`:

```kotlin
val exceptionHandler = ExceptionHandler(
    errorPresenter = selectorErrorPresenter,
    exceptionMapper = ExceptionMappersStorage.throwableMapper()
)
```

## Samples
Please see more examples in the [sample directory](sample).

## Set Up Locally 
- The [errors directory](errors) contains the `errors` library;
- The [sample directory](sample) contains sample apps for Android and iOS; plus the mpp-library connected to the apps;
- For local testing a library use the `./publishToMavenLocal.sh` script - so that sample apps use the locally published version.

## Contributing
All development (both new features and bug fixes) is performed in the `develop` branch. This way `master` always contains the sources of the most recently released version. Please send PRs with bug fixes to the `develop` branch. Documentation fixes in the markdown files are an exception to this rule. They are updated directly in `master`.

The `develop` branch is pushed to `master` on release.

For more details on contributing please see the [contributing guide](CONTRIBUTING.md).

## License
        
    Copyright 2020 IceRock MAG Inc.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
