package com.sesac.common.utils

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sesac.common.ui_state.ResponseUiState

/**
 * A composable function that handles different UI states from a [ResponseUiState] within a [BoxScope].
 * It simplifies displaying loading, error, success and idle states.
 *
 * Example usage:
 * ```
 * Box(modifier = Modifier.fillMaxSize()) {
 *     HandleUiState(
 *         uiState = myViewModel.uiState.collectAsStateWithLifecycle().value,
 *         success = { data ->
 *             MySuccessContent(data)
 *         }
 *     )
 * }
 * ```
 *
 * @param T The type of data in the success state.
 * @param uiState The [ResponseUiState] to observe.
 * @param loading A composable to display when the state is [ResponseUiState.Loading].
 *                Defaults to a centered [CircularProgressIndicator].
 * @param error A composable to display when the state is [ResponseUiState.Error].
 *              Defaults to a centered [Text] showing the error message.
 * @param idle A composable to display when the state is [ResponseUiState.Idle]. Defaults to an empty view.
 * @param success A composable to display when the state is [ResponseUiState.Success].
 *                It receives the data of type [T].
 */
@Composable
fun <T> BoxScope.HandleUiState(
    uiState: ResponseUiState<T>,
    loading: @Composable BoxScope.() -> Unit = {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    },
    error: @Composable BoxScope.(String) -> Unit = { message ->
        Text(text = message, modifier = Modifier.align(Alignment.Center))
    },
    idle: @Composable BoxScope.() -> Unit = { },
    success: @Composable BoxScope.(data: T) -> Unit
) {
    when (uiState) {
        is ResponseUiState.Loading -> loading()
        is ResponseUiState.Error -> error(uiState.message)
        is ResponseUiState.Success -> success(uiState.result)
        is ResponseUiState.Idle -> idle()
    }
}
