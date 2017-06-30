package instance;

import java.text.DecimalFormat;

public class Visit {
	private double x;
	private double y;
	private double twStart=0;
	private double twEnd=100000000;
	private static final DecimalFormat myFormatter = new DecimalFormat("###.##");
	
	public Visit(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Visit(double x, double y, double twStart, double twEnd){
		this.x = x;
		this.y = y;
		this.twStart = twStart;
		this.twEnd = twEnd;
	}
	
	/**
	 * Calculates the Euclidian distance between two visits
	 * @param v
	 * @return
	 */
	public double getDistance(Visit v){
		return Math.sqrt( (v.x-x)*(v.x-x)+(v.y-y)*(v.y-y) );
	}
	
	/**
	 * 
	 * @return The x coordinate of the visit
	 */
	public double getX(){
		return x;
	}
	
	/**
	 * 
	 * @return The y coordinate of the visit
	 */
	public double getY(){
		return y;
	}

	/**
	 * 
	 * @return The start of the time window
	 */
	public double getTwStart(){
		return twStart;
	}
	
	/**
	 * 
	 * @return The end of the time window
	 */
	public double getTwEnd(){
		return twEnd;
	}
	
	/**
	 * Returns a string representation of the visit
	 */
	public String toString(){
		if(twEnd>-1)
			return "<Visit ("+x+","+y+") ["+twStart+","+twEnd+"]>";
		return "<Visit ("+x+","+y+")>";
	}
	
	public String getTwLabel(){
		return "["+myFormatter.format(twStart)+", "+myFormatter.format(twEnd)+"]";
	}
}
