package io.uniflow.android.test

import android.arch.lifecycle.Observer
import io.uniflow.android.flow.AndroidDataFlow
import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

class SimpleObserver<T>(val callback: (T) -> Unit) : Observer<T> {
    val values = arrayListOf<T>()

    override fun onChanged(t: T?) {
        t?.let {
            values.add(t)
            callback(t)
        }
    }
}

class TestViewObserver {
    val values = arrayListOf<UIData>()
    val states = SimpleObserver<UIState> { values.add(it) }
    val events = SimpleObserver<Event<UIEvent>> { values.add(it.peek()) }

    val lastStateOrNull: UIState?
        get() = states.values.lastOrNull()
    val statesCount
        get() = states.values.count()
    val eventsCount
        get() = events.values.count()
    val lastEventOrNull
        get() = events.values.lastOrNull()
    val lastValueOrNull
        get() = values.lastOrNull()

    fun assertReceived(vararg any: UIData) = assert(this.values == any.toList()) { "Wrong values\nshould have [${any.toList()}]\nbut was [${values}]" }
    fun assertReceived(vararg states: UIState) = assert(this.states.values == states.toList()) { "Wrong values\nshould have [${states.toList()}]\nbut was [${this.states.values}]" }
    fun assertReceived(vararg events: UIEvent) = assert(this.events.values == events.toList()) { "Wrong values\nshould have [${events.toList()}]\nbut was [${this.events.values}]" }
}

fun AndroidDataFlow.createTestObserver(): TestViewObserver {
    val tester = TestViewObserver()
    dataPublisher.states.observeForever(tester.states)
    dataPublisher.events.observeForever(tester.events)
    return tester
}