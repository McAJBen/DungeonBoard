package model

import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.awt.Color

/**
 * Serializer for storing the red, green, blue and alpha of a `Color`
 * @author McAJBen@gmail.com
 * @since 3.0
 */
@Serializer(forClass = Color::class)
class RGBAColorSerializer: KSerializer<Color> {
    override val descriptor: SerialDescriptor = object : SerialClassDescImpl("RGBAColor") {
        init {
            addElement("red")
            addElement("green")
            addElement("blue")
            addElement("alpha")
        }
    }

    override fun deserialize(decoder: Decoder): Color {
        val dec: CompositeDecoder = decoder.beginStructure(descriptor)
        var red = 0
        var green = 0
        var blue = 0
        var alpha = 0
        loop@ while (true) {
            when (val i = dec.decodeElementIndex(descriptor)) {
                CompositeDecoder.READ_DONE -> break@loop
                0 -> red = dec.decodeIntElement(descriptor, i)
                1 -> green = dec.decodeIntElement(descriptor, i)
                2 -> blue = dec.decodeIntElement(descriptor, i)
                3 -> alpha = dec.decodeIntElement(descriptor, i)
                else -> throw SerializationException("Unknown index $i")
            }
        }
        dec.endStructure(descriptor)
        return Color(red, green, blue, alpha)
    }

    override fun serialize(encoder: Encoder, obj: Color) {
        val compositeOutput = encoder.beginStructure(descriptor)
        compositeOutput.encodeIntElement(descriptor, 0, obj.red)
        compositeOutput.encodeIntElement(descriptor, 1, obj.green)
        compositeOutput.encodeIntElement(descriptor, 2, obj.blue)
        compositeOutput.encodeIntElement(descriptor, 3, obj.alpha)
        compositeOutput.endStructure(descriptor)
    }
}