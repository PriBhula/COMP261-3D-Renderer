import java.awt.Color;
import java.awt.Rectangle;


public class Polygon{

	public Vector3D[] vertices = new Vector3D[3];
	public Vector3D normal;
	public int red,blue,green;
	private Color colour;
	
	public int minX,maxX,minY,maxY;
	
	public Rectangle bBox;

	public Polygon (Vector3D v1, Vector3D v2, Vector3D v3, int r, int g, int b){
		vertices[0] = v1;
		vertices[1] = v2;
		vertices[2] = v3;
		red = r;
		green = g;
		blue = b;
		colour = new Color(r,g,b);
		calcNormal();
	}

	public boolean isVisible() {
		if (normal.z >= 0){
			return false;
		}
		return true;
	}
	
	public Vector3D calcNormal(){
		Vector3D v1 = vertices[0];
		Vector3D v2 = vertices[1];
		Vector3D v3 = vertices[2];
		normal = v2.minus(v1).crossProduct(v3.minus(v2));
		return normal;
	}

	public void maxMin() {
		minX = Integer.MAX_VALUE;
		maxX = Integer.MIN_VALUE;
		minY = Integer.MAX_VALUE;
		maxY = Integer.MIN_VALUE;
		for (int i = 0; i < 3; i++) {
			Vector3D v = vertices[i];
			if (v.x < minX)
				minX = (int) v.x;
			if (v.x > maxX)
				maxX = (int) v.x;
			if (v.y < minY)
				minY = (int) v.y;
			if (v.y > maxY)
				maxY = (int) v.y;
		}
	}


	public Color getColor(Polygon p){
		return p.colour;
	}

	public void setBoundingBox() {
		float initMinX = Math.min(vertices[0].x, vertices[1].x);
		initMinX = Math.min(initMinX, vertices[2].x);
		minX = Math.round(initMinX);

		float initMaxX = Math.max(vertices[0].x, vertices[1].x);
		initMaxX = Math.max(initMaxX, vertices[2].x);
		maxX = Math.round(initMaxX);

		float initMinY = Math.min(vertices[0].y, vertices[1].y);
		initMinY = Math.min(initMinY, vertices[2].y);
		minY = Math.round(initMinY);

		float initMaxY = Math.max(vertices[0].y, vertices[1].y);
		initMaxY = Math.max(initMaxY, vertices[2].y);
		maxY = Math.round(initMaxY);

		//System.out.println("minX: "+minX+" minY: "+minY+" maxX:"+maxX+" maxY: "+maxY);
		bBox = new Rectangle(minX, minY, (maxX - minX), (maxY - minY));
	}

	public String toString(){
		return ("Polygon(v1: "+vertices[0].toString()+" v2: "+vertices[1].toString()+" v3: "+vertices[2].toString() + "colour: "+colour);
	}

}
