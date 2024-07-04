package pl.artur.tasknest.ui

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SnackbarManager(private val coroutineScope: CoroutineScope) {
    private val messages = Channel<String>(Channel.BUFFERED)
    val snackbarMessages = messages.receiveAsFlow()
    val snackbarHostState = SnackbarHostState()

    fun showMessage(message: String) {
        coroutineScope.launch {
            messages.send(message)
        }
    }
}

@Composable
fun rememberSnackbarManager(coroutineScope: CoroutineScope): SnackbarManager {
    return remember { SnackbarManager(coroutineScope) }
}

@Composable
fun SnackbarHost(snackbarManager: SnackbarManager) {
    val lifecycleOwner = LocalLifecycleOwner.current

    androidx.compose.runtime.LaunchedEffect(snackbarManager.snackbarMessages, lifecycleOwner) {
        snackbarManager.snackbarMessages.collect { message ->
            snackbarManager.snackbarHostState.showSnackbar(message)
        }
    }

    SnackbarHost(hostState = snackbarManager.snackbarHostState)
}
