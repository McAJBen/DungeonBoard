package paint

import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.json.Json
import util.Log
import java.awt.Point
import java.io.File

/**
 * storage class for `PaintReference`
 * @author McAJBen@gmail.com
 * @param displayZoom the displayZoom used in `PaintReference`
 * @param windowCenter the windowCenter used in `PaintReference`
 * @since 2.5.1
 */
@Serializable
data class PaintData(
    val displayZoom: Double,
    @Serializable(with = PointSerializer::class) val windowCenter: Point
) {
    companion object {

        /**
         * reads a data file and converts it to `PaintData`
         * @param file the data file to read
         * @return the conversion of the file into `PaintData`
         */
        fun read(file: File): PaintData {
            @UseExperimental(ImplicitReflectionSerializer::class)
            return Json.parse(file.readText())
        }

        /**
         * writes a `PaintData` to a data file
         * @param file the data file to write
         * @param paintData the data to write
         */
        fun write(file: File, paintData: PaintData) {
            try {
                @UseExperimental(ImplicitReflectionSerializer::class)
                file.writeText(Json.stringify(paintData))
            } catch (e: Exception) {
                Log.error(e.localizedMessage)
            }
        }
    }

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
}

