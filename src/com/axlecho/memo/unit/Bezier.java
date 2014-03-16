package com.axlecho.memo.unit;

import java.util.ArrayList;
import java.util.List;


public class Bezier {
	private Point _src;
	private Point _dst;
	private Point _c1;
	private Point _c2;

	public Bezier(Point src, Point dst, Point c1, Point c2) {
		_src = src;
		_dst = dst;
		_c1 = c1;
		_c2 = c2;
	}

	public List<Point> getPoints(float precision) {
		List<Point> bezier = new ArrayList<Point>();
		float d = 1.0f / precision;
		for (float t = 0.0f; t < 1.0f; t += d) {
			bezier.add(getPoint(t));
		}
		bezier.add(getPoint(1.0f));
		return bezier;
	}

	private Point getPoint(float t) {
		float x = (1 - t) * (1 - t) * (1 - t) * _src.x + 3 * t * (1 - t) * (1 - t) * _c1.x + 3 * t * t * (1 - t)
				* _c2.x + t * t * t * _dst.x;
		float y = (1 - t) * (1 - t) * (1 - t) * _src.y + 3 * t * (1 - t) * (1 - t) * _c1.y + 3 * t * t * (1 - t)
				* _c2.y + t * t * t * _dst.y;
		return new Point(x, y);
	}

}
