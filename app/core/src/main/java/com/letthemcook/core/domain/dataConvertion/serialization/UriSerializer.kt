package com.letthemcook.core.domain.dataConvertion.serialization

import android.net.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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