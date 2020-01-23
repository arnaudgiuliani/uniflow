package io.uniflow.test.data

import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState
import io.uniflow.core.flow.fromState
import io.uniflow.core.flow.stateFlowFrom
import io.uniflow.core.threading.onIO
import kotlinx.coroutines.delay

class TodoStackActorFlow(private val repository: TodoRepository) : ListDataFlow() {

    init {
        setState { UIState.Empty }
    }

    fun getAll() = setState {
        repository.getAllTodo().mapToTodoListState()
    }

    fun filterDones() = setState { current ->
        when (current) {
            is TodoListState -> current.copy(current.todos.filter { it.done })
            else -> sendEvent(UIEvent.Fail("Can't filter as without todo list"))
        }
    }

    fun add(title: String) = fromState<TodoListState> {
        val added = repository.add(title)
        if (added) {
            repository.getAllTodo().mapToTodoListState()
        } else {
            sendEvent(UIEvent.Fail("Can't add '$title'"))
        }
    }

    fun done(title: String) = setState { currentState ->
        if (currentState is TodoListState) {
            val done = repository.isDone(title)
            if (done) {
                repository.getAllTodo().mapToTodoListState()
            } else {
                sendEvent(UIEvent.Fail("Can't make done '$title'"))
            }
        } else {
            sendEvent(UIEvent.BadOrWrongState(this@TodoStackActorFlow.currentState))
        }
    }

    fun childIO() = setState {
        onIO {
            delay(100)
            repository.add("LongTodo")
            repository.getAllTodo().mapToTodoListState()
        }
    }

    fun childIOError() = setState {
        onIO {
            error("Boom on IO")
        }
    }

    fun longWait() = setState {
        delay(1000)
        repository.add("LongTodo")
        repository.getAllTodo().mapToTodoListState()
    }

    fun makeOnError() = setState(
            {
                error("boom")
            },
            { error -> sendEvent(UIEvent.Fail("Event logError", error)) })

    fun makeOnStateError() = setState(
            {
                error("boom")
            },
            { error -> sendEvent(UIEvent.Fail("Event logError", error)) })


    fun makeGlobalError() = setState {
        error("global boom")
    }

    fun notifyUpdate() = fromState<TodoListState> { state ->
        val t = Todo("t2")
        val list = state.todos + t
        notifyUpdate(TodoListState(list), TodoListUpdate(t))
    }

    fun testFlow() = stateFlow {
        emit(UIState.Loading)
        delay(10)
        emit(UIState.Success)
    }

    fun notifyFlowFromState() = stateFlowFrom<TodoListState> {
        emit(UIState.Loading)
        delay(10)
        emit(UIState.Success)
    }

    fun testBoomFlow() = stateFlow({
        emit(UIState.Loading)
        error("boom")
    }, { e -> UIState.Failed("flow boom", e) })

    override suspend fun onError(error: Exception) {
        setState { UIState.Failed("Failed state", error) }
    }
}



