package bspTest;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.zushen.game.MoreMath;


public class BSPLine extends Line2D.Float{
	public static final int SIDE_COLLINEAR = 0;
	public static final int SIDE_FRONT = 1;
	public static final int SIDE_BACK = 2;
	public static final int SIDE_SPANNING = 3;
	
	private static int JOINT_LINE_ID = 0;
	
	private int m_lineID = 0;
	private String m_segmentID = "";
	
	private double m_length;
	
	private Point2D m_normal;
	private Point2D m_middlePoint;
	
	private Line2D.Float m_visualNormal;
	
	public BSPLine(float x1, float y1, float x2, float y2){
		this(new Point2D.Double(x1, y1), new Point2D.Double(x2,y2));
	}
	
	public BSPLine(Point2D p1, Point2D p2){
		super(p1,p2);
		
		this.m_normal = MoreMath.getNormal(p1, p2);
		this.m_length = MoreMath.length(this.getP1(), this.getP2()); 
		this.m_normal = MoreMath.normalize(this.getNormal(), this.getLength());
		this.m_visualNormal = new Line2D.Float();
		this.buildVisualNormal();
	}
	
	public Point2D getNormal(){
		return this.m_normal;
	}
	
	public double getLength(){
		return this.m_length; 
	}
	
	public void buildVisualNormal(){
		double mx = ((this.getX2() - this.getX1()) / 2) + this.getX1();
		double my = ((this.getY2() - this.getY1()) / 2) + this.getY1();
		
		this.m_middlePoint = new Point2D.Double(mx, my);
		
		float nx = (int) (this.getNormal().getX() * 20 + mx);
		float ny = (int) (this.getNormal().getY() * 20 + my);
		
		this.m_visualNormal.setLine(this.m_middlePoint.getX(), this.m_middlePoint.getY(), nx, ny);
	}
	
	public Line2D.Float getVisualNormal(){
		return this.m_visualNormal;
	}
	
	public int getSide(double x, double y){
		double side = MoreMath.getDotProduct(
				(x - this.getX1()), 
				(y - this.getY1()), 
				this.m_normal.getX(), 
				this.m_normal.getY());
		return 
			(side < 0) ? BSPLine.SIDE_BACK : 
			(side > 0) ? BSPLine.SIDE_FRONT : BSPLine.SIDE_COLLINEAR;
	}
	
	public int getSideThick(double x, double y){
		
		int frontside = this.getSide(x - this.getNormal().getX() / 2, y - this.getNormal().getY());
		if(frontside == BSPLine.SIDE_FRONT){
			return BSPLine.SIDE_FRONT;
		}
		else if(frontside == BSPLine.SIDE_BACK){
			int backside = this.getSide(x + this.getNormal().getX() / 2, y + this.getNormal().getY());
			if(backside == BSPLine.SIDE_BACK){
				return BSPLine.SIDE_BACK;
			}
		}
		return BSPLine.SIDE_COLLINEAR;
	}
	
	public int getSide(BSPLine line){
		Point2D ip = MoreMath.getIntersectionPoint(this, line);
		
		int side1 = this.getSideThick(line.getX1(), line.getY1());
		int side2 = this.getSideThick(line.getX2(), line.getY2());
		
		/*
		 * the following two lines 
		 * (side1 == BSPLine.SIDE_COLLINEAR && side2 != BSPLine.SIDE_COLLINEAR) ? side2 :
		 * (side1 != BSPLine.SIDE_COLLINEAR && side2 == BSPLine.SIDE_COLLINEAR) ? side1 :
		 * are aimed to rule out the possibility of our line being like this :
		 * point1: collinear
		 * point2: front
		 */
		return 
			(side1 == BSPLine.SIDE_BACK && side2 == BSPLine.SIDE_BACK) ? BSPLine.SIDE_BACK :
			(side1 == BSPLine.SIDE_FRONT && side2 == BSPLine.SIDE_FRONT) ? BSPLine.SIDE_FRONT :
			(side1 == BSPLine.SIDE_COLLINEAR && side2 != BSPLine.SIDE_COLLINEAR) ? side2 :
			(side1 != BSPLine.SIDE_COLLINEAR && side2 == BSPLine.SIDE_COLLINEAR) ? side1 :
			(ip != null && side1 != side2) ? BSPLine.SIDE_SPANNING :
			BSPLine.SIDE_COLLINEAR;
	}
	
	public Point2D getMiddlePoint(){
		return this.m_middlePoint;
	}
	
	public static void updateJointLineID(){
		BSPLine.JOINT_LINE_ID++;
	}
	
	public static void resetJointLineID(){
		BSPLine.JOINT_LINE_ID = 0;
	}
	
	public static int getJointLineID(){
		return BSPLine.JOINT_LINE_ID;
	}
	
	public void setLineID(int id){
		this.m_lineID = id;
	}
	
	public int getLineID(){
		return this.m_lineID;
	}
	
	public void setSegmentID(String id){
		this.m_segmentID = id;
	}
	
	public void appendSegmentID(String parent){
		this.m_segmentID += parent;
	}

	public String getSegmentID(){
		return this.m_segmentID;
	}
	
	public String getID(){
		return String.valueOf(this.getLineID()) + "." + this.getSegmentID();
	}
}
