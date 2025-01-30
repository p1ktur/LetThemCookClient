package com.letthemcook.core.domain.dataConvertion.serialization

import androidx.compose.ui.geometry.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

object OffsetSerializer : KSerializer<Offset> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Offset", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Offset {
        val string = decoder.decodeString()
        val values = string.substring(1, string.length - 1).split("; ")
        return Offset(
            x = values[0].toFloat(),
            y = values[1].toFloat()
        )
    }

    override fun serialize(encoder: Encoder, value: Offset) {
        val string = "(${value.x}; ${value.y})"
        return encoder.encodeString(string)
    }
}