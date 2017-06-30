package distributions;

public class Singular implements Distribution {

	private double value;
	
	public Singular(Double val){
		value = val;
	}
	
	@Override
	public double sample() {
		return value;
	}
	
	@Override
	public String toString(){
		return "<Singular:"+value+">";
	}

}
