package bspTest;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.zushen.game.MoreMath;


public class BSPTree {
	private BSPNode m_root;
	
	public void add(BSPLine line){
		if(this.m_root == null){
			this.m_root = new BSPNode(line); 
		}
		else{
			this.m_root.add(line);
		}
	}
	
	public ArrayList createList(){
		ArrayList list = new ArrayList();
		this.traverseTreeToGetList(this.m_root, list);
		return list;
	}
	
	private void traverseTreeToGetList(BSPNode node, ArrayList list){
		if(node == null){
			return;
		}
		
		this.traverseTreeToGetList(node.getBackNode(), list);
		list.add(node.getData());
		this.traverseTreeToGetList(node.getFrontNode(), list);
	}
	
	public void clear(){
		this.m_root = null;
	}
	
	public ArrayList locatePosition(Point2D p){
		ArrayList list = new ArrayList();
		this.traverseTreeToLocatePosition(this.m_root, list, p);
		return list;
	}
	
	private void traverseTreeToLocatePosition(BSPNode node, ArrayList stringList, Point2D p){
		if(node == null){
			return;
		}
		
		BSPLine line = node.getData();
		String message = "";
		message = line.getID() + "'s ";
		int side = line.getSide(p.getX(), p.getY());
		
		if(side == BSPLine.SIDE_FRONT){
			message += "front; ";
			stringList.add(message);
			this.traverseTreeToLocatePosition(node.getFrontNode(), stringList, p);
		}
		else if(side == BSPLine.SIDE_BACK){
			message += "back; ";
			stringList.add(message);
			this.traverseTreeToLocatePosition(node.getBackNode(), stringList, p);
		}
		else{
			//collinear! stop parsing!
		}
	}
	
	public class BSPNode{
		private BSPNode m_front;
		private BSPNode m_back;
		private BSPLine m_thisLine;
		
		public BSPNode(BSPLine line){
			this.m_thisLine = line;
		}
		
		public void add(BSPLine line){
			if(line != null){
				Point2D ip = MoreMath.getIntersectionPoint(this.m_thisLine, line);
				int side = this.m_thisLine.getSide(line);
				
				if(side == BSPLine.SIDE_FRONT){
					this.addFront(line);
				}
				else if(side == BSPLine.SIDE_BACK){
					this.addBack(line);
				}
				else if(ip != null && side == BSPLine.SIDE_SPANNING){
					BSPLine segment1 = new BSPLine(line.getP1(), ip);
					BSPLine segment2 = new BSPLine(ip, line.getP2());
					
					segment1.setLineID(line.getLineID());
					segment2.setLineID(line.getLineID());
					
					int side1 = this.m_thisLine.getSide(segment1);
					
					if(side1 == BSPLine.SIDE_FRONT){
						this.addFront(segment1);
						this.addBack(segment2);
					}
					else {
						this.addFront(segment2);
						this.addBack(segment1);
					}
				}
				else if(side == BSPLine.SIDE_COLLINEAR){
					//pass down to see if there is another node containing the line.
					if(this.m_front != null){
						this.m_front.add(line);
					}
					else if(this.m_back != null){
						this.m_back.add(line);
					}
					else{
						//see as the same line
					}
				}
			}
		}
		
		private void addFront(BSPLine line){
			if(this.m_front == null){
				line.setSegmentID(this.m_thisLine.getSegmentID() + "f");
				this.m_front = new BSPNode(line);
			}
			else{
				this.m_front.add(line);
			}
		}
		
		private void addBack(BSPLine line){
			if(this.m_back == null){
				line.setSegmentID(this.m_thisLine.getSegmentID() + "b");
				this.m_back = new BSPNode(line);
			}
			else{
				this.m_back.add(line);
			}
		}
		
		public BSPNode getFrontNode(){
			return this.m_front;
		}
		
		public BSPNode getBackNode(){
			return this.m_back;
		}
		
		public BSPLine getData(){
			return this.m_thisLine;
		}
	}
	
}
