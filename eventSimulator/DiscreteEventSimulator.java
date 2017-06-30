package eventSimulator;

import instance.CostFunction;
import instance.Instance;
import instance.Visit;

import java.util.ArrayList;
import java.util.Collections;

import solution.Route;
import solver.Solver;
import visualization.SolutionVisualizer;

public class DiscreteEventSimulator {
	private double time = -1;
	private Solver solver;
	private Instance instance;
	private CostFunction costFunc;
	private ArrayList<Event> events = new ArrayList<Event>();
	
	private ArrayList<Route> solution = new ArrayList<Route>();
	
	private boolean verbose = true;
	
	public DiscreteEventSimulator(Instance i, Solver s, CostFunction cf){
		instance = i;
		solver = s;
		costFunc = cf;
	}
	
	/**
	 * Sets the solver to be used during the optimization
	 * @param s
	 */
	public void setSolver(Solver s){
		solver = s;
	}
	
	/**
	 * Sets the cost function to be used during the simulation. This function will be sent to the solver
	 * @param cf
	 */
	public void setCostFunction(CostFunction cf){
		costFunc = cf;
	}
	
	/**
	 * Add an event to the simulator
	 * @param e The event to be added
	 */
	public void addEvent(Event e){
		events.add(e);
		Collections.sort(events);
	}
	
	/**
	 * Add a list of events to the simulation
	 * @param es
	 */
	public void addEvents(ArrayList<Event> es){
		events.addAll(es);
		Collections.sort(events);
	}
	
	/**
	 * Reset the simulation
	 */
	private void reset(){
		solution.clear();
		solution.add(new Route(0.0));
		addEvents(instance.getEvents());
		time = -1;
	}
	
	/**
	 * Sets whether the simulation should be verbose. Defaults to true
	 * @param verbose
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Runs the simulation
	 */
	public void simulate(boolean visualize){
		// Create an empty solution, and fetch events from the instance
		reset();
		
		// No next trigger has been requested, so set the time "infinitely" far in the future
		double nextTriggerRequest = Double.MAX_VALUE;
		ArrayList<Visit> revealedVisits = new ArrayList<Visit>();
		
		// Go on until there are no events, and all visits have been assigned
		while(revealedVisits.size()+events.size()>0){
			double nextEvent = events.get(events.size()-1).getAwareTime();
			double nextTime = Math.min(nextEvent, nextTriggerRequest);
			// Reveal events if current time permits
			if(nextEvent==nextTime){
				for(int i=events.size()-1; i>=0; i--){
					if(events.get(i).getAwareTime()<=nextTime){
						Event e = events.remove(i);
						revealedVisits.add(e.getVisit());
					}else{
						break;
					}
				}
			}

			// Update timings
			time = nextTime;
			for(Route r:solution){
				r.updateTime(time);
			}

			// Update what visits are locked
			for(Route r: solution)
				r.updateLocks(nextTime);
			
			// Call solver to get new solution
			int revealedBefore = revealedVisits.size();
			if(verbose) System.out.println("/==================================\n"
					+ "|Calling solver at time "+time+" with "+revealedBefore+" revealed visits.");
			nextTriggerRequest = solver.solve(solution, revealedVisits, nextTime, costFunc);
			if(verbose) System.out.println("| "+(revealedBefore-revealedVisits.size())+"/"+revealedBefore+" placed."
					+"\n\\==================================");
			

			
			// Check (loosely) the validity of the given data
			if(nextTriggerRequest<time)
				throw new RuntimeException("The next requested trigger "+nextTriggerRequest+" is in the past, as current time is "+time);

			if(events.size()==0 && revealedVisits.size()>0 && nextTriggerRequest >= Double.MAX_VALUE)
				throw new RuntimeException("Ran out of events before all visits were planned, and the next request for reoptimization is at or near infinity."
						+ "\nDid you remember to remove visits added to the solution?");
			
			if(visualize) SolutionVisualizer.visualize(instance, solution, true, true, "Time: "+time, true);
		}
		
		// Call solver until it sends vehicle to depot
		while(nextTriggerRequest<Double.POSITIVE_INFINITY){
			// Check if all vehicles are home
			boolean allHome = true;
			for(Route r : solution){
				if(r.getVisit(r.size()-1).getX()!=0 || r.getVisit(r.size()-1).getY()!=0){
					allHome = false;
				}
			}
			if(allHome) break;
			
			// Update what visits are locked
			for(Route r: solution)	
				r.updateLocks(nextTriggerRequest);
			
			// Call solver to get new solution
			double oldTrigger = nextTriggerRequest;
			int revealedBefore = revealedVisits.size();
			if(verbose) System.out.println("/==================================\n|Calling solver at time "+time+" with "+revealedBefore+" revealed visits.");
			nextTriggerRequest = solver.solve(solution, revealedVisits, nextTriggerRequest, costFunc);
			if(verbose) System.out.println("| "+(revealedBefore-revealedVisits.size())+"/"+revealedBefore+" placed.\n\\==================================");
			
			// Avoid looping forever if the solver does not increase time
			if(oldTrigger==nextTriggerRequest)
				break;
		}
		
		// Add depot visits at the end if they aren't there
		for(Route r : solution){
			if(r.getVisit(r.size()-1).getX()!=0 || r.getVisit(r.size()-1).getY()!=0){
				r.AddVisit(r.size(), r.getVisit(0), 10000);
				System.err.println("Inserted the depot at the end of the route as it was missing");
			}
		}
		
		// Update what visits are locked
		for(Route r: solution)
			r.updateLocks(Double.POSITIVE_INFINITY);
		
		// Check if solution is valid
		String err = instance.isSolutionValid(solution);
		if(err.length()>0){
			System.err.println("The solution is not valid:\n\t"+err);
		}else{
			if(verbose) System.out.println("Solution contains all visits. Cost: "+costFunc.getCost(solution));
		}
	}
	
	/**
	 * Get the solution after simulation concludes
	 * @return The solution resulting from the simulation
	 */
	public ArrayList<Route> getSolution() {
		return solution;
	}
	
	public String toString(){
		return "<EventSimulator with "+events.size()+" events, solver: +"+solver+"+>";
	}

}
