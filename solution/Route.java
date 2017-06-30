package solution;

import instance.Visit;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Route {
	private ArrayList<Visit> visits = new ArrayList<Visit>();
	private ArrayList<Double> arrivalTimes = new ArrayList<Double>();
	private ArrayList<Double> departureTimes = new ArrayList<Double>();	
	private ArrayList<Boolean> locked = new ArrayList<Boolean>();
	private double speed = 50;
	private boolean drivefirst = true;
	private static final DecimalFormat myFormatter = new DecimalFormat("###.##");
	
	public Route(double time){
		// Add a visit representing start in orego
		AddVisit(0, new Visit(0, 0),0);
		arrivalTimes.set(0, time);
		departureTimes.set(0, -1.0);
		locked.set(0,true);
	}
	
	/**
	 * A constructor creating a copy of a route.
	 * @param r The rout to be copied
	 */
	public Route(Route r){
		visits.addAll(r.visits);
		arrivalTimes.addAll(r.arrivalTimes);
		departureTimes.addAll(r.departureTimes);
		locked.addAll(r.locked);
		speed = r.speed;
	}
	
	/**
	 * Remove a visit from the route. Fails if the visit is locked already.
	 * @param index The index of the visit to remove
	 * @param time The current time to be used when updating timings
	 * @return The visit that was removed
	 * @throws RuntimeException If the visit is locked or if index is out of bounds.
	 */
	public Visit removeVisit(int index, double time) throws RuntimeException {
		if(index>=visits.size())
			throw new RuntimeException("Attempted to remove index "+index+" of a route of size "+visits.size());
		if(locked.get(index)){
			System.out.println("Removeing "+index+" at time "+time);
			System.out.println(this);
			throw new RuntimeException("Attempted to remove a locked visit");
		}
		locked.remove(index);
		arrivalTimes.remove(index);
		departureTimes.remove(index);
		Visit removed = visits.remove(index);
		updateTime(time);
		return removed;
	}
	
	/**
	 * 
	 * @param index
	 * @return The visit at the specified index
	 */
	public Visit getVisit(int index){
		return visits.get(index);
	}
	
	/**
	 * Return the arrival time of the index'th visit 
	 * @param index
	 * @return
	 */
	public double getArrivalTime(int index){
		return arrivalTimes.get(index);
	}
	
	/**
	 * 
	 * @return A copy of the arrival times
	 */
	public ArrayList<Double> getArrivalTimes(){
		return new ArrayList<Double>(arrivalTimes);
	}

	/**
	 * Return the departure time of the index'th visit 
	 * @param index
	 * @return
	 */
	public double getDepartureTime(int index){
		return departureTimes.get(index);
	}
	
	/**
	 * 
	 * @return A copy of the departure times
	 */
	public ArrayList<Double> getDepartureTimes(){
		return new ArrayList<Double>(departureTimes);
	}
	
	/**
	 * Function to determine is a visit is locked
	 * @param index The index to check for locked status
	 * @return True is and only if the visit at the specified index is locked.
	 */
	public boolean isLocked(int index){
		if(index==visits.size())
			return false;
		return locked.get(index);
	}
	
	/**
	 * 
	 * @return A list of boolean indicating if a visit is locked
	 */
	public ArrayList<Boolean> getIsLocked(){
		return new ArrayList<Boolean>(locked);
	}
	
	/**
	 * 
	 * @return The size of the route including the depot in the start
	 */
	public int size(){
		return visits.size();
	}
	
	/**
	 * Adds a visit at the specified index and updates timings accordingly
	 * @param index The index to insert the visit at
	 * @param v The visit to insert
	 * @param time The time the insertion is done, to be used when updating timings
	 * @throws RuntimeException
	 */
	public void AddVisit(int index, Visit v, double time) throws RuntimeException {
		if(locked.size()>index && locked.get(index)){
			if(index>0 && departureTimes.get(index-1)==time){
				locked.set(index, false);
			}else
				throw new RuntimeException("Attempted to move a locked visit by adding something before it");
		}
		locked.add(index,false);
		arrivalTimes.add(index,Double.MAX_VALUE);
		departureTimes.add(index,Double.MAX_VALUE);
		visits.add(index, v);
		updateTime(time);
	}
	
	/**
	 * Determine if a drive first (true) or wait first (false) policy is used when updating timings and locks
	 * @param drivefirst
	 */
	public void setDriveFirst(boolean drivefirst){
		this.drivefirst = drivefirst;
	}
	
	
	/**
	 * Replace a visit with another
	 * @param index The index of the visit to replace
	 * @param v The visit to insert
	 * @param time The current time
	 * @return The visit that was just replaced
	 * @throws RuntimeException
	 */
	public Visit replaceVisit(int index, Visit v, double time) throws RuntimeException {
		if(locked.size()>index && locked.get(index)){
			if(index>0 && departureTimes.get(index-1)==time){
				locked.set(index, false);
			}else
				throw new RuntimeException("Attempted to replace a locked visit");
		}
		Visit old = visits.get(index);
		visits.set(index, v);
		updateTime(time);
		return old;
	}
	
	/**
	 * Update the locks
	 * @param time
	 */
	public void updateLocks(double time){
		// Update the timings for all other visits
		for(int i=1; i<visits.size(); i++){			
			// If we have already started driving, ensure we have locked
			if(departureTimes.get(i-1)<time){
				locked.set(i, true);
			}
		}
	}
	
	private void updateTimeDF(double time){
		
		// Update the departure time from the depot if it is not set yet
		if(visits.size()>1 && departureTimes.get(0)==-1.0){
			departureTimes.set(0, Math.max(0,time));
		}
		
		// Update the timings for all other visits
		for(int i=1; i<visits.size(); i++){
			// Check if we have information to set a new departure time for a locked visit
			if(locked.get(i) && departureTimes.get(i)==-1 && i<visits.size()-1){
				departureTimes.set(i, Math.max(Math.max(arrivalTimes.get(i),time),visits.get(i).getTwStart()));
			}
			
			// If the visit is not locked, it's arrival time must be updated
			if(!locked.get(i)){
				double travelTime = visits.get(i-1).getDistance(visits.get(i))/speed;
				arrivalTimes.set(i, departureTimes.get(i-1)+travelTime);
				
				if(i<visits.size()-1){ // Do we know where to go after the visit?
					departureTimes.set(i, Math.max(arrivalTimes.get(i),visits.get(i).getTwStart()));
				}
			}
						
			if(i==visits.size()-1){ // If we are at the last visit, wait
				departureTimes.set(i, -1.0);
			}
		}		
	}
	
	private void updateTimeWF(double time){
		// Calculate latest departure
		ArrayList<Double> latestDeparture = new ArrayList<Double>(departureTimes);
		
		if(latestDeparture.size()>0)
			latestDeparture.set(latestDeparture.size()-1, 10000000.0);
		
		for(int i = visits.size()-2; i>=0; i--){
			Visit cv = visits.get(i);
			Visit nv = visits.get(i+1);
			double traveltime = cv.getDistance(nv)/speed;
			double toNext = nv.getTwEnd();
			if(toNext<0)
				toNext = 10000000.0;
			toNext -= traveltime;
			double latestDep = Math.min(toNext,latestDeparture.get(i+1)-traveltime);
			latestDeparture.set(i, latestDep);
		}
	
		
		// Update the departure time from the depot if it is not set yet
		if(visits.size()>1 && (departureTimes.get(0) == -1.0 || departureTimes.get(0)>time ) ){
			departureTimes.set(0, Math.max(time, latestDeparture.get(0)) );
		}
		
		// Update the timings for all other visits
		for(int i=1; i<visits.size(); i++){
			// Check if we have information to set a new departure time for a locked visit
			if(locked.get(i) && departureTimes.get(i)==-1 && i<visits.size()-1){
				departureTimes.set(i, Math.max(Math.max(latestDeparture.get(i),arrivalTimes.get(i)), visits.get(i).getTwStart() ) );
			}
			
			// If the visit is not locked, it's arrival time must be updated
			if(!locked.get(i)){
				double travelTime = visits.get(i-1).getDistance(visits.get(i))/speed;
				arrivalTimes.set(i, departureTimes.get(i-1)+travelTime);
				
				if(i<visits.size()-1){ // Do we know where to go after the visit?
					departureTimes.set(i, Math.max(Math.max(latestDeparture.get(i),arrivalTimes.get(i)),
							visits.get(i).getTwStart()) );
				}
			}
						
			if(i==visits.size()-1){ // If we are at the last visit, wait
				departureTimes.set(i, -1.0);
			}
		}
	
//		System.out.println(this);
//		System.out.println(latestDeparture);
		
	}
	
	/**
	 * Update the timings of the arrivals and departures.
	 * @param time
	 */
	public void updateTime(double time){
		//System.out.println("Updating time at "+time);
		if(drivefirst)
			updateTimeDF(time);
		else
			updateTimeWF(time);
	}
	
	/**
	 * Updates the maximum speed of the vehicle
	 * @param speed
	 */
	public void setMaxSpeed(double speed){
		this.speed = speed;
	}
	
	/**
	 * 
	 * @return The speed the vehicle is traveling at
	 */
	public double getSpeed(){
		return speed;
	}
	
	/**
	 * 
	 * @return The distance traveled by the vehicle
	 */
	public double getDistance(){
		double time = 0;
		for(int i=0; i<visits.size()-1; i++){
			time += visits.get(i).getDistance(visits.get(i+1));
		}
		return time;
	}
	
	/**
	 * 
	 * @return The time used by the vehicle
	 */
	public double getTime(){
		return arrivalTimes.get(arrivalTimes.size()-1)-departureTimes.get(0);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTimeLabel(int index){
		String depart = "?";
		if(departureTimes.get(index)!=-1) depart = myFormatter.format(departureTimes.get(index));
		return myFormatter.format(arrivalTimes.get(index))+" -> "+depart;
	}
	
	/**
	 * Returns a string representation of the route.
	 */
	public String toString(){
		String str = "<Route: ";
		for(int i=0; i<visits.size(); i++){
			Visit v = visits.get(i);
			str += "\t"+i+": "+ v +"\t"+ "At time: "+arrivalTimes.get(i)+"->"+departureTimes.get(i);
			if(locked.get(i)) str += " locked";
			str += "\n";
		}
		str += "\t"+visits.get(0);
		return str+">";
	}
	
}
