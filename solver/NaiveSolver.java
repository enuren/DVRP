package solver;

import instance.CostFunction;
import instance.Visit;

import java.util.ArrayList;

import solution.Route;

public class NaiveSolver implements Solver{

	@Override
	public double solve(ArrayList<Route> solution,
			ArrayList<Visit> unplannedVisits, double time, CostFunction cf) {
		
		// Take out the first route in the solution
		Route first = solution.get(0);
		
		// Insert each unplanned visit after turn starting with the last in the list
		for(int i=unplannedVisits.size()-1; i>=0; i--){
			Visit v = unplannedVisits.remove(i);
			first.AddVisit(first.size(), v, time);
		}
		
		// Request next solver to be in an infinite amount of time OR when next event occurs
		return Double.POSITIVE_INFINITY;
	}

	// If a name for the solver is ever required, return this:
	public String toString(){
		return "NaiveSolver";
	}
}
