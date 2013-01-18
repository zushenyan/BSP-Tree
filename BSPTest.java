package bspTest;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Iterator;

import com.zushen.game.GameAction;
import com.zushen.game.LazyDisplay;
import com.zushen.game.MoreMath;

public class BSPTest extends LazyDisplay{
	private static final int FONT_HEIGHT = 12;
	private static final int MESSAGE_FONT_HEIGHT = 20;
	private static final String MESSAGE[] = {
		"P - display intersection point",
		"R - reset",
		"Mouse Right Button - ",
		"locate the mouse's relative position",
		"program by Zushen-Yan 2009/8 ^_<"
		};
	
	
	private GameAction m_left = null;
	private GameAction m_right = null;
	private GameAction m_showDirectionButton = null;
	private GameAction m_showIPButton = null;
	private GameAction m_resetButton = null;
	
	private Point m_pressedPoint = null;
	private Point m_currentPoint = null;
	
	private BSPTree m_tree = null;

	private ArrayList m_lines = null;
	private ArrayList m_linesReadyToShow = null;
	
	private ArrayList m_messageList = null;
	
	private boolean m_displayDirection = false;
	private boolean m_displayIP = true;
	
	public BSPTest(String title, int width, int height, int sleepTime) {
		super(title, width, height, sleepTime);
		
		this.m_left = new GameAction("left", GameAction.BEHAVIOR_MOUSE_DRAGGING_HELPER);
		this.m_right = new GameAction("right");
		this.m_showDirectionButton = new GameAction("show direction", GameAction.BEHAVIOR_KEYBOARD_HOLDING_ON, true);
		this.m_showIPButton = new GameAction("show ip", GameAction.BEHAVIOR_KEYBOARD_HOLDING_ON, true);
		this.m_resetButton = new GameAction("reset", GameAction.BEHAVIOR_KEYBOARD_HOLDING_ON, true);
		
		this.m_inputManager.mapToMouse(this.m_left, MouseEvent.BUTTON1);
		this.m_inputManager.mapToMouse(this.m_right, MouseEvent.BUTTON3);
		this.m_inputManager.mapToKey(this.m_showDirectionButton, KeyEvent.VK_SPACE);
		this.m_inputManager.mapToKey(this.m_showIPButton, KeyEvent.VK_P);
		this.m_inputManager.mapToKey(this.m_resetButton, KeyEvent.VK_R);
		
		this.m_pressedPoint = new Point();
		this.m_currentPoint = new Point();
		
		this.m_lines = new ArrayList();
		this.m_messageList = new ArrayList();
		this.m_tree = new BSPTree();
	}

	@Override
	public void run() {
		try {
			this.showAndRun();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void update(Graphics2D g2) {
		this.detectInput();
		
		this.drawBackground(g2);
		this.drawLines(g2);
		this.drawIntersectionPoint(g2);
		this.drawConstructingLine(g2);
		this.drawDirections(g2);
	}
	
	private void detectInput(){
		
		if(this.m_left.isPressed()){
			this.m_pressedPoint.setLocation(
					this.m_inputManager.getMousePressedLocation().getX(), 
					this.m_inputManager.getMousePressedLocation().getY());
			this.m_currentPoint.setLocation(
					this.m_inputManager.getMouseCurrentLocation().getX(),
					this.m_inputManager.getMouseCurrentLocation().getY());
		}
		else if(this.m_left.isHolding()){
			//when the flow path continue updating quickly, our mouse's press state may be missed check,
			//and mouse's state will directly turn into holding state...
			//so the codes below is going to fix the problem.
			if(this.m_pressedPoint.equals(new Point2D.Double(0, 0))){
				this.m_pressedPoint.setLocation(this.m_currentPoint);
			}
			this.m_currentPoint.setLocation(
					this.m_inputManager.getMouseCurrentLocation().getX(),
					this.m_inputManager.getMouseCurrentLocation().getY());
		}
		else if(this.m_left.isJustReleased()){
			this.m_currentPoint.setLocation(
					this.m_inputManager.getMouseCurrentLocation().getX(),
					this.m_inputManager.getMouseCurrentLocation().getY());
			
			if(!this.m_pressedPoint.equals(this.m_currentPoint)){
				BSPLine line = new BSPLine(this.m_pressedPoint, this.m_currentPoint);
				line.setLineID(BSPLine.getJointLineID());
				
				this.m_lines.add(line);
				this.createTree();
				
				BSPLine.updateJointLineID();
				
				this.m_currentPoint.setLocation(0, 0);
				this.m_pressedPoint.setLocation(0, 0);
			}
		}
		
		if(this.m_right.isPressed()){
			this.m_messageList = this.m_tree.locatePosition(this.m_inputManager.getMousePressedLocation());
		}
		
		if(this.m_showDirectionButton.isPressed()){
			this.m_displayDirection = !this.m_displayDirection;
		}
		
		if(this.m_showIPButton.isPressed()){
			this.m_displayIP = !this.m_displayIP;
		}
		
		if(this.m_resetButton.isPressed()){
			this.reset();
		}
	}
	
	private void createTree(){
		this.m_tree.clear();
		
		for(int index = 0; index < this.m_lines.size(); index++){
			this.m_tree.add((BSPLine) this.m_lines.get(index));
		}
		
		this.m_linesReadyToShow = this.m_tree.createList();
	}
	
	private void reset(){
		this.m_lines.clear();
		this.m_tree.clear();
		this.m_messageList.clear();
		this.m_linesReadyToShow = null;
		BSPLine.resetJointLineID();
		
		System.gc();
	}
	
	private void drawBackground(Graphics2D g2){
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, this.m_frame.getWidth(), this.m_frame.getHeight());
	}
	
	private void drawConstructingLine(Graphics2D g2){
		g2.setColor(Color.GREEN);
		g2.drawLine(this.m_pressedPoint.x, this.m_pressedPoint.y, this.m_currentPoint.x, this.m_currentPoint.y);
	}
	
	private void drawLines(Graphics2D g2){
		if(this.m_linesReadyToShow == null){
			return;
		}
		
		Iterator it = this.m_linesReadyToShow.iterator();
		while(it.hasNext()){
			BSPLine l = (BSPLine) it.next();

			//draw normal
			g2.setColor(Color.RED);
			g2.draw(l.getVisualNormal());
			
			//draw line
			g2.setColor(Color.BLACK);
			g2.draw(l);
			
			//draw name
			g2.setColor(Color.DARK_GRAY);
			g2.drawString(l.getID(), (int)l.getMiddlePoint().getX() + 5, (int)l.getMiddlePoint().getY() + 5);
		}
	}
	
	private void drawIntersectionPoint(Graphics2D g2){
		if(!this.m_displayIP){
			return;
		}
		
		BSPLine line1;
		BSPLine line2;
		
		g2.setColor(Color.CYAN);
		
		for(int index = 0; index < this.m_lines.size(); index++){
			line1 = (BSPLine) this.m_lines.get(index);
			for(int index2 = index; index2 < this.m_lines.size(); index2++){
				line2 = (BSPLine) this.m_lines.get(index2);
				
				
				Point2D.Double ip = (Double) MoreMath.getIntersectionPoint(line1, line2);
				
				if(ip != null){
					g2.fillOval((int)ip.getX() - 3, (int)ip.getY() - 3, 6, 6);
					g2.drawString(String.valueOf(line1.getBounds2D().contains(ip) && line2.getBounds2D().contains(ip)),
							(int) ip.getX() - 3, (int) ip.getY() - 3);
				}
			}
		}
	}
	
	private void drawDirections(Graphics2D g2){
		g2.setColor(Color.BLUE);
		g2.drawString("press space for showing directions", 0, BSPTest.FONT_HEIGHT);
		g2.drawString("mouse is at:", 0, FONT_HEIGHT * 2);
		
		if(this.m_messageList != null){
			for(int index = 0; index < this.m_messageList.size(); index++){
				g2.drawString((String)this.m_messageList.get(index), 0, FONT_HEIGHT * (index + 3));
			}
		}
		
		if(!this.m_displayDirection){
			return;
		}
		
		g2.setColor(Color.WHITE);
		g2.fillRect(500 / 6, (int) (500 / 3.5) , 370, 500 / 4);
		
		g2.setColor(Color.BLACK);
		g2.drawRect(500 / 6, (int) (500 / 3.5) , 370, 500 / 4);
		
		g2.setFont(new Font("", Font.PLAIN, MESSAGE_FONT_HEIGHT));
		
		for(int index = 0; index < BSPTest.MESSAGE.length; index++){
			if(index == 3){
				g2.drawString(BSPTest.MESSAGE[index], (int) (500 / 4.5), 500 / 3 + MESSAGE_FONT_HEIGHT * index);
			}
			else{
				g2.drawString(BSPTest.MESSAGE[index], 500 / 6, 500 / 3 + MESSAGE_FONT_HEIGHT * index);
			}
		}
	}

	public static void main(String args[]){
		//don't be a system resource draining monster~ :P
		new BSPTest("BSP Test ver1.0", 500, 500, 3).run();
	}
}