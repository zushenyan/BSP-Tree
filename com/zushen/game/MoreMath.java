package com.zushen.game;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class MoreMath {
	public static Point2D getNormal(Point2D start, Point2D end){
		double dx = end.getX() - start.getX();
		double dy = end.getY() - start.getY();
		
		return new Point2D.Double(dy, -dx);
	}
	
	public static double length(Point2D start, Point2D end){
		double dx = end.getX() - start.getX();
		double dy = end.getY() - start.getY();
		
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public static Point2D normalize(Point2D normal, double length){
		double x = normal.getX() / length;
		double y = normal.getY() / length;
		
		return new Point2D.Double(x,y);
	}
	
	public static Point2D getIntersectionPoint(Line2D l1, Line2D l2){
		double numerator =
			(l2.getX2() - l2.getX1()) * (l1.getY1() - l2.getY1()) - 
			(l2.getY2() - l2.getY1()) * (l1.getX1() - l2.getX1());
		double denominator = 
			(l2.getY2() - l2.getY1()) * (l1.getX2() - l1.getX1()) - 
			(l2.getX2() - l2.getX1()) * (l1.getY2() - l1.getY1());
		
		if(denominator == 0){
			return null;
		}
		
		double u = numerator / denominator;
		
		double x = l1.getX1() + u * (l1.getX2() - l1.getX1());
		double y = l1.getY1() + u * (l1.getY2() - l1.getY1());
		
		return new Point2D.Double(x, y);
	}
	
	public static double getDotProduct(double x1, double y1, double x2, double y2){
		return x1 * x2 + y1 * y2;
	}
}
