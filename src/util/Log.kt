package util

import java.lang.Error
import javax.swing.JOptionPane

/**
 * contains methods for logging
 * @author McAJBen@gmail.com
 * @since 2.5
 */
object Log {

    /**
     * shows a `JOptionPane` to display an error
     * @param message custom text to be displayed
     */
    fun error(message: String) {
        System.err.println(message)
        JOptionPane.showMessageDialog(
            null,
            message,
            Labels.ERROR,
            JOptionPane.ERROR_MESSAGE
        )
    }

    /**
     * shows a `JOptionPane` to display an error
     * @param message custom text to be displayed
     * @param error the error that was thrown
     */
    fun error(message: String, error: Error) {
        error.printStackTrace()
        JOptionPane.showMessageDialog(
            null,
            "$message\n${error.message}",
            Labels.ERROR,
            JOptionPane.ERROR_MESSAGE
        )
    }

    /**
     * shows a `JOptionPane` to display an error
     * @param message custom text to be displayed
     * @param error the error that was thrown
     */
    fun error(message: String, error: Exception?) {
        error?.printStackTrace()
        JOptionPane.showMessageDialog(
            null,
            "$message\n${error?.message}",
            Labels.ERROR,
            JOptionPane.ERROR_MESSAGE
        )
    }

    /**
     * prints the message to console
     * @param message custom text to be displayed
     */
    fun debug(message: String) {
        println(message)
    }
}