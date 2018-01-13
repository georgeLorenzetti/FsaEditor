import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.lang.*;
import java.lang.Math.*;

class FsaPanel extends JPanel implements FsaListener {

	private FsaImpl fsa;
    private FsaEditor parentPointer;
    private FsaPanel selfPointer;
	private Set<State> states;
	private Set<Transition> transitions;

	private Set<StateIcon> selectedStates;
	private MouseListener mouseListener;
	private MouseMotionListener mouseMotionListener;

	private int boundX = 0;
	private int boundY = 5;
	private int width = 1000;
	private int height = 500;

	//Selecting FSA variable
    private String selectionBoxState;
    private int x0;
    private int y0;
    private int x1;
    private int y1;

    private State recentState = null;
    private State first = null;
    private State second = null;
    private Point helperLine1 = null;
    private Point helperLine2 = null;
    private String newTrans = "";

	public FsaPanel(int x, int y, int w, int h, FsaEditor p){
        selfPointer = this;
        parentPointer = p;
		boundX = x;
		boundY = y;
		width = w;
		height = h;

		selectionBoxState = "IDLE";
        this.setLayout(null);
        this.setBounds(boundX, boundY, boundX + width, boundY + height);
        this.setBackground(Color.WHITE);

        setMouseListener();
        setMouseMotionListener();

		fsa = new FsaImpl();
		fsa.addListener(this);
		states= new HashSet<State>();
		transitions = new HashSet<Transition>();
		selectedStates = new HashSet<StateIcon>();

	}

	@Override
	public void paintComponent(Graphics gg){
		super.paintComponent(gg);

		Graphics2D g = (Graphics2D)gg;
		if(selectionBoxState.equals("DRAGGING")){
			g.setColor(Color.LIGHT_GRAY);

			Rectangle r = calcRectangle(x0, y0, x1, y1);
			g.fillRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
		}

        if(helperLine1 != null){
            g.setStroke(new BasicStroke(3));
            g.drawLine((int)helperLine1.getX(), (int)helperLine1.getY(), (int)helperLine2.getX(), (int)helperLine2.getY());
            g.setStroke(new BasicStroke(1));

        }
	}

    //Called whenever the number of states in the FSA has changed
    public void statesChanged(){
    	//check for added state
    	for(Iterator<State> it = fsa.getStates().iterator(); it.hasNext(); ){
    		State s = it.next();
    		if(!states.contains(s)){
    			states.add(s);
    			StateImpl sl = (StateImpl)s;
                //iterate over state's listeners. Change this later cos there is only ever 1 listener
	    		for(Iterator<StateIcon> itt = sl.getListeners().iterator(); itt.hasNext(); ){
	    				StateIcon si = itt.next();
	    				this.add(si);
	    		}
	    	}
    	}
        Set<State> removeStates = new HashSet<State>();
    	//check for removed state
    	for(Iterator<State> it = states.iterator(); it.hasNext(); ){
    		State s = it.next();
    		if(!fsa.getStates().contains(s)){
    			removeStates.add(s);
    			StateImpl sl = (StateImpl)s;
                //iterate over state's listeners. Change this later cos there is only ever 1 listener
 	    		for(Iterator<StateIcon> itt = sl.getListeners().iterator(); itt.hasNext(); ){
	    				StateIcon si = itt.next();
	    				this.remove(si);
	    		}
	    	}   			
    	}

        states.remove(removeStates);
    	this.repaint();
    	//System.out.println("States Changed");
    }

    //Called whenever the number of transitions in the FSA has changed
    public void transitionsChanged(){

    	//check for added transitions
    	for(Iterator<Transition> it = fsa.getTransitions().iterator(); it.hasNext(); ){
    		Transition t = it.next();
    		if(!transitions.contains(t)){
    			transitions.add(t);
    			TransitionImpl tl = (TransitionImpl)t;
	    		for(Iterator<TransitionIcon> itt = tl.getListeners().iterator(); itt.hasNext(); ){
	    				TransitionIcon ti = itt.next();
	    				this.add(ti);
	    		}
	    	}
    	}

    	//check for removed transitions
        Set<Transition> removeTrans = new HashSet<Transition>();
    	for(Iterator<Transition> it = transitions.iterator(); it.hasNext(); ){
    		Transition t = it.next();
    		if(!fsa.getTransitions().contains(t)){
    			removeTrans.add(t);
    			TransitionImpl tl = (TransitionImpl)t;
 	    		for(Iterator<TransitionIcon> itt = tl.getListeners().iterator(); itt.hasNext(); ){
	    				TransitionIcon ti = itt.next();
	    				this.remove(ti);
	    		}
	    	}   			
    	}
        transitions.remove(removeTrans);
    	this.repaint();
    	//System.out.println("Transitions Changed  " + transitions.size());
    }

    //Called whenever something about the FSA has changed
    //(other than states or transitions)
    public void otherChanged(){
        for(Iterator<State> it = fsa.getStates().iterator(); it.hasNext(); ){
            State s = it.next();
            for(Iterator<StateIcon> itt = ((StateImpl)s).getListeners().iterator(); itt.hasNext();){
                StateIcon si = itt.next();
                si.StateHasChanged();
            }
        }
    	//System.out.println("Other Change");
    }

    public FsaImpl getFsa(){
    	return fsa;
    }

    public void resetFsa(){
    	fsa = new FsaImpl();
    }

    public void setMouseListener(){
    	FsaPanel fsaPanel = this;
    	mouseListener = new MouseListener(){
    		@Override
    		public void mouseEntered(MouseEvent e){
    		}
    		@Override
    		public void mouseExited(MouseEvent e){
                if(!selectionBoxState.equals("PLACINGTRANS")){
                    selectionBoxState = "IDLE";
                }
    		}
    		@Override
    		public void mousePressed(MouseEvent e){
    			boolean circleClicked = false;
    			StateIcon clicked = null;
				for(Iterator<State> it = states.iterator(); it.hasNext(); ){
    				StateImpl sl = (StateImpl)it.next();
    				for(Iterator<StateIcon> itt = sl.getListeners().iterator(); itt.hasNext(); ){
	    				StateIcon si = itt.next();
	    				if(si.insideCircle(e.getX(), e.getY())){
	    					circleClicked = true;
	    					clicked = si;
	    				}
	    			}
    			}
    			if(!circleClicked && selectionBoxState.equals("IDLE")){
    				clearSelected();
    				x0 = e.getX();
    				y0 = e.getY();
    				selectionBoxState = "SELECTING";
    			}else if(circleClicked && selectionBoxState.equals("IDLE")){
    				x0 = e.getX();
    				y0 = e.getY();
    				if(clicked.isSelected()){
    					selectionBoxState = "MOVE";
    				}else{
    					selectionBoxState = "MOVE";
	    				clearSelected();
	    				addSelected(clicked);
	    			}
    			}else if(circleClicked && selectionBoxState.equals("PLACINGTRANS")){
                    if(first == null){
                        first = clicked.getState();
                        clearSelected();
                        helperLine1 = new Point(((StateImpl)first).getX()+(StateIcon.size/2), ((StateImpl)first).getY()+(StateIcon.size/2));
                        helperLine2 = new Point(e.getX(), e.getY());
                        selfPointer.repaint();
                    }else{
                        second = clicked.getState();

                        try{
                            fsa.newTransition(first, second, newTrans);
                            first = null;
                            second = null;
                            helperLine1 = null;
                            helperLine2 = null;
                            selfPointer.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            selectionBoxState = "IDLE";
                            parentPointer.repaintIsRecognised();
                        }catch(IllegalArgumentException ex){
                            first = null;
                            second = null;
                            helperLine1 = null;
                            helperLine2 = null;
                            selfPointer.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            selectionBoxState = "IDLE";
                            selfPointer.repaint();
                            JOptionPane.showMessageDialog(selfPointer, parentPointer.clipErrorMsg(ex.toString()), "File Format Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                else{
    				selectionBoxState = "IDLE";
                    selfPointer.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    first = null;
                    second = null;

    			}
    			//System.out.println("mouse pressed");
    		}
    		@Override
    		public void mouseClicked(MouseEvent e){
                if(selectionBoxState.equals("PLACING")){
                    selectionBoxState = "IDLE";
                }
    		}
    		@Override
    		public void mouseReleased(MouseEvent e){
                if(!selectionBoxState.equals("PLACINGTRANS")){
        			selectionBoxState = "IDLE";
        			fsaPanel.repaint();
    			    //System.out.println("mouse released");
                }
    		}
    	};

    	this.addMouseListener(mouseListener);
    }

    public void setMouseMotionListener(){
    	FsaPanel fsaPanel = this;
    	mouseMotionListener = new MouseMotionListener(){
    		@Override
    		public void mouseDragged(MouseEvent e){
    			if(selectionBoxState.equals("SELECTING")){
    				selectionBoxState = "DRAGGING";
    			}

    			if(selectionBoxState.equals("DRAGGING")){
    				x1 = e.getX();
    				y1 = e.getY();
    				//System.out.println(x0 + " " + y0 + " -- " + x1 + " " + y1);    				
    				fsaPanel.repaint();

    				Rectangle r = calcRectangle(x0, y0, x1, y1);
    				for(Iterator<State> it = states.iterator(); it.hasNext(); ){
    					StateImpl sl = (StateImpl)it.next();
    					for(Iterator<StateIcon> itt = sl.getListeners().iterator(); itt.hasNext(); ){
	    					StateIcon si = itt.next();
	    					if(r.intersects(si.getRect())){
	    						addSelected(si);
	    					}else{
	    						removeSelected(si);
	    					}
	    					si.repaint();
	    				}
    				}
    			}

                if(selectionBoxState.equals("PLACINGTRANS")){
                    if(helperLine1 != null){
                        helperLine2 = new Point(e.getX(), e.getY());
                        selfPointer.repaint();
                    }
                }

    			if(selectionBoxState.equals("MOVE")){
    				for(Iterator<StateIcon> it = selectedStates.iterator(); it.hasNext(); ){
    					StateIcon si = it.next();
    					si.getState().moveBy(e.getX() - x0, e.getY() - y0);
    					si.repaint();
    				}
    				x0 = e.getX();
    				y0 = e.getY();
    			}
    			//System.out.println("mouse dragged");
    		}

    		@Override
    		public void mouseMoved(MouseEvent e){
                if(selectionBoxState.equals("PLACING")){
                    recentState.moveBy((int)MouseInfo.getPointerInfo().getLocation().getX() - x0, (int)MouseInfo.getPointerInfo().getLocation().getY() - y0);
                    for(Iterator<StateIcon> it = ((StateImpl)recentState).getListeners().iterator(); it.hasNext(); ){
                        StateIcon si = it.next();
                        si.repaint();
                    }
                }

                if(selectionBoxState.equals("PLACINGTRANS")){
                    if(helperLine1 != null){
                        helperLine2 = new Point(e.getX(), e.getY());
                        selfPointer.repaint();
                    }
                }
                x0 = (int)MouseInfo.getPointerInfo().getLocation().getX();
                y0 = (int)MouseInfo.getPointerInfo().getLocation().getY();
    		}
    	};

    	this.addMouseMotionListener(mouseMotionListener);
    }

    public void setSelectedInitial(){
    	for(Iterator<StateIcon> it = selectedStates.iterator();  it.hasNext(); ){
    		StateIcon si = it.next();
    		si.getState().setInitial(true);
    		si.repaint();
    	}

    	clearSelected();
    }

    public void setSelectedFinal(){
    	for(Iterator<StateIcon> it = selectedStates.iterator();  it.hasNext(); ){
    		StateIcon si = it.next();
    		si.getState().setFinal(true);
    		si.repaint();
    	}

    	clearSelected();
    }

   	public void unsetSelectedInitial(){
    	for(Iterator<StateIcon> it = selectedStates.iterator();  it.hasNext(); ){
    		StateIcon si = it.next();
    		si.getState().setInitial(false);
    		si.repaint();
    	}

    	clearSelected();
    }

    public void unsetSelectedFinal(){
    	for(Iterator<StateIcon> it = selectedStates.iterator();  it.hasNext(); ){
    		StateIcon si = it.next();
    		si.getState().setFinal(false);
    		si.repaint();
    	}

    	clearSelected();
    }

    public void clearSelected(){
    	for(Iterator<StateIcon> it = selectedStates.iterator(); it.hasNext(); ){
    		StateIcon si = it.next();
    		si.setSelected(false);
    		si.repaint();
    	}
    	selectedStates.clear();  	
    }

    public void deleteSelected(){
        for(Iterator<StateIcon> it = selectedStates.iterator();  it.hasNext(); ){
            StateIcon si = it.next();
            fsa.removeState(si.getState());
        }
        this.repaint();
        clearSelected(); 
    }

    public void addSelected(StateIcon si){
    	if(!selectedStates.contains(si)){
	    	selectedStates.add(si);
	    	si.setSelected(true);
    	}
    }

  	public void removeSelected(StateIcon si){
    	selectedStates.remove(si);
    	si.setSelected(false);
    }

    public Rectangle calcRectangle(int x, int y, int xx, int yy){
    		int width = Math.abs(x - xx);
			int height = Math.abs(y - yy);

			int bx,by;

			if(x < xx){
				bx = x;
			}else{
				bx = xx;
			}

			if(y < yy){
				by = y;
			}else{
				by = yy;
			}

			return new Rectangle(bx, by, width, height);
    }

    public void addState (String name, Point p) throws IllegalArgumentException{
        Point p4 = new Point((int)(p.getX() - boundX), (int)(p.getY() - boundY));
        x0 = (int)MouseInfo.getPointerInfo().getLocation().getX();
        y0 = (int)MouseInfo.getPointerInfo().getLocation().getY();
        int px = (int)p4.getX() - (StateIcon.size/2);
        int py = ((int)p4.getY()-45 -(StateIcon.size/2));
        try{
            recentState = fsa.newState(name, px, py);
            selectionBoxState = "PLACING";
        }catch(IllegalArgumentException e){
            throw e;
        }
    }

    public void addTransition(String name) throws IllegalArgumentException{

        if(name != null){
            if(!(name.length() == 1 && name.charAt(0) == '?')){
                for(int i = 0; i < name.length(); i++){
                    if(!Character.isLetter(name.charAt(i))){
                        throw new IllegalArgumentException("Event name must be only letters or '?' ");
                    }
                }
            }
        }

        newTrans = name;
        selectionBoxState = "PLACINGTRANS";
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    public int getX(){
    	return boundX;
    }

    public int getY(){
    	return boundY;
    }

    public int getWidth(){
    	return width;
    }

    public int height(){
    	return height;
    }
}