package com.zushen.game;

public class GameAction {
	//keyboard will not have holding state.
	public static final int BEHAVIOR_NORMAL = 0;
	
	//keyboard's press state will be replaced by holding state.
	//if you apply it to a mouse key, things may work weird. 
	public static final int BEHAVIOR_KEYBOARD_HOLDING_ON = 1;
	
	//help mouse to reset amount when isJustReleased() is called.
	public static final int BEHAVIOR_MOUSE_DRAGGING_HELPER = 2;
	
	public static final int STATE_RELEASED = 0;
	public static final int STATE_PRESSED = 1;
	public static final int STATE_HOLDING = 2;
	
	private String m_name = "";
	
	private boolean m_autoRecount = false;
	
	private int m_state = GameAction.STATE_RELEASED;
	private int m_behavior = GameAction.BEHAVIOR_NORMAL;
	private int m_amount = 0;
	
	public GameAction(String name){
		this(name, GameAction.BEHAVIOR_NORMAL);
	}
	
	public GameAction(String name, int behavior){
		this(name, behavior, false);
	}
	
	public GameAction(String name, boolean autoRecount){
		this(name, GameAction.BEHAVIOR_NORMAL, autoRecount);
	}
	
	public GameAction(String name, int behavior, boolean autoRecount){
		this.m_name = name;
		this.m_behavior = behavior;
		this.m_autoRecount = autoRecount;
		this.resetAmount();
	}
	
	public synchronized void press(){
		this.press(1);
	}
	
	public synchronized void press(int amount){
		if(!this.isHolding()){
			this.plusAmount(1);
			this.m_state = GameAction.STATE_PRESSED;
		}
	}
	
	public synchronized void hold(){
		this.m_state = GameAction.STATE_HOLDING;
	}
	
	public synchronized void release(){
		this.m_state = GameAction.STATE_RELEASED;
		if(this.isAutoRecount()){
			this.resetAmount();
		}
	}
	
	public synchronized boolean isPressed(){
		boolean result = this.m_state == GameAction.STATE_PRESSED ? true : false;
		if(this.m_behavior == GameAction.BEHAVIOR_KEYBOARD_HOLDING_ON && result == true){
			this.hold();
		}
		return result;
	}
	
	public synchronized boolean isHolding(){
		return this.m_state == GameAction.STATE_HOLDING ? true : false;
	}
	
	public synchronized boolean isReleased(){
		return this.m_state == GameAction.STATE_RELEASED ? true : false;
	}
	
	public synchronized boolean isJustReleased(){
		int amount = this.getAmount();
		if(this.m_behavior == GameAction.BEHAVIOR_MOUSE_DRAGGING_HELPER){
			this.resetAmount();
		}
		return (this.m_state == GameAction.STATE_RELEASED && amount != 0) ? true : false;
	}
	
	public synchronized void setAutoRecount(boolean value){
		this.m_autoRecount = value;
	}
	
	public synchronized boolean isAutoRecount(){
		return this.m_autoRecount;
	}
	
	public synchronized int getState(){
		return this.m_state;
	}

	public synchronized void setBehavior(int behavior){
		this.m_behavior = behavior;
	}
	
	public synchronized int getBehavior(){
		return this.m_behavior;
	}
	
	public String getName(){
		return this.m_name;
	}
	
	public synchronized int resetAmount(){
		int val = this.m_amount;
		this.m_amount = 0;
		return val;
	}
	
	public synchronized int getAmount(){
		return this.m_amount;
	}
	
	protected synchronized void plusAmount(int amount){
		this.m_amount += amount;
	}
}
