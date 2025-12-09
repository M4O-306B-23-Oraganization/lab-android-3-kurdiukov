package ru.lavafrai.study.android2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.lavafrai.study.android2.models.CounterItem
import ru.lavafrai.study.android2.ui.theme.Android2Theme
import ru.lavafrai.study.android2.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Android2Theme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val viewModel = viewModel<MainViewModel>()
    val items by viewModel.items.collectAsState()
    val editing by viewModel.editingState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val newItem = CounterItem(
                    name = "Item ${items.size + 1}",
                    counter = 0,
                )
                viewModel.addItem(newItem)
            }) {
                Icon(Icons.Default.Add, "add item")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items, key = { it.id }) { item ->
                CounterItemView(
                    item,
                    onIncrement = { viewModel.incrementCounter(item.id) },
                    onDecrement = { viewModel.decrementCounter(item.id) },
                    onRemoveRequest = { viewModel.removeItem(item.id) },
                    onClick = { viewModel.startEditing(item.id) },
                    modifier = Modifier.fillMaxWidth().animateItem()
                )
            }
        }
    }

    val editingItem = remember(items, editing) {
        items.find { it.id == editing }
    }
    if (editingItem != null) {
        EditItemDialog(
            editingItem,
            onDismissRequest = { viewModel.stopEditing() },
            onUpdate = { updatedItem ->
                viewModel.updateItem(updatedItem.id, updatedItem)
                viewModel.stopEditing()
            }
        )
    }
}

@Composable
fun EditItemDialog(
    item: CounterItem,
    onDismissRequest: () -> Unit,
    onUpdate: (CounterItem) -> Unit,
) {
    var newName by rememberSaveable(item) { mutableStateOf(item.name) }
    var newCounter by rememberSaveable(item) { mutableIntStateOf(item.counter) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(min = 280.dp, max = 560.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Edit Item",
                    style = LocalTextStyle.current.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
                Text(text = "Name:")
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                )

                Text(text = "Counter:")
                OutlinedTextField(
                    value = newCounter.toString(),
                    onValueChange = { newCounter = it.toIntOrNull() ?: newCounter },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Counter") },
                )

                Button(
                    onClick = {
                        val updatedItem = item.copy(
                            name = newName,
                            counter = newCounter,
                        )
                        onUpdate(updatedItem)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun CounterItemView(
    item: CounterItem,
    onClick: () -> Unit,
    onRemoveRequest: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilledIconButton(
                colors = IconButtonDefaults.filledIconButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.onError,
                    containerColor = MaterialTheme.colorScheme.error,
                ),
                onClick = onRemoveRequest
            ) {
                Icon(Icons.Default.Delete, "remove item")
            }

            Text(
                text = item.name,
                style = LocalTextStyle.current.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onIncrement) {
                    Icon(Icons.Default.Add, "increment")
                }
                Text(
                    text = "${item.counter}",
                    style = LocalTextStyle.current.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDecrement) {
                    Icon(painterResource(R.drawable.ic_remove), "decrement")
                }
            }
        }
    }
}