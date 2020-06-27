package control

/**
 * a listener for events coming from `ControlLoading`
 * @author McAJBen@gmail.com
 */
interface ControlLoadingListener {

	/**
	 * starts a timer for a number of seconds
	 * @param seconds the number of seconds to run a timer for
	 */
	fun setTimer(seconds: Int)

	/**
	 * closes and hides the timer
	 */
	fun clearTimer()
}
