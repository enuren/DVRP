package instance;

import java.util.ArrayList;

import solution.Route;

public class CostFunction {

	private double pulloutCost;
	private double hourlyCost;
	private double distanceCost;
	private double tardinessCost;
	
	public CostFunction(double pullout, double hourly, double distance, double tardiness){
		pulloutCost=pullout;
		hourlyCost=hourly;
		distanceCost=distance;
		tardinessCost=tardiness;
	}
	
	/**
	 * 
	 * @param solution to be evaluated
	 * @return The cost of the solution
	 */
	public double getCost(ArrayList<Route> solution){
		double cost=0;
		cost += getPulloutCost(solution);
		cost += getHourlyCost(solution);
		cost += getDistanceCost(solution);
		cost += getTardinessCost(solution);
		return cost;
	}
	
	public double getPulloutCost(ArrayList<Route> solution){
		return solution.size()*pulloutCost;
	}
	
	public double getHourlyCost(ArrayList<Route> solution){
		double cost = 0;
		for(Route r : solution){
			cost += r.getTime()*hourlyCost;
		}
		return cost;
	}

	public double getDistanceCost(ArrayList<Route> solution){
		double cost = 0;
		for(Route r : solution){
			cost += r.getDistance()*distanceCost;
		}
		return cost;
	}
	
	public double getTardinessCost(ArrayList<Route> solution){
		double cost = 0;
		for(Route r : solution){
			for(int i=0; i<r.size(); i++){
				double arrive = r.getArrivalTime(i);
				Visit v = r.getVisit(i);
				if(v.getTwEnd()>-1){
					cost += Math.max(arrive-v.getTwEnd(), 0)*tardinessCost;
				}
			}
		}
		return cost;
	}
	
	/**
	 * 
	 * @return The cost per truck used
	 */
	public double getPulloutCostFactor(){
		return pulloutCost;
	}
	
	/**
	 * 
	 * @return The cost per hour used
	 */
	public double getHourlyCostFactor(){
		return hourlyCost;
	}
	
	/**
	 * 
	 * @return The cost per distance travelled
	 */
	public double getDistanceCostFactor(){
		return distanceCost;
	}
	
	/**
	 * 
	 * @return The cost per hour delayed
	 */
	public double getTardinessCostFactor(){
		return tardinessCost;
	}
}
