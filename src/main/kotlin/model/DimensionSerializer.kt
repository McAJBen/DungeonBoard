package model

import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.awt.Dimension

@Serializer(forClass = Dimension::class)
class DimensionSerializer: KSerializer<Dimension> {
    override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Dimension") {
        init {
            addElement("width")
            addElement("height")
        }
    }

    override fun deserialize(decoder: Decoder): Dimension {
        val dec: CompositeDecoder = decoder.beginStructure(descriptor)
        var width = 0
        var height = 0
        loop@ while (true) {
            when (val i = dec.decodeElementIndex(descriptor)) {
                CompositeDecoder.READ_DONE -> break@loop
                0 -> width = dec.decodeIntElement(descriptor, i)
                1 -> height = dec.decodeIntElement(descriptor, i)
                else -> throw SerializationException("Unknown index $i")
            }
        }
        dec.endStructure(descriptor)
        return Dimension(width, height)
    }

    override fun serialize(encoder: Encoder, obj: Dimension) {
        val compositeOutput = encoder.beginStructure(descriptor)
        compositeOutput.encodeIntElement(descriptor, 0, obj.width)
        compositeOutput.encodeIntElement(descriptor, 1, obj.height)
        compositeOutput.endStructure(descriptor)
    }
}