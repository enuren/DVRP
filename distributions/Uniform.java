package distributions;

import instance.Instance;

public class Uniform implements Distribution {
	
	double lowerBound;
	double upperBound;
	
	public Uniform(double lb, double ub){
		if(lb>=ub){
			throw new RuntimeException("The lower bound "+lb+" of the uniform distribution is not strictly smaller than the upper bound "+ub);
		}
		lowerBound=lb;
		upperBound=ub;
	}
	
	@Override
	public double sample() {
		return Instance.rand.nextDouble()*(upperBound-lowerBound)+lowerBound;
	}
	
	@Override
	public String toString(){
		return "<Uniform:["+lowerBound+","+upperBound+"]>";
	}
	
}
