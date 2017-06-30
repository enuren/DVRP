package distributions;

import instance.Instance;

public class Normal implements Distribution {
	
	double mean;
	double stdDev;
	
	public Normal(double mean, double stdDev){
		this.mean = mean;
		this.stdDev = stdDev;
	}
	
	@Override
	public double sample() {
		return Instance.rand.nextGaussian()*stdDev+mean;
	}
	
	@Override
	public String toString(){
		return "<Normal mean:"+mean+" stdDev:"+stdDev+"]>";
	}
	
}
