package com.letthemcook.core.domain.dataConvertion.serialization

import android.net.Uri
import androidx.compose.ui.geometry.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

object UriSerializer : KSerializer<Uri> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Uri {
        val string = decoder.decodeString()
        return Uri.parse(string)
    }

    override fun serialize(encoder: Encoder, value: Uri) {
        val string = value.toString()
        encoder.encodeString(string)
    }
}