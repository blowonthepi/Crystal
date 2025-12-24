package kiwi.liam.crystal.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class ResponseMessage(
    val message: String,
    val status: ResponseStatus,
)

@Serializable(with = StatusSerializer::class)
enum class ResponseStatus(val status: Int) {
    Success(1000),

    MissingBody(2000),
    TenantNotFound(2100),
    BadJSON(2200),
    BadDataFormat(2300),
    FailedToSave(2400),
    Unknown(-1);

    companion object {
        fun fromStatusCode(statusCode: Int): ResponseStatus? {
            return ResponseStatus.entries.firstOrNull { it.status == statusCode }
        }
    }
}

internal object StatusSerializer : KSerializer<ResponseStatus> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Status", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: ResponseStatus) {
        // Encode the enum as its ordinal (0, 1, 2...)
        encoder.encodeInt(value.status)
    }

    override fun deserialize(decoder: Decoder): ResponseStatus {
        val statusCode = decoder.decodeInt()
        return ResponseStatus.fromStatusCode(statusCode) ?: ResponseStatus.Unknown
    }
}