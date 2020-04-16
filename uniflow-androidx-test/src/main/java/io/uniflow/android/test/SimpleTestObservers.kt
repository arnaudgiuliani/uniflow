package io.uniflow.android.test

import androidx.lifecycle.Observer
import io.uniflow.androidx.flow.AndroidDataFlow
import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

class TestObserver<T> : Observer<T> {
    val elements = arrayListOf<T>()

    override fun onChanged(t: T) {
        elements.add(t)
    }
}

data class TestViewObserver(private val states: TestObserver<UIState>, private val events: TestObserver<Event<UIEvent>>) {
    fun hasState(state: UIState): Boolean = states.elements.lastOrNull() == state
    fun hasState(state: UIState, index: Int): Boolean = states.elements[index] == state
    fun lastState(): UIState? = states.elements[states.elements.lastIndex]
    fun statesCount() = states.elements.count()
    fun hasEvent(event: UIEvent) = events.elements.lastOrNull()?.take() == event
    fun hasEvent(event: UIEvent, index: Int) = events.elements[index].take() == event
    fun eventsCount() = events.elements.count()
    fun lastEvent(): UIEvent? = events.elements[events.elements.lastIndex].take()
}

fun AndroidDataFlow.createTestObserver(): TestViewObserver {
    val viewStates: TestObserver<UIState> = TestObserver()
    val viewEvents: TestObserver<Event<UIEvent>> = TestObserver()
    states.observeForever(viewStates)
    events.observeForever(viewEvents)
    return TestViewObserver(viewStates, viewEvents)
}