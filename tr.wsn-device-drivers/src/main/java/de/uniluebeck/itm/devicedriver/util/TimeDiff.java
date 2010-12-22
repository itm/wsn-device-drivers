package de.uniluebeck.itm.devicedriver.util;


public class TimeDiff {

	private long lastTouch = System.currentTimeMillis();

	private long timeOutMillis = 0;

	/**
	 * Constructor
	 */
	public TimeDiff() {
	}

	/**
	 * Constructor
	 */
	public TimeDiff(long timeOutMillis) {
		setTimeOutMillis(timeOutMillis);
	}

	/**	 */
	public void touch() {
		lastTouch = System.currentTimeMillis();
	}

	/**	 */
	public long absoluteMillis() {
		return lastTouch;
	}

	/**	 */
	public boolean isTimeout() {
		return ms() >= timeOutMillis;
	}

	/**	 */
	public boolean noTimeout() {
		return !isTimeout();
	}

	/**	 */
	public long ms() {
		return System.currentTimeMillis() - lastTouch;
	}

	/**	 */
	public long s() {
		return Math.round(((double) ms()) / 1000.0);
	}

	/**	 */
	public long m() {
		return Math.round(((double) s()) / 60.0);
	}

	/**	 */
	public long h() {
		return Math.round(((double) m()) / 60.0);
	}

	/**	 */
	public long d() {
		return Math.round(((double) h()) / 24.0);
	}

	/**	 */
	public void setTimeOutMillis(long timeOutMillis) {
		this.timeOutMillis = timeOutMillis;
	}

}

