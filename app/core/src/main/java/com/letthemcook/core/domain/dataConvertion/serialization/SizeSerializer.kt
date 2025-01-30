package com.letthemcook.core.domain.dataConvertion.serialization

import androidx.compose.ui.geometry.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

object SizeSerializer : KSerializer<Size> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Size", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Size {
        val string = decoder.decodeString()
        val values = string.substring(1, string.length - 1).split("; ")
        return Size(
            width = values[0].toFloat(),
            height = values[1].toFloat()
        )
    }

    override fun serialize(encoder: Encoder, value: Size) {
        val string = "(${value.width}; ${value.height})"
        return encoder.encodeString(string)
    }
}