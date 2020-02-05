package model

import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.awt.Point

@Serializer(forClass = Point::class)
class PointSerializer: KSerializer<Point> {
    override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Point") {
        init {
            addElement("x")
            addElement("y")
        }
    }

    override fun deserialize(decoder: Decoder): Point {
        val dec: CompositeDecoder = decoder.beginStructure(descriptor)
        var x = 0
        var y = 0
        loop@ while (true) {
            when (val i = dec.decodeElementIndex(descriptor)) {
                CompositeDecoder.READ_DONE -> break@loop
                0 -> x = dec.decodeIntElement(descriptor, i)
                1 -> y = dec.decodeIntElement(descriptor, i)
                else -> throw SerializationException("Unknown index $i")
            }
        }
        dec.endStructure(descriptor)
        return Point(x, y)
    }

    override fun serialize(encoder: Encoder, obj: Point) {
        val compositeOutput = encoder.beginStructure(descriptor)
        compositeOutput.encodeIntElement(descriptor, 0, obj.x)
        compositeOutput.encodeIntElement(descriptor, 1, obj.y)
        compositeOutput.endStructure(descriptor)
    }
}