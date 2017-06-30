import instance.CostFunction;
import instance.Instance;
import instance.InstanceGenerator;

import java.io.PrintWriter;
import java.util.ArrayList;

import distributions.Distribution;
import distributions.Normal;
import distributions.Uniform;
import eventSimulator.DiscreteEventSimulator;
import solver.NaiveSolver;
import solver.Solver;


public class TestBench {

	public static void main(String[] args){
		// Cost function
		CostFunction costFunc = new CostFunction(10000, 200, 10, 300);
		
		// Setup solvers and instances
		ArrayList<Instance> instances = getNormdistScenarioA(1000);
		ArrayList<Solver> solvers = new ArrayList<Solver>();
		
		// Add your solvers here
		solvers.add(new NaiveSolver());
		
		runTests(costFunc, instances, solvers);
		
	}
	
	private static ArrayList<Instance> getNormdistScenarioA(int nTestInstances) {
		ArrayList<Instance> instances = new ArrayList<Instance>();
		Distribution nPreknown = new Uniform(20, 30);
		Distribution nRevealed = new Uniform(5,10);
		
		Distribution xCoord = new Normal(25, 25);
		Distribution yCoord = new Normal(10, 25);
		Distribution timeDist = new Uniform(0, 8);
		Distribution timeToTwStart = new Uniform(2, 3);
		Distribution twLength = new Uniform(1, 2);
		
		// Setup instance generator
		InstanceGenerator instGen = new InstanceGenerator(xCoord, yCoord, timeToTwStart, twLength);
		
		for(int i=0; i<nTestInstances; i++){
			int nPre = ((Double) nPreknown.sample()).intValue();
			int nRev = ((Double) nRevealed.sample()).intValue();
			instances.add(instGen.generate(nPre, nRev, timeDist));
		}
		
		return instances;
	}

	private static ArrayList<Instance> getNormdistScenarioB(int nTestInstances) {
		ArrayList<Instance> instances = new ArrayList<Instance>();
		Distribution nPreknown = new Uniform(1, 10);
		Distribution nRevealed = new Uniform(10,100);
		
		Distribution xCoord = new Normal(25, 25);
		Distribution yCoord = new Normal(10, 25);
		Distribution timeDist = new Uniform(0, 8);
		Distribution timeToTwStart = new Uniform(2, 3);
		Distribution twLength = new Uniform(2, 3);
		
		// Setup instance generator
		InstanceGenerator instGen = new InstanceGenerator(xCoord, yCoord, timeToTwStart, twLength);
		
		for(int i=0; i<nTestInstances; i++){
			int nPre = ((Double) nPreknown.sample()).intValue();
			int nRev = ((Double) nRevealed.sample()).intValue();
			instances.add(instGen.generate(nPre, nRev, timeDist));
		}
		
		return instances;
	}
	
	private static ArrayList<Instance> getNormdistScenarioC(int nTestInstances) {
		ArrayList<Instance> instances = new ArrayList<Instance>();
		Distribution nPreknown = new Uniform(1, 10);
		Distribution nRevealed = new Uniform(10,100);
		
		Distribution xCoord = new Normal(25, 25);
		Distribution yCoord = new Normal(10, 25);
		Distribution timeDist = new Uniform(0, 8);
		Distribution timeToTwStart = new Uniform(1, 2);
		Distribution twLength = new Uniform(0.5, 1);
		
		// Setup instance generator
		InstanceGenerator instGen = new InstanceGenerator(xCoord, yCoord, timeToTwStart, twLength);
		
		for(int i=0; i<nTestInstances; i++){
			int nPre = ((Double) nPreknown.sample()).intValue();
			int nRev = ((Double) nRevealed.sample()).intValue();
			instances.add(instGen.generate(nPre, nRev, timeDist));
		}
		
		return instances;
	}
	
	private static void runTests(CostFunction costFunc,
			ArrayList<Instance> instances, ArrayList<Solver> solvers) {
		double[][] costs = new double[solvers.size()][instances.size()];
		
		for(int i=0; i<instances.size(); i++){
			DiscreteEventSimulator sim = new DiscreteEventSimulator(instances.get(i), null, costFunc);
			sim.setVerbose(false);
			
			for(int s=0; s<solvers.size(); s++){
				sim.setSolver(solvers.get(s));
				sim.simulate(false);
				double cost = costFunc.getCost(sim.getSolution());
				costs[s][i] = cost;
			}
		}
		
		writeToFile("testOutput.csv", solvers, costs);
		
		/*
		 * Print averages
		 */
		System.out.println("+=======================================================================");
		System.out.print("| Solver:  | ");
		for(int s=0; s<solvers.size(); s++){
			System.out.print(solvers.get(s));
			if(s<solvers.size()-1)
				System.out.print("\t| ");
		}
		System.out.println();
		
		System.out.print("| Average: | ");
		for(int s=0; s<solvers.size(); s++){
			double avgCost = 0;
			for(int i=0; i<instances.size(); i++){
				avgCost += costs[s][i];
			}
			System.out.print(avgCost/instances.size());
			if(s<solvers.size()-1)
				System.out.print("\t| ");
		}
		System.out.println("");
		System.out.println("+=======================================================================");
	}
	
	private static void writeToFile(String filename, ArrayList<Solver> solvers, double costs[][]){
		try{
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			
			for(int s=0; s<solvers.size(); s++){
				writer.print(solvers.get(s));
				if(s<solvers.size()-1)
					writer.print(",");
			}
			writer.println();
			
			for(int i=0; i<costs[0].length; i++){
				for(int s=0; s<solvers.size(); s++){
					writer.print(costs[s][i]);
					if(s<solvers.size()-1)
						writer.print(",");
				}
				writer.println("");
			}

			writer.close();
		} catch(Exception e){
			System.err.println("Failed to write to file:");
			e.printStackTrace();
		}
	}
	
}
