package ru.lavafrai.study.android2.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.lavafrai.study.android2.models.CounterItem
import kotlin.uuid.Uuid

class MainViewModel: ViewModel() {
    private val _items = MutableStateFlow<List<CounterItem>>(emptyList())
    val items = _items.asStateFlow()

    private val _editingState = MutableStateFlow<Uuid?>(null)
    val editingState = _editingState.asStateFlow()

    fun addItem(item: CounterItem) {
        _items.value = _items.value + item
        // startEditing(item.id)
    }

    fun incrementCounter(itemId: Uuid) {
        _items.value = _items.value.map { item ->
            if (item.id == itemId) item.copy(counter = item.counter + 1)
            else item
        }
    }

    fun decrementCounter(itemId: Uuid) {
        _items.value = _items.value.map { item ->
            if (item.id == itemId) item.copy(counter = item.counter - 1)
            else item
        }
    }

    private fun setEditingItem(itemId: Uuid?) {
        _editingState.value = itemId
    }

    fun startEditing(itemId: Uuid) {
        _editingState.value = itemId
    }

    fun stopEditing() {
        _editingState.value = null
    }

    fun removeItem(id: Uuid) {
        _items.value = _items.value.filter { it.id != id }
    }

    fun updateItem(id: Uuid, updatedItem: CounterItem) {
        _items.value = _items.value.map { item ->
            if (item.id == id) updatedItem
            else item
        }
    }
}