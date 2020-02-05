package model

import kotlinx.serialization.*
import util.Colors
import java.awt.Color
import java.awt.Dimension
import java.awt.Point

@Serializable
data class GridData(
    @Serializable(with = DimensionSerializer::class) var squareSize: Dimension = Dimension(100, 100),
    var lineWidth: Int = 4,
    @Serializable(with = PointSerializer::class) var offset: Point = Point(0, 0),
    @Serializable(with = RGBAColorSerializer::class) var color: Color = Colors.TRANSPARENT_GREY
) {
    fun changeRed(red: Int) {
        color = Color(red, color.green, color.blue, color.alpha)
    }

    fun changeGreen(green: Int) {
        color = Color(color.red, green, color.blue, color.alpha)
    }

    fun changeBlue(blue: Int) {
        color = Color(color.red, color.green, blue, color.alpha)
    }

    fun changeAlpha(alpha: Int) {
        color = Color(color.red, color.green, color.blue, alpha)
    }
}