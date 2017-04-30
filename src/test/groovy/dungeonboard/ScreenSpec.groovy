package dungeonboard

import spock.lang.Specification

import java.awt.Dimension
import java.awt.GraphicsConfiguration
import java.awt.GraphicsDevice
import java.awt.Rectangle

class ScreenSpec extends Specification {

    def subject, mockDimension, mockRectangle

    def setup() {
        mockDimension = Mock Dimension

        mockRectangle = Mock Rectangle
        mockRectangle.getSize() >> mockDimension
        mockRectangle.width = 65
        mockRectangle.height = 99

        def mockConfiguration = Mock GraphicsConfiguration
        mockConfiguration.getBounds() >> mockRectangle

        def mockGraphicsDevice = Mock GraphicsDevice
        mockGraphicsDevice.getIDstring() >> 'this id'
        mockGraphicsDevice.getDefaultConfiguration() >> mockConfiguration

        subject = new Screen(mockGraphicsDevice)
    }

    def 'returns the correct size'() {
        expect:
        mockDimension == subject.getSize()
    }

    def 'returns the rectangle'() {
        expect:
        mockRectangle == subject.getRectangle()
    }

    def 'returns the correct string'() {
        expect:
        'this id  65x99' == subject.toString()
    }
}