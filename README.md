SgMobileData with Android Architecture Components
===========================================================

Introduction
-------------
###Scope
List of yearly record of Mobile data consumption in Singapore and highlighting the year in which
there has been a decrease in the consumption between the quarters. Written in Kotlin, this app supports caching
and works on Architectural Components from Android.

### Functionality
The app is composed of one screen.

#### ResultFragment
Allows you to browse through the yearly records of Mobile data consumption in Singapore.
Each result from API call is kept in the database in `Result` table.
The API results are cached in the Room database, hence the subsequent request calls to the
API are served from Room database.


**NOTE** The UI currently loads all `Results` items at once, which would not
perform well on lower end devices. But since the list, by virtue of the type
 of its content, will not grow exponentially hence one-time load should work
 for this problem.

### Building
You can open the project in Android studio and press run.
### Testing
The project uses both instrumentation tests that run on the device
and local unit tests that run on your computer.
To run both of them and generate a coverage report, you can run:

`./gradlew fullCoverageReport` (requires a connected device or an emulator)

#### Device Tests
##### UI Tests
The projects uses Espresso for UI testing. Since each fragment
is limited to a ViewModel, each test mocks related ViewModel to
run the tests.
##### Database Tests
The project creates an in memory database for each database test but still
runs them on the device.

#### Local Unit Tests
##### ViewModel Tests
Each ViewModel is tested using local unit tests with mock Repository
implementations.
##### Repository Tests
Each Repository is tested using local unit tests with mock web service and
mock database.
##### Webservice Tests
The project uses [MockWebServer][mockwebserver] project to test REST api interactions.


### Libraries
* [Android Support Library][support-lib]
* [Android Architecture Components][arch]
* [Android Data Binding][data-binding]
* [Dagger 2][dagger2] for dependency injection
* [Retrofit][retrofit] for REST api communication
* [Glide][glide] for image loading
* [Timber][timber] for logging
* [espresso][espresso] for UI tests
* [mockito][mockito] for mocking in tests


[mockwebserver]: https://github.com/square/okhttp/tree/master/mockwebserver
[support-lib]: https://developer.android.com/topic/libraries/support-library/index.html
[arch]: https://developer.android.com/arch
[data-binding]: https://developer.android.com/topic/libraries/data-binding/index.html
[espresso]: https://google.github.io/android-testing-support-library/docs/espresso/
[dagger2]: https://google.github.io/dagger
[retrofit]: http://square.github.io/retrofit
[glide]: https://github.com/bumptech/glide
[timber]: https://github.com/JakeWharton/timber
[mockito]: http://site.mockito.org

### Limitations
* Instrumentation test are not running.

