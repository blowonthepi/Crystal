package kiwi.liam.crystal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

@Composable
fun CrystalFeedbackForm(
    topImage: ImageVector,
    apiKey: String,
    host: String = "https://ingestion-gw-2bhswcp2.uc.gateway.dev/feedback",
    onDismiss: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    FeedbackForm(
        topImage = topImage,
        modifier = modifier,
        viewModel = viewModel(
            factory = viewModelFactory {
                initializer {
                    FeedbackFormViewModel(
                        apiKey = apiKey,
                        host = host,
                        onDismiss = onDismiss,
                    )
                }
            }
        )
    )
}

@Composable
internal fun FeedbackForm(
    topImage: ImageVector,
    modifier: Modifier = Modifier,
    viewModel: FeedbackFormViewModel,
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(12.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Icon(
                imageVector = topImage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(15.dp),
                    )
                    .padding(16.dp)
                    .size(56.dp),
            )
            Text(
                text = "Something to say?",
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = "Your ideas, bug finds, and general feedback is valuable to creating a better app!",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 12.dp),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                viewState.name.BuildView(
                    label = "First name",
                )
                viewState.email.BuildView(
                    label = "Email",
                )
            }

            viewState.text.BuildView(
                label = "Feedback...",
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                enabled = !viewState.isLoading,
                onClick = {
                    viewState.onSubmit()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (viewState.isLoading) {
                        "Loading..."
                    } else {
                        "Submit feedback"
                    }
                )
            }
        }
    }
}

@Composable
private fun FeedbackFormViewModel.ViewState.TextField.BuildView(
    label: String,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(label) },
        singleLine = singleLine,
        modifier = if (!singleLine) {
            Modifier
                .defaultMinSize(minHeight = 150.dp)
                .then(modifier)
        } else modifier,
    )
}
