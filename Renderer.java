import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class Renderer extends GUI{

	private Set<Polygon> polygons = new HashSet<Polygon>();
	private Vector3D lightSource;
	private int[] ambientLight = getAmbientLight();
	private int height = GUI.CANVAS_HEIGHT;

	private Shape shape;

	@Override
	protected void onLoad(File file) {
		try{
			BufferedReader buff = new BufferedReader(new FileReader(file));
			String line = buff.readLine();
			//System.out.println(line)//the light source;
			String[] data = line.split(" ");
			float [] lightSF = new float [3];
			for (int i=0;i<data.length;i++){
				lightSF[i] = Float.parseFloat(data[i]);
			}
			float lightX = lightSF[0];
			float lightY = lightSF[1];
			float lightZ = lightSF[2];
			lightSource = new Vector3D(lightX, lightY, lightZ);
			//for(int i=0;i<lightSF.length;i++){System.out.println(lightSF[i]);}
			while ((line=buff.readLine()) != null){
				data = line.split(" ");
				Vector3D v1 = new Vector3D(Float.parseFloat(data[0]),Float.parseFloat(data[1]),Float.parseFloat(data[2]));
				//System.out.println("c1: "+c1.toString());
				Vector3D v2 = new Vector3D(Float.parseFloat(data[3]),Float.parseFloat(data[4]),Float.parseFloat(data[5]));
				//System.out.println("c2: "+c2.toString());
				Vector3D v3 = new Vector3D(Float.parseFloat(data[6]),Float.parseFloat(data[7]),Float.parseFloat(data[8]));
				//System.out.println("c3: "+c3.toString());
				int r = Integer.parseInt(data[9]);
				int g = Integer.parseInt(data[10]);
				int b = Integer.parseInt(data[11]);
				//System.out.println("r: "+r+" g: "+g+" b: "+b);
				Polygon poly = new Polygon(v1, v2, v3, r, g, b);
				//System.out.println(poly.toString());
				polygons.add(poly);
			}
			buff.close();
		}
		catch (IOException e){
			System.out.println("Error: "+e);			
		}
	}


	@Override
	protected void onKeyPress(KeyEvent ev) {
	
	}

	@Override
	protected BufferedImage render() {
		if(!polygons.isEmpty()){
			//System.out.println("!isEmpty()");
			shape = new Shape(polygons,lightSource,ambientLight,height);
			Color[][] img = shape.zBuffer();
			//System.out.println("RETURN");
			return convertBitmapToImage(img); 
		}
		return null;
	}


	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new Renderer();
	}

}
