package kiwi.liam.crystal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackModel(
    val name: String,
    val email: String,
    val text: String,
    @SerialName("screenshot_links") val screenshotLinks: List<String>,
)
