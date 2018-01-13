import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.Rectangle;
import java.awt.Color;
import java.lang.Math.*;

class TransitionIcon extends JComponent implements TransitionListener{

	private Transition transition;
	private int count;

	public TransitionIcon(Transition t){
		transition = t;
		this.setBounds(0, 5, 1000, 505);
	}

	public TransitionIcon(Transition t, int c){
		transition = t;
		this.setBounds(0, 5, 1000, 505);
		count = c;
	}

	@Override
	public void paintComponent(Graphics gg){
		Graphics2D g = (Graphics2D)gg;

		StateImpl to = (StateImpl)transition.toState();
		StateImpl from = (StateImpl)transition.fromState();

		boolean toIsLeftMost = false;
		boolean toIsAbove = false;

		double tX = (double)to.getX();
		double tY = (double)to.getY();
		tX += ((double)to.getSize())/2;
		tY += ((double)to.getSize())/2;

		double fX = (double)from.getX();
		double fY = (double)from.getY();
		fX += ((double)from.getSize())/2;
		fY += ((double)from.getSize())/2;

		if(fX > tX){
			toIsLeftMost = true;
		}

		double mX = (tX+fX)/2;
		double mY = (tY+fY)/2;

		//large triangle sides
		double s1 = 100;
		double s2 = Math.sqrt((mX-tX)*(mX-tX) + (mY-tY)*(mY-tY));
		double s3 = Math.sqrt((s1*s1) + (s2*s2));

		//small triangle
		double ss1 = s1*(50/s3);
		double ss2 = s2*(50/s3);
		double ss3 = 50;

		//intersection points
		double tbX,tbY,fbX,fbY,xc,yc;

		if(toIsLeftMost){
			tbX = tX + ss2;
			fbX = fX - ss2+2;
			xc = tX + s2-2 + (10 * count);

			if(toIsAbove){
				tbY = tY + ss1-2;
				fbY = fY - ss1-2;
				yc = tY + s1-2 + (30 * count);
			}else{
				tbY = tY - ss1-2;
				fbY = fY + ss1-2;
				yc = tY - s1-2 + (30 * count);
			}
		}else{
			tbX = tX - ss2;
			fbX = fX + ss2-2;
			xc = tX - s2-2 + (30 * count);
			if(toIsAbove){
				tbY = tY + ss1-2;
				fbY = fY - ss1-2;
				yc = tY + s1-2 + (30 * count);
			}else{
				tbY = tY - ss1-2;
				fbY = fY + ss1-2;
				yc = tY - s1-2 + (30 * count);
			}
		}
		
		if(to.getName().equals(from.getName())){
			tbX = to.getX() + (to.getSize()/2);
			tbY = to.getY() - 4;

			fbX = from.getX() + from.getSize();
			fbY = from.getY() + (from.getSize()/2);

			xc = to.getX() + ((0.75)*to.getSize()) + (30 * count);
			yc = to.getY() - ((1.35)*to.getSize()) + (30 * count);
		}

		QuadCurve2D.Double curve = new QuadCurve2D.Double(tbX,tbY,xc,yc,fbX,fbY);
		g.draw(curve);

		//Draw arrow
		int x1 = (int)fbX;
		int y1 = (int)fbY;
		int x2 = x1 - 15;
		int y2 = y1 + 5;
		int x3 = x1 - 15;
		int y3 = y1 - 5;

		double xOC = tbX - tX;
		double yOC = tbY - tY;

		double xc2 = tbX + (0.2 * xOC);
		double yc2 = tbY + (0.2 * yOC);

		double perpX = yOC;
		double perpY = xOC * -1;

		int ax = (int)(xc2+(0.15)*perpX);
		int ay = (int)(yc2+(0.15)*perpY);
		int bx = (int)(xc2-(0.15)*perpX);
		int by = (int)(yc2-(0.15)*perpY);

		g.fillPolygon(new int[]{(int)tbX, ax, bx}, new int[]{(int)tbY, ay, by}, 3);
		//g.drawLine((int)tX, (int)tY, (int)fX, (int)fY);

		QuadCurve2D.Double left = new QuadCurve2D.Double();
		QuadCurve2D.Double right = new QuadCurve2D.Double();
		QuadCurve2D.subdivide(curve, left, right);
		Point2D midpoint = left.getP2();
		String s = "";
		if(transition.eventName() == null){
			s = "?";
		}else{
			s = transition.eventName();
		}
		g.drawString(s, (int)midpoint.getX()+5, (int)midpoint.getY()-5);
	}

	public void TransitionHasChanged(){
		this.repaint();
	}
}