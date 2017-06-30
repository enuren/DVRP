package eventSimulator;

import instance.Visit;

public class Event implements Comparable<Event>{
	private double awareTime;
	private Visit visit;
	
	public Event(double awareTime, Visit v){
		this.awareTime = awareTime;
		this.visit = v;
	}

	/**
	 * 
	 * @return The time the simulator becomes aware of the visit
	 */
	public double getAwareTime(){
		return awareTime;
	}
	
	/**
	 * 
	 * @return The visit associated with the event-
	 */
	public Visit getVisit(){
		return visit;
	}
	
	/**
	 * Makes events sortable by their awareness time. The first to be revealed will be sorted last
	 */
	@Override
	public int compareTo(Event e) {
		return -Double.compare(awareTime,e.awareTime);
	}
	
	@Override
	public String toString(){
		return "<Event: "+visit+" @ "+awareTime+">";
	}
	
}
