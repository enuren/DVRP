import java.util.Random;

import distributions.Distribution;
import distributions.Normal;
import distributions.Uniform;
import solver.NaiveSolver;
import solver.Solver;
import visualization.SolutionVisualizer;
import eventSimulator.DiscreteEventSimulator;
import instance.CostFunction;
import instance.Instance;
import instance.InstanceGenerator;



public class DynamicVRP {

	public static void main(String args[]){
		// Make the randomness behave the same each time the program is run, uncomment for random or change the number to fix another random sequence
		Instance.rand = new Random(1);
		
		System.out.println("Creating instance");
		// The distribution of the coordinates. I.e. the coordinates will follow a normal distribution with a mean of 0 and an stdDev of 50
		Normal coordDist = new Normal(0, 50);
		// The distribution of appearance time of visits. I.e. the visits will appear in the simulation following a uniform distribution
		Uniform timeDist = new Uniform(0, 50);
		// How long time do we have to react from we know of a visit till its time window begins?
		Distribution reactTime = new Uniform(10, 15);
		// How large is the time window?
		Distribution twLength = new Uniform(5, 10);
		// Prepare to make one or more instances with the specified data
		InstanceGenerator instGen = new InstanceGenerator(coordDist, coordDist,reactTime, twLength);
		// Get a single instance
		Instance inst = instGen.generate(10, 10, timeDist);
		// Print out the instance
		System.out.println(inst);
		
		// Now we need to create a solver... This should be replaced by one of your making
		System.out.println("Creating solver");
		Solver solver = new NaiveSolver();
		
		// For now cost is 10000 per truck, 300 per hour and 50 per kilometer, and no punishment for being late
		CostFunction cf = new CostFunction(10000, 300, 50, 0);
		
		// Create a discrete event simulation
		System.out.println("Creating simulator");
		DiscreteEventSimulator sim = new DiscreteEventSimulator(inst, solver, cf);

		// Run the simulation (and show us what is happening)
		System.out.println("Simulating");
		sim.simulate(true);
		
		// Show the final solution (with all labels)
		SolutionVisualizer.visualize(inst, sim.getSolution(),true,true, "Done",true);
		
		System.out.println("Ended the dynamic VRP program.");
	}
	
}
