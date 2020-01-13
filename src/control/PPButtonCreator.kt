package control

import main.Settings
import java.io.File
import java.util.*
import javax.swing.JButton

/**
 * a thread handler that loads all of the `PicturePanel` button thumbnails in rapid succession
 * @param picturePanel the `PicturePanel` the buttons need to be added to
 * @param folder the folder the images are in
 * @author McAJBen@gmail.com
 * @since 2.3
 */
class PPButtonCreator(
    private val picturePanel: PicturePanel,
    folder: File
) {
    /**
     * the queue of files to be loaded
     */
    private val queue = Collections.synchronizedList(LinkedList<ButtonInfo>())
    /**
     * the returned buttons that should be placed in the `ControlPicture`
     */
    private val buttons: Array<JButton?>

    init {
        Settings.listFilesInOrder(folder).filter {
            val suffix = it.name.substring(it.name.lastIndexOf('.') + 1)
            suffix.equals("PNG", ignoreCase = true)
                || suffix.equals("JPG", ignoreCase = true)
                || suffix.equals("JPEG", ignoreCase = true)
        }.forEachIndexed { index, file ->
            queue.add(ButtonInfo(index, file))
        }

        buttons = arrayOfNulls(queue.size)
    }

    /**
     * starts the loading process
     */
    @Synchronized
    fun run() {
        val bmt = arrayOfNulls<ButtonMakerThread>(Settings.SYS_THREADS)
        for (i in bmt.indices) {
            bmt[i] = ButtonMakerThread(ButtonMakerThread::class.java.name + "-" + i)
            bmt[i]!!.start()
        }
        for (i in bmt.indices) {
            try {
                bmt[i]!!.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        for (b in buttons) {
            println(b!!.text)
            picturePanel.add(b)
        }
    }

    /**
     * A Thread that loads a thumbnail for a button, and adds it to the picture panel
     * @param string the name of the thread
     * @author McAJBen@gmail.com
     */
    private inner class ButtonMakerThread(string: String) : Thread(string) {
        override fun run() {
            try {
                while (true) {
                    val info = queue.removeAt(0)
                    buttons[info.position] = picturePanel.createPPButton(info.file)
                }
            } catch (e: IndexOutOfBoundsException) {}
        }
    }

    private data class ButtonInfo(
        val position: Int,
        val file: File
    )
}