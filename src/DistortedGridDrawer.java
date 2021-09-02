import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DistortedGridDrawer {
	
	private static final Color backgroundColor = new Color(255, 255, 255);
	private static final Color lineColor = new Color(0,0,0);
	private static final int WIDTHPERPIXEL = 20;
	private static final int MARGINWIDTH = 20;
	private static final boolean DISTORTION = true;
	private static boolean VARYLINETHICKNESS = true;
	private static boolean VARYLINECOLOR = false;
	
	//small number = more distortion, very sensitive, maybe change to double at some point
	private static final int DISTORTIONSTRENGTH = 1;
	
	private static final int LINETHICKNESSDISTORTIONSTRENGTH = 2;
	
	private static BufferedImage input;
	private static int WIDTH;
	private static int HEIGHT;
	
	private static Point[][] grid;
	
    public static void main(String[] args) throws IOException {
    	
    	//input
    	if (args.length > 0) {
    		VARYLINETHICKNESS = false;
    		VARYLINECOLOR = false;
    		for (int i = 0; i < args.length; i++) {
    			System.out.println(args[i]);
    			if (args[i].equals("-thick")) {
    				VARYLINETHICKNESS = true;
    			}
    			if (args[i].equals("-color")) {
    				VARYLINECOLOR = true;
    			}
    		}
    	}

    	input = ImageIO.read(new File("input.png"));
    	
    	WIDTH = (input.getWidth() * WIDTHPERPIXEL) + (2* MARGINWIDTH);
    	HEIGHT = (input.getHeight() * WIDTHPERPIXEL) + (2* MARGINWIDTH);
    	
        File create = new File("distortedDrawing.png");
        if (!create.exists()) {
        	create.createNewFile();
        }
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        
        initializeGrid();
        if (DISTORTION) {
        	distortGrid();
        }
        drawGrid(graphics);
        
        ImageIO.write(image, "png", create);
    }

	private static void drawGrid(Graphics2D g) {
		g.setColor(lineColor);
		//g.setStroke(new BasicStroke(3));
		for(int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (i != 0) {
					drawLine(grid[i][j], grid[i-1][j], g);
				}
				if (i != grid.length - 1) {
					drawLine(grid[i][j], grid[i+1][j], g);
				}
				if (j != 0) {
					drawLine(grid[i][j], grid[i][j-1], g);
				}
				if (j != grid[i].length - 1) {
					drawLine(grid[i][j], grid[i][j+1], g);
				}
			}
		}
		
		g.dispose();
	}
	
	private static void drawLine(Point point1, Point point2, Graphics2D g) {
		//temp value in-case VARYLINETHICKNESS not enabled
		int strokeWidth = (int)((double)WIDTHPERPIXEL/6.5);
		
		if (VARYLINETHICKNESS) {
			strokeWidth = (20/LINETHICKNESSDISTORTIONSTRENGTH)-((((point1.brightness + point2.brightness)/2)+5)/(LINETHICKNESSDISTORTIONSTRENGTH));
		}
		
		if (VARYLINECOLOR) {
			g.setColor(point1.color);
		}
		
		//bugfixing
		if (strokeWidth < 0) {
			System.out.println(LINETHICKNESSDISTORTIONSTRENGTH + " " + point1.brightness + " " + point2.brightness);
		}
		
		g.setStroke(new BasicStroke(strokeWidth));
		g.drawLine(point1.x, point1.y, point2.x, point2.y);
	}

	private static void distortGrid() {
		for(int i = 1; i < grid.length - 2; i++) {
			for (int j = 1; j < grid[i].length - 2; j++) {
				/*
		        int brightness = getPixelBrightness(i,j);
		        
		        //upper left corner
		        grid[i][j].x = grid[i][j].x - brightness;
		        grid[i][j].y = grid[i][j].y - brightness;
		        
		        //upper right corner
		        grid[i+1][j].x = grid[i+1][j].x + brightness;
		        grid[i+1][j].y = grid[i+1][j].y - brightness;
		        
		        //bottom left corner
		        grid[i][j+1].x = grid[i][j+1].x - brightness;
		        grid[i][j+1].y = grid[i][j+1].y + brightness;
		        
		        //bottom right corner
		        grid[i+1][j+1].x = grid[i+1][j+1].x + brightness;
		        grid[i+1][j+1].y = grid[i+1][j+1].y + brightness;
		        */
				
		        
		        //upper left corner
				int averageSurroundingBrightness = (getPixelBrightness(i-1,j)+getPixelBrightness(i-1,j-1)+getPixelBrightness(i,j-1))/3;
				int diffBrightness = getPixelBrightness(i,j) - averageSurroundingBrightness;
		        grid[i][j].x = grid[i][j].x - diffBrightness;
		        grid[i][j].y = grid[i][j].y - diffBrightness;
		        
		        //upper right corner
		        averageSurroundingBrightness = (getPixelBrightness(i,j-1)+getPixelBrightness(i+1,j-1)+getPixelBrightness(i+1,j))/3;
				diffBrightness = getPixelBrightness(i,j) - averageSurroundingBrightness;
		        grid[i+1][j].x = grid[i+1][j].x + diffBrightness;
		        grid[i+1][j].y = grid[i+1][j].y - diffBrightness;
		        
		        //bottom left corner
		        averageSurroundingBrightness = (getPixelBrightness(i-1,j)+getPixelBrightness(i-1,j+1)+getPixelBrightness(i,j+1))/3;
				diffBrightness = getPixelBrightness(i,j) - averageSurroundingBrightness;
		        grid[i][j+1].x = grid[i][j+1].x - diffBrightness;
		        grid[i][j+1].y = grid[i][j+1].y + diffBrightness;
		        
		        //bottom right corner
		        averageSurroundingBrightness = (getPixelBrightness(i+1,j)+getPixelBrightness(i+1,j+1)+getPixelBrightness(i,j+1))/3;
				diffBrightness = getPixelBrightness(i,j) - averageSurroundingBrightness;
		        grid[i+1][j+1].x = grid[i+1][j+1].x + diffBrightness;
		        grid[i+1][j+1].y = grid[i+1][j+1].y + diffBrightness;
		        
			}
		}
	}
	
	private static int getPixelBrightness(int i, int j) {
		if (i < 0 || j < 0 || i >= input.getWidth() || j >= input.getHeight()) {
			return 0;
		}
		
		int clr = input.getRGB(i, j);
        int red =   (clr & 0x00ff0000) >> 16;
        int green = (clr & 0x0000ff00) >> 8;
        int blue =   clr & 0x000000ff;
        
        //negative is dark, positive is bright
        int brightness = ((red + green + blue)/3)-127;
        brightness /= 255/(WIDTHPERPIXEL/DISTORTIONSTRENGTH);
        return brightness;
	}
	
	private static int getPixelColor(int i, int j) {
		if (i < 0 || j < 0 || i >= input.getWidth() || j >= input.getHeight()) {
			return 0;
		}
		
		return input.getRGB(i, j);
	}
	
	private static void initializeGrid() {
		grid = new Point[input.getWidth() + 1][input.getHeight() + 1];
		
		for(int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				//temp value in-case VARYLINETHICKNESS not enabled
				int brightness = 1;
				
				if (VARYLINETHICKNESS) {
					brightness = gridPointSurroundingBrightness(i,j);
				}
				
				Color color = Color.BLACK;
				if (VARYLINECOLOR) {
					color = gridPointSurroundingColor(i,j);
				}
				
				grid[i][j] = new Point((i*WIDTHPERPIXEL) + MARGINWIDTH, (j * WIDTHPERPIXEL) + MARGINWIDTH, brightness, color);
			}
		}
	}
	
	private static int gridPointSurroundingBrightness(int i, int j) {
		int upperLeft = getPixelBrightness(i-1,j-1);
		int upperRight = getPixelBrightness(i,j-1);
		int bottomRight = getPixelBrightness(i,j);
		int bottomLeft = getPixelBrightness(i-1,j);
		
		return (upperLeft+upperRight+bottomRight+bottomLeft)/4;
	}
	
	private static Color gridPointSurroundingColor(int i, int j) {
		int upperLeft = getPixelColor(i-1,j-1);
		int upperRight = getPixelColor(i,j-1);
		int bottomRight = getPixelColor(i,j);
		int bottomLeft = getPixelColor(i-1,j);
		
		int avgRed = (((upperLeft & 0x00ff0000) >> 16) + ((upperRight & 0x00ff0000) >> 16) + ((bottomRight & 0x00ff0000) >> 16) + ((bottomLeft & 0x00ff0000) >> 16))/4;
		int avgGreen = (((upperLeft & 0x0000ff00) >> 8) + ((upperRight & 0x0000ff00) >> 8) + ((bottomRight & 0x0000ff00) >> 8) + ((bottomLeft & 0x0000ff00) >> 8))/4;
		int avgBlue = ((upperLeft & 0x000000ff) + (upperRight & 0x000000ff) + (bottomRight & 0x000000ff) + (bottomLeft & 0x000000ff))/4;
		
		return new Color(avgRed, avgGreen, avgBlue);
	}

	private static class Point {
		public int x;
		public int y;
		public int brightness;
		public Color color;
		
		public Point(int x_, int y_, int b_, Color c_) {
			x = x_;
			y = y_;
			brightness = b_;
			color = c_;
		}
	}
	
}