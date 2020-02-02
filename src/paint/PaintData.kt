package paint

import java.awt.Point
import java.io.File

/**
 * storage class for `PaintReference`
 * @author McAJBen@gmail.com
 * @param displayZoom the displayZoom used in `PaintReference`
 * @param windowCenter the windowCenter used in `PaintReference`
 * @since 2.5.1
 */
data class PaintData(
    val displayZoom: Double,
    val windowCenter: Point
) {
    companion object {

        /**
         * reads a data file and converts it to `PaintData`
         * @param file the data file to read
         * @return the conversion of the file into `PaintData`
         */
        fun read(file: File): PaintData {
            val data = file.readText().split(" ".toRegex())
            return PaintData(
                data[0].toDouble(),
                Point(
                    data[1].toInt(),
                    data[2].toInt()
                )
            )
        }

        /**
         * writes a `PaintData` to a data file
         * @param file the data file to write
         * @param paintData the data to write
         */
        fun write(file: File, paintData: PaintData) {
            file.writeText("${paintData.displayZoom} ${paintData.windowCenter.x} ${paintData.windowCenter.y}")
        }
    }
}

