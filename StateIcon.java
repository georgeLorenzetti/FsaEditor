import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.Rectangle;
import java.awt.Color;
import java.lang.Math.*;
import java.util.*;

class StateIcon extends JComponent implements StateListener{

		public static int size = 100;
		private int xpos = 0;
		private int ypos = 0;

		private int maxX;
		private int maxY;

		private boolean selected = false;
		private boolean initial = false;
		private boolean fin = false;
		private boolean current = false;
		private State state = null;
		private FsaPanel panel = null;

		public StateIcon(State s, int x, int y){
			state = s;
			maxY = y;
			maxX = x;
			initial = s.isInitial();
			fin = s.isFinal();
			current = s.isCurrent();
			this.setPosition(state.getXpos(), state.getYpos());
		}

	    public void StateHasChanged(){
	    	this.setPosition(state.getXpos(), state.getYpos());
	    	Set<Transition> t = state.transitionsTo();
	    	Set<Transition> f = state.transitionsFrom();

	    	for(Iterator<Transition> it = state.transitionsTo().iterator(); it.hasNext(); ){
	    		TransitionImpl tr = (TransitionImpl)it.next();
	    		for(Iterator<TransitionIcon> itt = tr.getListeners().iterator(); itt.hasNext(); ){
	    			TransitionIcon ti = itt.next();
	    			ti.TransitionHasChanged();
	    		}
	    	}

	    	for(Iterator<Transition> it = state.transitionsFrom().iterator(); it.hasNext(); ){
	    		TransitionImpl tr = (TransitionImpl)it.next();
	    		for(Iterator<TransitionIcon> itt = tr.getListeners().iterator(); itt.hasNext(); ){
	    			TransitionIcon ti = itt.next();
	    			ti.TransitionHasChanged();
	    		}
	    	}
	    	initial = state.isInitial();
	    	fin = state.isFinal();
	    	current = state.isCurrent();
	    }
	    @Override
	    public void paintComponent(Graphics gg){
	    	Graphics2D g = (Graphics2D)gg;
	    	//g.drawRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
	    	int gX = 0;
	    	int gY = 0;
	    	g.setColor(Color.GRAY);
	    	if(selected){
	    		g.fillOval(gX, gY, size, size);
	 		}
	 		g.setStroke(new BasicStroke(1));
	 		g.setColor(Color.BLACK);
	 		g.drawOval(gX, gY, size, size);

	 		if(initial){
	 			g.setStroke(new BasicStroke(2));
	 			g.drawLine(size/2+2, 10, (size/2)-2, 20);
	 			g.drawLine(size/2+6, 16, (size/2)+2, 26);
	 			g.drawLine((size/2)-2, 20, size/2+6, 16);
	 		}
	 		if(fin){
	 			g.setStroke(new BasicStroke(1));
	 			g.drawOval(gX+4, gY+4, size-8, size-8);
	 		}
	 		if(current){
	 			//System.out.println("CURRENT - " + state.getName());
	    		g.fillOval(gX+(size/4)-10, gY+10, 20, 20);
	 		}

	 		g.drawString(state.getName(), (gX+size/2-10), (gY+size/2));
	    }


	    public void setPosition(int x, int y){
	    	xpos = x;
	    	ypos = y;
	    	this.setBounds(xpos, ypos, size, size);
	    	this.repaint();
	    }

	    public void setSize(int s){
	    	size = s;
	    	this.setBounds(xpos, ypos, size, size);
	    	this.repaint();
	    }

	    public void setState(State s){
	    	state = s;
	    }

	     public int getIconSize(){
	     	return size;
	     }

	    public State getState(){
	    	return state;
	    }

	    public void addToPanel(JPanel jp){
	    	jp.add(this);
	    }

	    public boolean insideCircle(int x, int y){
	    	int dx = Math.abs(x - (xpos + (size/2)));
	    	int dy = Math.abs(y - (ypos + (size/2)));
	    	int r = size/2;

	    	return ((dx*dx)+(dy*dy) <= (r*r));
	    }

	    public void toggleSelected(){
	    	if(selected){
	    		selected = false;
	    	}else{
	    		selected = true;
	    	}
	    }

	    public void setSelected(boolean b){
	    	selected = b;
	    }

	    public boolean isSelected(){
	    	return selected;
	    }

	    public Rectangle getRect(){
	    	return new Rectangle(xpos, ypos, size, size);
	    }

	    public int getMaxX(){
	    	return maxX;
	    }

	    public int getMaxY(){
	    	return maxY;
	    }

}