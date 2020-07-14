[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) ![kotlin-version](https://img.shields.io/badge/kotlin-1.3.72-orange)

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
- **ExceptionMappersRegistry** singleton object, registry that stores a set of exception converters
to error classes required for **ErrorPresenter** objects.
- **ErrorPresenter** classes implements a strategy for displaying exceptions in a user-friendly form
on the platforms. Converts the exception class to an error object to display. There are several
`ErrorPresenter` implementations: `AlertErrorPresenter` - displays errors text in alert dialogs,
`ToastErrorPresenter` - displays errors text in toasts for Android and in alert dialog for iOS,
`SnackBarErrorPresenter` - displays errors text in snackbar for Android and in alert dialog for iOS.

## Requirements
- Gradle version 5.6.4+
- Android API 16+
- iOS version 9.0+

## Versions
- kotlin 1.3.70
  - 0.1.0

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
    commonMainApi("dev.icerock.moko:errors:0.1.0")
}
```

## Usage

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

Creating instances of `ExceptionHandler` class:

```kotlin
ExceptionHandler(
    errorEventsDispatcher = eventsDispatcherInstance, // moko-mvvm EventsDispatcher instance
    errorPresenter = errorsPresenterInstance // concrete ErrorPresenter implementation
)
```

And use it to safe requests in `ViewModel`:

```kotlin
fun onSendRequest() {
    viewModelScope.launch {
        exceptionHandler.handle {
            serverRequest() // Some dangerous code that can throw an exception
        }.finally {
            // Optional finally block
        }.execute() // Executes handler block
    }
}
```

Also you can add some custom `catch` handlers for `ExceptionHandler`:

```kotlin
fun onSendRequest() {
    viewModelScope.launch {
        exceptionHandler.handle {
            serverRequest()
        }.catch<IllegalArgumentException> { // Specifying a specific exception class
            // Some custom handler code
            false // true - cancels ErrorPresenter; false - allows execution of ErrorsPresenter
        }.execute()
    }
}
```

#### ExceptionMappersRegistry

Registration of simple custom exceptions mapper:

```kotlin
ExceptionMappersRegistry
    .register<IllegalArgumentException, StringDesc> { // Maps IllegalArgumentException instances to StringDesc
        MR.strings.illegalArgumentText.desc()
    }
    .register<HttpException, Int> { // Maps HttpException instances to Int
        it.code
    }
```

Registration of custom exception mapper with condition:

```kotlin
ExceptionMappersRegistry.condition<StringDesc>( // Registers exception mapper Throwable -> StringDesc
    condition = { it is CustomException && it.code == 10 }, // Condition that maps Throwable -> Boolean
    mapper = { MR.strings.myExceptionText.desc() } // Mapper for Throwable that matches to the condition
)
```

The registration can be done in the form of an endless chain:

```kotlin
ExceptionMappersRegistry
    .condition<StringDesc>(
        condition = { it is CustomException && it.code == 10 },
        mapper = { MR.strings.myExceptionText.desc() }
    )
    .register<IllegalArgumentException, StringDesc> {
        MR.strings.illegalArgumentText.desc()
    }
    .register<HttpException, Int> {
        it.code
    }
```

After initializing the registry, you can pass exception mappers of `(Throwable) -> StringDesc` 
signature from the `ExceptionMappersRegistry` to an `ErrorPresenter`:

```kotlin
val alertErrorPresenter = AlertErrorPresenter(
    exceptionMapper = ExceptionMappersRegistry::throwableToStringDesc,
    alertTitle = "Error".desc()
)
```

Or you can create your own mapper using extensions:

```kotlin
fun <E : Throwable> ExceptionMappersRegistry.throwableToInt(e: E): Int {
    return find<Int, E>(e) // Tries to find mapper (Throwable) -> Int in the registry 
        ?.invoke(e) // If it was found - invokes it
        ?: 0 // Or default value
}
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
