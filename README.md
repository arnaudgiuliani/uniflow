
# UniFlow - A Simple Unidirectional Data Flow framework for Android, using Kotlin coroutines

## Current Version

Uniflow current version is `0.2.0`

## Setup

Choose one of the following dependency:

```gradle
// Android
implementation 'io.uniflow:uniflow-android:$version'
testImplementation 'io.uniflow:uniflow-android-test:$version'

// AndroidX
implementation 'io.uniflow:uniflow-androidx:$version'
testImplementation 'io.uniflow:uniflow-androidx-test:$version'
```

## What is UniFlow?

A Simple Unidirectional Data Flow framework for Android, using Kotlin coroutines

```kotlin
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Setup loggers & Coroutine dispatchers for UniFlow
        setupUniFlow()
    }
}
```

Describe your data flow states & ViewModel:

```kotlin
data class WeatherState(val weather : Weather) : UIState()

class WeatherViewModelFlow : AndroidDataFlow() {

    init {
        // init state as Loading
        setState { UIState.Loading }
    }

    fun getMyWeather(val day : String) = setState {
        // Background call
        val weather = getWeatherForDay(day).await()
        // return state to UI
        WeatherState(weather)
    }

    // Unhandled errors here
    override fun onError(error: Throwable) = setState { UIState.Failed(error = error) }
}
```

Just observe your state flow from your Activity or Fragment:

```kotlin
class WeatherActivity : AppCompatActivity {

    // created WeatherViewModelFlow ViewModel instance here
    val myWeatherFlow : WeatherViewModelFlow ...

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Observe incoming states flow
        observeStates(myWeatherFlow) { state ->
            when (state) {
                is UIState.Loading -> showLoading()
                is WeatherState -> showWeather(state)
                is UIState.Failed -> showError(state.error)
            }
        }
        myWeatherFlow.getMyWeather("monday")
    }
}

```

