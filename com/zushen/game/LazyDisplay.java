package com.zushen.game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public abstract class LazyDisplay {
	protected JFrame m_frame = null;
	protected Canvas m_canvas = null;
	
	protected InputManager m_inputManager = null;
	
	private int m_sleepTime = 0;
	
	public LazyDisplay(String title, int width, int height, int sleepTime){
		this.m_canvas = new Canvas();
		this.m_canvas.setSize(width, height);
		this.m_canvas.setIgnoreRepaint(true);
		
		this.m_frame = new JFrame(title);
		this.m_frame.setIgnoreRepaint(true);
		this.m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.m_frame.getContentPane().add(this.m_canvas);
		this.m_frame.setResizable(false);
		this.m_frame.setSize(width, height);
		
		this.m_inputManager = new InputManager(this.m_canvas);
		this.setSleepTime(sleepTime);
	}
	
	protected void showAndRun() throws InterruptedException{
		
		this.show();
		BufferStrategy bf = this.m_canvas.getBufferStrategy();
		Graphics2D g2;
		
		while(true){
			g2 = (Graphics2D) bf.getDrawGraphics();
			
			this.update(g2);
			
			g2.dispose();
			if(!bf.contentsLost()){
				bf.show();
			}
			
			this.goRestingCPU();
		}
	}

	private void show(){
		if(!this.m_frame.isVisible()){
			this.m_frame.setVisible(true);	
			this.m_canvas.createBufferStrategy(2);
			this.m_canvas.requestFocus();
		}
	}
	
	protected void goRestingCPU() throws InterruptedException{
		if(this.getSleepTime() > 0){
			Thread.sleep(this.getSleepTime());
		}
	}
	
	public void setSleepTime(int time){
		this.m_sleepTime = time > 0 ? time : 0;
	}
	
	public int getSleepTime(){
		return this.m_sleepTime;
	}
	
	public abstract void run();
	protected abstract void update(Graphics2D g2);
}
