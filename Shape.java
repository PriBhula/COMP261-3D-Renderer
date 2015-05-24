import java.awt.Color;
import java.util.HashSet;
import java.util.Set;


public class Shape {

	private Set<Polygon> polygons = new HashSet<Polygon>();
	private Set<Polygon> visibleP = new HashSet<Polygon>();
	private Vector3D lightSource;
	private int[] ambientLight;
	private int height;
	private Vector3D origin;
	private float minX,maxX,minY,maxY;

	public Shape(Set<Polygon> polygons,Vector3D lightS, int[] ambience, int height){
		this.polygons = polygons;
		this.lightSource = lightS;
		this.ambientLight = ambience;
		this.height = height;
		resetTransform();

	}

	public void calcVisibleP(){
		visibleP.clear();
		for (Polygon p: polygons){
			if(p.isVisible()){
				visibleP.add(p);
			}
		}
	}

	public Color[][] zBuffer(){
		//System.out.println("zBuff");
		calcVisibleP();
		Color[][] zBuffC = new Color[GUI.CANVAS_WIDTH][GUI.CANVAS_HEIGHT];
		float[][] zBuffD = new float[GUI.CANVAS_WIDTH][GUI.CANVAS_HEIGHT];

		for (int i=0;i<zBuffC.length;i++){
			for(int j=0;j<zBuffC[i].length;j++){
				zBuffC[i][j] = Color.BLACK;
				zBuffD[i][j] = Float.POSITIVE_INFINITY;
			}
		}
		//System.out.println("initialised");
		for (Polygon p: visibleP){
			float[][] edgelist = getEdgeList(p);
			Color shading = getShading(p);
			//System.out.println("edgelist: "+edgelist.toString()+" shading: "+shading.toString());

			for (int i=0;i<edgelist.length;i++){
				float x = (float) Math.ceil(edgelist[i][0]);
				float z = edgelist[i][0];

				float mZ = (edgelist[i][3])-(edgelist[i][1]) / (edgelist[i][2])-(edgelist[i][0]);

				while(x<Math.ceil(edgelist[i][2])){
					if (x >= 0 && x < height && z < zBuffD[(int) x][i]) {
						zBuffD[(int) (x)][i] = z;
						zBuffC[(int) Math.ceil(x)][i] = shading;
					}
					x++;
					z += mZ;
				}
			}

		}
		return zBuffC;
	}


	private float[][] getEdgeList(Polygon p){
		p.setBoundingBox();
		p.maxMin();
		float[][] edgelist = new float [(int) p.maxY][4];
		//System.out.println("p.maxY: "+p.maxY);

		for( int i = (int) p.minY; i<edgelist.length;i++){
			edgelist[i][0] = Float.POSITIVE_INFINITY;
			edgelist[i][1] = Float.POSITIVE_INFINITY;
			edgelist[i][2] = Float.NEGATIVE_INFINITY;
			edgelist[i][3] = Float.NEGATIVE_INFINITY;
		}

		for (int i=0;i<3;i++){
			Vector3D vA = p.vertices[i];//vA has smaller y value
			Vector3D vB = p.vertices[(i+1) % 3];
			if (vA.y > vB.y){
				Vector3D temp = vB;
				vB = vA;
				vA = temp;
			}
			//System.out.println("vA.x: "+vA.x+" vA.y: "+vA.y+" vB.x: "+vB.x+" vB.y: "+vB.y);

			float mX = (vB.x-vA.x)/(vB.y-vA.y);
			float mZ = (vB.z-vA.z)/(vB.y-vA.y);
			//System.out.println("mx: "+mX+" mz: "+mZ);
			float x = (float) Math.floor(vA.x);
			float z = (float) Math.floor(vA.z);
			int q =  (int) Math.floor(vA.y);
			int maxQ = (int) Math.floor(vB.y);
			//System.out.println("x: "+x+" z: "+z+" y: "+q+" maxY: "+maxQ);

			while(q<maxQ){
				//System.out.println("q: "+q);
				if (x < edgelist[q][0]){
					edgelist[q][0] = x;
					edgelist[q][1] = z;
				}
				if (x > edgelist[q][2]){
					edgelist[q][2] = x;
					edgelist[q][3] = x;
				}
				q++;
				x+=mX;
				z+=mZ;
			}
		}
		/*for (int i=0; i<edgelist.length;i++){
			for (int j=0;j<edgelist[i].length;j++){
				System.out.println("edgelist["+i+", "+j+"]: "+edgelist[i][j]);
			}
		}*/
		return edgelist;
	}

	private Color getShading(Polygon p) {
		//for(int i=0;i<ambientLight.length;i++){System.out.println(ambientLight[i]);}
		
		Vector3D normal = p.calcNormal();
		float cosTheta = normal.cosTheta(lightSource);
		//System.out.println("BEFORE BEFORE cT: "+cosTheta+" r: "+p.red+" g: "+p.green+" b: "+p.blue);
		float r = ((ambientLight[0]/255f) + 0.5f * cosTheta) * (p.red/255f);
		float g = ((ambientLight[1]/255f) +0.5f* cosTheta) * (p.green/255f);
		float b = ((ambientLight[2]/255f)+0.5f* cosTheta) * (p.blue/255f);
		
		//System.out.println("BEFORE cT: "+cosTheta+" r: "+r+" g: "+g+" b: "+b);
		
		if (r < 0) { r = 0; } else if (r > 1) { r = 1; }
		if (g < 0) { g = 0; } else if (g > 1) { g = 1; }
		if (b < 0) { b = 0; } else if (b > 1) { b = 1; }
		//System.out.println("AFTER cT: "+cosTheta+" r: "+r+" g: "+g+" b: "+b);
		return new Color(r,g,b);

	}

	private void resetTransform() {
		Transform transf = Transform.identity();
		float scale = 0.8f;
		transf.newScale(scale, scale, scale);
		for (Polygon p: polygons){
			p.vertices[0] = transf.multiply(p.vertices[0]);
			p.vertices[1] = transf.multiply(p.vertices[1]);
			p.vertices[2] = transf.multiply(p.vertices[2]);
		}
		origin = new Vector3D(height/5, height/3,0f);
		//System.out.println("origin: "+origin.toString());
		transf = Transform.newTranslation(origin.x - maxX/3, origin.y - maxY/3 , origin.z );
		for (Polygon p: polygons){
			p.vertices[0] = transf.multiply(p.vertices[0]);
			p.vertices[1] = transf.multiply(p.vertices[1]);
			p.vertices[2] = transf.multiply(p.vertices[2]);
		}
	}

}
