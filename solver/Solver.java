package solver;

import instance.CostFunction;
import instance.Visit;

import java.util.ArrayList;

import solution.Route;

public interface Solver {
	
	/**
	 * 
	 * @param solution The solution as it has been constructed so far
	 * @param unplannedVisits A list of all revealed but not yet planned visits. Any element left in the list will be sent during next solving
	 * @param time The current time.
	 * @return The time the solver should be called again if no events occur before that.
	 */
	public double solve(ArrayList<Route> solution, ArrayList<Visit> unplannedVisits, double time, CostFunction cf);
	
}
