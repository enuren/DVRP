
package instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import solution.Route;
import distributions.Distribution;
import eventSimulator.Event;

public class Instance {
	// The random generator used in all distributions
	public static Random rand = new Random();
	
	// The most extreme coordinates appearing in the solution
	private double minX,maxX,minY,maxY;
	// The events that are gradually revealed.
	private ArrayList<Event> events= new ArrayList<Event>();
	
	public Instance(){
		this.minX=Double.POSITIVE_INFINITY;
		this.maxX=Double.NEGATIVE_INFINITY;
		this.minY=Double.POSITIVE_INFINITY;
		this.maxY=Double.NEGATIVE_INFINITY;
	}
	
	/**
	 * Generate an event and its associated visit and add it to the simulation
	 * @param xDist A distribution describing the likelihood of the visit having that x coordinate
	 * @param yDist A distribution describing the likelihood of the visit having that y coordinate
	 * @param awareDist A distribution describing the likelihood of the visit being revealed to the simulator at that time
	 * @param reactionTime The time after the simulator becomes aware of a visit till its time window starts
	 * @param twLength The length of the time window
	 */
	public void generateVisit(Distribution xDist, Distribution yDist, Distribution awareDist, 
			Distribution reactionTime, Distribution twLength){
		double x=xDist.sample();
		double y=yDist.sample();
		double aware = -1;
		if(awareDist!=null)
			aware = awareDist.sample();
		
		double twStart = Math.max(aware,0)+reactionTime.sample();
		double twEnd = twStart+twLength.sample();
		Visit v = new Visit(x, y, twStart, twEnd);
		
		if(x>maxX){
			maxX = x;
		}
		if(x<minX){
			minX = x;
		}
		if(y>maxY){
			maxY=y;
		}
		if(y<minY){
			minY=y;
		}

		Event e = new Event(aware,v);
		events.add(e);
		//System.out.println("Added "+e+" to the instance.");
	}
	
	/**
	 * Get the events to be revealed to the solver
	 * @return An arraylist of events
	 */
	public ArrayList<Event> getEvents(){
		return new ArrayList<Event>(events);
	}
	
	/**
	 * Provides a textual description of errors if they can be detected in the given solution
	 * @param solution The solution to be checked.
	 * @return The errors found, if no errors are detected, the empty string will be returned.
	 */
	public String isSolutionValid(ArrayList<Route> solution){
		// Check that each visit is in the solution
		for(Event e: events){
			Visit visit = e.getVisit();
			boolean found = false;
			for(Route r : solution){
				for(int v=0; v<r.size() && !found; v++){
					if(r.getVisit(v).equals(visit)){
						found = true;
					}
				}
				
				if(r.getVisit(r.size()-1).getX()!=0 && r.getVisit(r.size()-1).getY()!=0 )
					return "Route does not end in a depot";
			}
			if(!found){
				return "Failed to find "+visit+" in the solution!";
			}
		}
		
		return "";
	}

	public double getMinX() {
		return minX;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxY() {
		return maxY;
	}

	/**
	 * Returns a string representation of the instance
	 */
	@Override
	public String toString(){
		String str = "+============================================================\n"
				+ "| Instance x: ["+minX+","+maxX+"] y: ["+minY+","+maxY+"]:\n";
		Collections.sort(events);
		for(int e=events.size()-1;e>=0; e--){
			str += "|\t"+events.get(e)+"\n";
		}
		str += "+============================================================";
		return str;
	}
	
}
