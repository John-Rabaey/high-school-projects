import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
/**
 * This is a peg placed by the user. It can be any color; its diameter should be specified in the constructor.
 * 
 * @author John Rabaey
 * @version 4/18/2017
 */
public class Peg
{
    //Color constants make things simpler...
    public static final int RED = 0;
    public static final int WHITE = 1;
    public static final int BOARD_COLOR = 2;
    public static final int DARKBLUE = 3;
    public static final int PINK = 4;
    public static final int GREEN = 5;
    public static final int YELLOW = 6;
    public static final int ORANGE = 7;
    public static final int PURPLE = 8;
    public static final int BLACK = 9;
    
    public static final Color[] COLORS = new Color[]{Color.RED, Color.WHITE, new Color(100, 100, 255), new Color(54, 56, 143), 
                                                     new Color(250,50,180), new Color(40, 120, 50),
                                                     new Color(250, 220, 0), new Color(250, 130, 40), 
                                                     new Color(110, 30, 150), Color.BLACK};
    public static final int RESERVED = 4;                      
    //Instance variables
    private int color;
    private double diameter;
    private double leftX, topY;
    /**
     * Constructs a peg with a color and a diameter.
     * @param color the color of the peg--a static constant from this class
     * @param diameter the diameter of peg
     * @param leftX the x-coordinate of the left side of the peg
     * @param topY the y-coordinate of the top of the peg
     */
    public Peg(int color, double diameter, double leftX, double topY)
    {
        this.color = color;
        this.diameter = diameter;
        this.leftX = leftX;
        this.topY = topY;
    }

    /**
     * Draws the peg
     * @param Graphics2D the graphics context
     */
    public void draw(Graphics2D g2)
    {
        g2.setColor(COLORS[color]);
        g2.fill(getBounds());
    }
    
    /**
     * Returns the integer representation of the peg's color
     */
    public int getColor()
    {
        return color;
    }
    
    /**
     * Moves a peg
     * @param xCenter the x-coordinate of the center of the peg
     * @param yCenter the y-coordinate of the center of the peg
     */
    public void moveTo(double xCenter, double yCenter)
    {
        leftX = xCenter - diameter / 2;
        topY = yCenter - diameter / 2;
    }
    
    /**
     * @return the x-coordinate of the center of the peg
     */
    public double getX()
    {
        return leftX + diameter / 2;
    }
    
    /**
     * @return the ellipse bounding the peg
     */
    public Ellipse2D.Double getBounds()
    {
        return new Ellipse2D.Double(leftX, topY, diameter, diameter);
    }
}
