package de.uniluebeck.itm.wsn.drivers.core.operation;

/**
 * This factory creates a ProgressManager that will only call the onProgressChange when a 
 * fraction difference of 0.01 is reached.
 * 
 * @author Malte Legenhausen
 */
public class RoundedProgressManagerFactory implements ProgressManagerFactory {

	private class RoundedProgressCallback implements ProgressCallback {
		
		private static final float DIFFERENCE = 0.01f;

		private final ProgressCallback callback;
		
		private float lastFraction = -1f;
		
		public RoundedProgressCallback(ProgressCallback callback) {
			this.callback = callback;
		}
		
		@Override
		public void onProgressChange(float fraction) {
			if (fraction - lastFraction >= DIFFERENCE) {
				lastFraction = fraction;
				callback.onProgressChange(fraction);
			}
		}
		
	}
	
	@Override
	public ProgressManager create(ProgressCallback callback) {
		return new RootProgressManager(new RoundedProgressCallback(callback));
	}
}
