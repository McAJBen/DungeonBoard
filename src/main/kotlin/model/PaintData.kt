package model

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import kotlinx.serialization.stringify
import util.Log
import java.awt.Point
import java.io.File

/**
 * storage class for `PaintReference`
 * @author McAJBen@gmail.com
 * @param displayZoom the displayZoom used in `PaintReference`.
 * The zoom level of the displayed image
 * @param windowCenter the windowCenter used in `PaintReference`.
 * Used when `displayZoom` is changed and `windowOffset` must be recalculated
 * @param grid the grid settings or null if there is no grid, used in `PaintReference`
 * @since 2.5.1
 */
@Serializable
data class PaintData(
    var displayZoom: Double = 1.0,
    @Serializable(with = PointSerializer::class) var windowCenter: Point = Point(0, 0),
    var grid: GridData? = null
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
}

