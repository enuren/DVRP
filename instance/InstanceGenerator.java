package instance;

import distributions.Distribution;

public class InstanceGenerator {
	private Distribution xCoordDist;
	private Distribution yCoordDist;
	private Distribution reactTime;
	private Distribution twLength;
	
	public InstanceGenerator(Distribution xCoord, Distribution yCoord, Distribution reactTime, Distribution twLength){
		xCoordDist = xCoord;
		yCoordDist = yCoord;
		this.reactTime = reactTime;
		this.twLength = twLength;
	}
	
	/**
	 * Return an instance With the given characteristics
	 * @param nPreknown The number of visits known before execution starts
	 * @param nRevealed The number of visits revealed during execution
	 * @param timeDist The distribution of the time visits becomes known in the instance
	 * @return The generated instance
	 */
	public Instance generate(int nPreknown, int nRevealed, Distribution timeDist){
		Instance inst = new Instance();
		
		for(int i=0; i<nPreknown; i++){
			inst.generateVisit(xCoordDist, yCoordDist, null, reactTime, twLength);
		}
		
		for(int i=0; i<nRevealed; i++){
			inst.generateVisit(xCoordDist, yCoordDist, timeDist, reactTime, twLength);
		}
		
		return inst; 
	}
}
