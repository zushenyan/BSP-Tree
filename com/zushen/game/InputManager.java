package com.zushen.game;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputManager implements KeyListener, MouseListener, MouseMotionListener{
	public static final int KEY_CODE_SUM = 600;
	public static final int MOUSE_CODE_SUM = 10;
	
	private GameAction m_keyActions[];
	private GameAction m_mouseAction[];
	
	private Point m_mousePressedLocation;
	private Point m_mouseReleasedLocation;
	private Point m_mouseCurrentLocation;
	
	public InputManager(Component comp){
		comp.addKeyListener(this);
		comp.addMouseListener(this);
		comp.addMouseMotionListener(this);
		
		this.m_keyActions = new GameAction[InputManager.KEY_CODE_SUM];
		this.m_mouseAction = new GameAction[InputManager.MOUSE_CODE_SUM];
		
		this.m_mousePressedLocation = new Point();
		this.m_mouseReleasedLocation = new Point();
		this.m_mouseCurrentLocation = new Point();
	}
	
	public void mapToKey(GameAction ga, int keycode){
		if(keycode < this.m_keyActions.length){
			this.m_keyActions[keycode] = ga;
		}
	}
	
	public void mapToMouse(GameAction ga, int mouseCode){
		if(mouseCode < this.m_mouseAction.length){
			this.m_mouseAction[mouseCode] = ga;
		}
	}
	
	public void clearKeyOrMouseMap(GameAction ga){
		for(int index = 0; index < this.m_keyActions.length; index++){
			if(this.m_keyActions[index] == ga){
				this.m_keyActions[index] = null;
			}
		}
		
		for(int index = 0; index < this.m_mouseAction.length; index++){
			if(this.m_mouseAction[index] == ga){
				this.m_mouseAction[index] = null;
			}
		}
	}

	public Point getMousePressedLocation(){
		return this.m_mousePressedLocation;
	}
	
	public Point getMouseReleasedLocation(){
		return this.m_mouseReleasedLocation;
	}
	
	public Point getMouseCurrentLocation(){
		return this.m_mouseCurrentLocation;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(this.m_keyActions[e.getKeyCode()] != null){
			this.m_keyActions[e.getKeyCode()].press();
		}
		//be sure that the key will not do other things.
		e.consume();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(this.m_keyActions[e.getKeyCode()] != null){
			this.m_keyActions[e.getKeyCode()].release();
		}
		//be sure that the key will not do other things.
		e.consume();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(this.m_mouseAction[e.getButton()] != null){
			this.m_mouseAction[e.getButton()].press();
			this.m_mousePressedLocation.setLocation(e.getX(), e.getY());
		}
		e.consume();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(this.m_mouseAction[e.getButton()] != null){
			this.m_mouseAction[e.getButton()].release();
			this.m_mouseReleasedLocation.setLocation(e.getX(), e.getY());
		}
		e.consume();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(e.getModifiers() == MouseEvent.BUTTON1_MASK && this.m_mouseAction[MouseEvent.BUTTON1] != null){
			this.m_mouseAction[MouseEvent.BUTTON1].hold();
			this.m_mouseCurrentLocation.setLocation(e.getX(), e.getY());
		}
		else if(e.getModifiers() == MouseEvent.BUTTON2_MASK && this.m_mouseAction[MouseEvent.BUTTON2] != null){
			this.m_mouseAction[MouseEvent.BUTTON2].hold();
			this.m_mouseCurrentLocation.setLocation(e.getX(), e.getY());
		}
		else if(e.getModifiers() == MouseEvent.BUTTON3_MASK && this.m_mouseAction[MouseEvent.BUTTON3] != null){
			this.m_mouseAction[MouseEvent.BUTTON3].hold();
			this.m_mouseCurrentLocation.setLocation(e.getX(), e.getY());
		}
		e.consume();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.m_mouseCurrentLocation.setLocation(e.getX(), e.getY());
	}
}
