package distributions;

public interface Distribution {	
	/**
	 * Function for sampling a distribution
	 * @return A sample of the distribution in question
	 */
	public double sample();
}
