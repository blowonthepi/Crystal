package kiwi.liam.crystal.ui

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kiwi.liam.crystal.data.CrystalFeedbackService
import kiwi.liam.crystal.data.FeedbackService
import kiwi.liam.crystal.data.model.FeedbackModel
import kiwi.liam.crystal.data.model.ResponseMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedbackFormViewModel(
    private val apiKey: String,
    private val host: String,
    private val onDismiss: (Boolean) -> Unit,
    private val feedbackService: FeedbackService = CrystalFeedbackService(
        apiKey = apiKey,
        host = host,
    ),
) : ViewModel() {
    private val model: MutableStateFlow<Model> = MutableStateFlow(
        Model(
            name = "",
            email = "",
            text = "",
            isLoading = false,
        )
    )
    val viewState: StateFlow<ViewState> = model
        .map { createViewState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = createViewState(),
        )

    private fun createViewState(): ViewState = model.value.let { model ->
        return ViewState(
            name = ViewState.TextField(
                value = model.name,
                onChange = { name ->
                    this.model.update {
                        it.copy(name = name)
                    }
                }
            ),
            email = ViewState.TextField(
                value = model.email,
                onChange = { email ->
                    this.model.update {
                        it.copy(email = email)
                    }
                }
            ),
            text = ViewState.TextField(
                value = model.text,
                onChange = { text ->
                    this.model.update {
                        it.copy(text = text)
                    }
                }
            ),
            isLoading = model.isLoading,
            onSubmit = {
                this.model.update { it.copy(isLoading = true) }
                val feedback = FeedbackModel(
                    name = model.name,
                    email = model.email,
                    text = model.text,
                    screenshotLinks = emptyList(),
                )
                viewModelScope.launch {
                    val result = feedbackService.sendFeedback(feedback)

                    if (result.isFailure) {
                        Log.e("Crystal", "Response message: ${result.getOrNull()?.message}", result.exceptionOrNull())
                    }

                    onDismiss(result.isSuccess)
                    this@FeedbackFormViewModel.model.update { it.copy(isLoading = false) }
                }
            },
        )
    }

    private data class Model(
        val name: String,
        val email: String,
        val text: String,
        val isLoading: Boolean,
    )

    data class ViewState(
        val name: TextField,
        val email: TextField,
        val text: TextField,
        val isLoading: Boolean,
        val onSubmit: () -> Unit,
    ) {
        data class TextField(
            val value: String,
            val onChange: (String) -> Unit,
        )
    }
}