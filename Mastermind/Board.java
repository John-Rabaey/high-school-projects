import java.awt.Graphics2D;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
/**
 * The board class holds the tools for drawing the board. The size of the board will determine the size of the peg.
 * To add a peg to the board, use the .addPeg method and redraw the board.
 * For all methods... Guess starts at 1. Guess 0 is the answer key.
 * Position 0 holds computer-generated pegs.
 * 
 * @author John Rabaey
 * @version 4/18/2017
 */
public class Board
{
    public static final double BUFFER_RATIO = .4; //the outside edge of the board
    public static final double PEG_SIZE_RATIO = .8; //the size of a peg
    public static final double HOLE_SIZE_RATIO = .5; // size of hole compared to peg
    //instance variables
    private double xLeft, yTop;
    private double width, height;
    private int codePegs, guesses;
    private int rows, columns;
    private double rowWidth, columnWidth, gridTop, gridLeft;
    private int currentRow; //filledRows = number of rows filled with pegs
    private boolean codeHidden = true;
    private Peg[][] userPegs; //The two-dimensional array of pegs for the board
    private ArrayList<ComputerPegArray> computerPegs = new ArrayList<ComputerPegArray>();
    private double pegDiameter, holeDiameter;
    private double pegBufferX, pegBufferY, holeBufferX, holeBufferY; //space to add before drawing peg or hole
    /**
     * Constructs a board. Pass the constructor the (pixel) length and width of the board. Also tell it how many 
     * guesses the user should get and how many pegs should be in the code to break.
     * @param xLeft the x-coordinate of the left side of the board
     * @param yTop the y-coordinate of the top side of the board
     * @param length the width in pixels of the board
     * @param width the height in pixels of the board
     * @param guesses the number of guesses the user gets
     * @param codePegs the number of pegs per row
     */
    public Board(double xLeft, double yTop, double width, double height, int guesses, int codePegs)
    {
        reset(xLeft, yTop, width, height, guesses, codePegs);
    }
    
    /**
     * Resets the entire board, essentially a constructor.
     * @param xLeft the x-coordinate of the left side of the board
     * @param yTop the y-coordinate of the top side of the board
     * @param length the width in pixels of the board
     * @param width the height in pixels of the board
     * @param guesses the number of guesses the user gets
     * @param codePegs the number of pegs per row
     */
    public void reset(double xLeft, double yTop, double width, double height, int guesses, int codePegs)
    {
        
        this.xLeft = xLeft;
        this.yTop = yTop;
        this.height = height;
        this.width = width;         //Obvious, easy stuff
        this.codePegs = codePegs;
        this.guesses = guesses;
        this.rows = guesses + 1; //rows, including row for computer pegs
        this.columns = codePegs + 1; //cols, including col for computer pegs
        //calculate gridding system
        rowWidth = height / (rows + BUFFER_RATIO * 2); //calculate height of row
        columnWidth = width / (columns + BUFFER_RATIO * 2); //calculate width of column
        gridLeft = xLeft + columnWidth * BUFFER_RATIO; //grid calculations--simplified
        gridTop = yTop + rowWidth * BUFFER_RATIO; //same.
        //Set up peg array
        userPegs = new Peg[rows][codePegs]; //Hey, now we know how many rows we have.
        //get ready for drawing
        currentRow = 0; //Obviously...
        //these values assist in gridding pegs/holes in the right places
        pegDiameter = PEG_SIZE_RATIO * Math.min(rowWidth, columnWidth); //more complex, but straightforward.
        pegBufferX = (columnWidth - pegDiameter) / 2; //set up buffers
        pegBufferY = (rowWidth - pegDiameter) / 2;
        holeDiameter = HOLE_SIZE_RATIO * Math.min(rowWidth, columnWidth); //more complex, but straightforward.
        holeBufferX = (columnWidth - holeDiameter) / 2; //set up buffers
        holeBufferY = (rowWidth - holeDiameter) / 2;
        //Set up the empty game board--this is actually very straightforward.
        //(The 'pegs' are actually holes)
        for(int row = 0; row < userPegs.length; row++)
        {
            for(int col = 0; col < userPegs[row].length; col++)
            {
                userPegs[row][col] = new Peg(Peg.DARKBLUE, holeDiameter, 
                    gridLeft + holeBufferX + columnWidth * col, gridTop + holeBufferY + rowWidth * row);
            }
        }
    }
    
    /**
     * Removes the cover so that the code is visible, giving the user a sense of validation and self-worth.
     */
    public void uncoverCode()
    {
        codeHidden = false;
    }
    
    /**
     * Draws the board
     * @param g2 the graphics context
     */
    public void draw(Graphics2D g2)
    {
        //The game board
        RoundRectangle2D.Double board = new RoundRectangle2D.Double(xLeft,
            yTop, width, height, columnWidth * BUFFER_RATIO, rowWidth * BUFFER_RATIO);
        g2.setColor(Peg.COLORS[Peg.BOARD_COLOR]);
        g2.fill(board);
        //the pegs
        for(Peg[] row : userPegs)
        {
            for(Peg peg : row)
            {
                peg.draw(g2);
            }
        }
        //the computer pegs
        for(ComputerPegArray pegs : computerPegs)
        {
            pegs.draw(g2);
        }
        //If covered, draw a cover over the code.
        
        Rectangle2D.Double cover = new Rectangle2D.Double(gridLeft, 
            gridTop, columnWidth * codePegs, rowWidth);
        g2.setColor(Peg.COLORS[Peg.DARKBLUE]);
        if(codeHidden) g2.fill(cover); //draws the whole thing
        else g2.draw(cover); //draws the outline
    }
    
    /**
     * Adds a peg to the board at the specified position. Processes the position input and grids the peg
     * @param guess the number of the guess the user is on
     * @param color the color of the new peg (constant from the peg class)
     */
    public void addPegAt(double position, int color)
    {
         int column = (int) (position + .5); //round; now column = integer value of position
         column = (int)((column - gridLeft) / columnWidth); //now column = peg index
         if(column >= 0 && column < codePegs)
         {
            if(color == Peg.DARKBLUE)
            {
                userPegs[currentRow][column] = new Peg(color, holeDiameter, gridLeft + 
                    columnWidth * column + holeBufferX, gridTop + currentRow * rowWidth + holeBufferY); //add the hole
            }
            else
            {
                userPegs[currentRow][column] = new Peg(color, pegDiameter, gridLeft + 
                columnWidth * column + pegBufferX, gridTop + currentRow * rowWidth + pegBufferY); //add the peg
            }
         }
    }
    
    /**
     * Places the first set of pegs under the cover and stores them in the array.
     */
    public void generateCode()
    {
        Random g = new Random();
        for(int i = 0; i < codePegs; i++)
        {
            int color = g.nextInt(Peg.COLORS.length - Peg.RESERVED) + Peg.RESERVED; //generate random number in proper range
            userPegs[currentRow][i] = new Peg(color, pegDiameter, gridLeft + 
                columnWidth * i + pegBufferX, gridTop + currentRow * rowWidth + pegBufferY); //add the peg
        }
    }
    
    /**
     * Generates the array of computer pegs for the current row.
     * @return true if the code is correct
     */
    public boolean evaluate()
    {
        //first the total number of pegs
        int[] correctColorCounts = countColors(0);
        int[] guessedColorCounts = countColors(currentRow);
        int totalPegs = 0;
        for(int i = 0; i < guessedColorCounts.length; i++)
        {
                totalPegs+=Math.min(guessedColorCounts[i], correctColorCounts[i]);
        }
        //now the red pegs, which are easier.
        int redPegs = 0;
        for(int i = 0; i < codePegs; i++)
        {
            if(userPegs[0][i].getColor() == userPegs[currentRow][i].getColor())
            {
                redPegs++;
            }
        }
        //get white pegs, generate array
        int whitePegs = totalPegs - redPegs;
        ComputerPegArray evaluation = new ComputerPegArray(pegDiameter, pegDiameter, 
            redPegs, whitePegs, codePegs, codePegs * columnWidth + gridLeft + pegBufferX,
            currentRow * rowWidth + gridTop + pegBufferY);
        //add to computer pegs
        computerPegs.add(evaluation);
        //increment current row
        currentRow++;
        //true if the user got it right
        return redPegs == codePegs;
    }
    
    /**
     * Counts the pegs of each color in a given row
     * @param row the specific row
     * @return an array of counts with one value at each index--parallel to the color 
     * constants array of Peg
     */
    public int[] countColors(int row)
    {
        int[] colorCounts = new int[Peg.COLORS.length]; //counts the pegs of each color
        for(int i = 0; i < codePegs; i++)//
        {
            int color = userPegs[row][i].getColor();
            colorCounts[color]++; //store the number of pegs of each color in the array
        }
        return colorCounts;
    }
    
    /**
     * @return the diameter of a peg.
     */
    public double getPegDiam()
    {
        return pegDiameter;
    }
    
    /**
     * @return the point at the upper left corner of the next ComputerPegArray
     */
    public double[] getAnsPos()
    {
        return new double[]{codePegs * columnWidth + gridLeft + pegBufferX, 
            currentRow * rowWidth + gridTop + pegBufferY};
    }
    
    /**
     * @return the array of pegs from the current row
     */
    public Peg[] getPegs()
    {
        return userPegs[currentRow];
    }
    
    /**
     * Removes the peg at the position in the current row.
     */
    public Peg removePeg(int peg)
    {
        Peg temp = userPegs[currentRow][peg]; //need something to return
        userPegs[currentRow][peg] = new Peg(Peg.DARKBLUE, holeDiameter, 
                    gridLeft + holeBufferX + columnWidth * peg, gridTop + holeBufferY + rowWidth * currentRow); //replaces with a hole
        return temp; // the saved peg
    }
    
    /**
     * Tests whether the board is entirely full.
     * @return true if all rows have been used.
     */
    public boolean isFull()
    {
        return currentRow > guesses; 
    }
}