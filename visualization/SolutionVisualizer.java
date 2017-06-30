package visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.ArrayList;

import solution.Route;
import instance.Instance;
import instance.Visit;

public class SolutionVisualizer {
	private static int cwidth, cheight;
	
	public static void setWindowSize(int width, int height){
		if(cwidth==width && cheight==height)
			return;
		cwidth=width;
		cheight=height;
		StdDraw.setCanvasSize(width, height);
	}
	
	public static void visualize(Instance inst, ArrayList<Route> solution, boolean drawTw, boolean drawTime, String title, boolean pause){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setWindowSize(screenSize.height-100, screenSize.height-100);
		
		// Clear the canvas
		int width = new Double(inst.getMaxX()-inst.getMinX()).intValue();
		int height = new Double(inst.getMaxY()-inst.getMinY()).intValue();
		int padding = 20;
		System.out.println("Visualizing solution: "+width+"x"+height+" (This might take a bit of time)");

		StdDraw.clear();
		StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 16));
		StdDraw.text(0, inst.getMaxY()+20, title);
		StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 10));
		StdDraw.setXscale(inst.getMinX()-padding, inst.getMaxX()+padding);
		StdDraw.setYscale(inst.getMinY()-padding, inst.getMaxY()+padding);
		StdDraw.square(0, 0, 3);
		
		int color = 0;
		ArrayList<Color> colors = StdDraw.getColorPallette();
		
		for(Route r : solution){
			double prevX = 0;
			double prevY = 0;
			StdDraw.setPenColor(colors.get(color));
			color = (color+1)%colors.size();
			
			for(int i=0; i<r.size(); i++){
	
				Visit v = r.getVisit(i);
				StdDraw.line(prevX,prevY,v.getX(),v.getY());
				prevX = v.getX();
				prevY = v.getY();
				
				if(r.isLocked(i))
					StdDraw.filledCircle(v.getX(), v.getY(), 2);
				else
					StdDraw.circle(v.getX(), v.getY(), 2);
					
				// Do not draw depot label twice
				if(i==r.size()-1 && r.getVisit(r.size()-1).getX()==0 && r.getVisit(r.size()-1).getY()==0 )
					continue;
				
				String label = "";
				int offset = 5;
				if(drawTw){
					label = v.getTwLabel();
					StdDraw.text(v.getX(), v.getY()-offset, label);
					offset += 5;
				}
				if(drawTime){
					label = r.getTimeLabel(i);
					StdDraw.text(v.getX(), v.getY()-offset, label);
					offset += 5;
				}
				//StdDraw.text(v.getX(), v.getY()-10-offset, ""+i);
				
			}
		}
		
		System.out.println("Solution has been visualized");
		boolean cont = !pause;
		while(!cont){
			if(!StdDraw.hasNextKeyTyped()){
				try { Thread.sleep(100); }
				catch (InterruptedException e) { System.out.println("Error sleeping"); }
			}else{
				// Empty queue
				while(StdDraw.hasNextKeyTyped()){
					StdDraw.nextKeyTyped();
				}
				break;
			}
		}
	}
	
}
