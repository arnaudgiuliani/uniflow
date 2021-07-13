
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Applying side effects with Events

The same way we define States, we define events from `UIEvent` class, as immutable Kotlin data:

```kotlin
// Events definition
sealed class WeatherEvent : UIEvent() {
    data class Success(val location: String) : WeatherEvent()
    data class Failed(val location: String, val error: Throwable? = null) : WeatherEvent()
}
```

When you don't want to update the current state, you can simply send an event with `sendEvent()` function:

```kotlin
fun getWeather() = action {
    
    // send event
    sendEvent(WeatherEvent.Success(location))
}
```

To observe events from your Activity/Fragment view class, use the  `onEvent` function with your ViewModel instance:

```kotlin
class MyActivity : AppCompatActivity(){

    fun onCreate(...) {
        
        // Let's observe incoming events
        onEvents(viewModel) { event ->
            when (event) {
                is WeatherListUIEvent.Success -> showSuccess(event.location)
                is WeatherListUIEvent.Failed -> showFailed(event.location, event.error)
            }
        }
    }
}
```

_Warning_: An event is "one shot". It won't persist in the current Dataflow. Once consumed, an event is cleared from the Dataflow 

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)



