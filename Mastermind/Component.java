import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JOptionPane;
/**
 * The mastermind component manages the game.
 *
 * @author John Rabaey
 * @version 5/22/17
 */
public class Component extends JComponent
{
    //Sets up the levels.
    Level[] levels = new Level[]{null,
        new Level(8,2,"First level. Easy."),//add instructions in frame class to display right
        new Level(5,2,"You now have only 5 tries. This is possible--if you're smart!"),
        new Level(10,3,"Try with 3 pegs in my code. This is getting harder!"),
        new Level(6,3,"This one might be tough: only 6 tries!"),
        new Level(12,4,"4 pegs now!"),
        new Level(7,4,"Just 7 tries!"),
        new Level(10,5, "5 pegs!"),
        new Level(20,10, "Bonus level! 10 pegs!")};
    Board b; //the game board reference
    private int level; //the current level
    private double xLeft, yTop, width, height; //specifications of the board
    private Peg[] pegs;
    private Rectangle2D.Double submitButton;
    private Peg temp; // a peg that doesn't fit in the board grid--use for transferring one peg to a new place
    private double pegSize;
    private boolean submitVisible; //true when the question mark should show.
    boolean first; //helps with JOptionPane message display
    long startTime; //the time when the program begins running
    boolean competitive; //whether it's intense
    int totalGuesses = 0; //the total number of guesses the user took to complete the entire game
    String name; //the player's name
    /**
     * Constructs a component at level one, with given size
     * @param xLeft the upper left corner of the board
     * @param yTop the upper left corner of the board
     * @param width the width of the board
     * @param height the height of the board 
     * @param competitive a boolean specifying whether the game should be played in competitive mode
     * @param name the name of the player
     */
    public Component(double xLeft, double yTop, double width, double height, boolean competitive, String name)
    {
        startTime = System.currentTimeMillis();//start tiemr
        this.competitive = competitive;
        level = 0;
        this.xLeft = xLeft;
        this.yTop = yTop;
        this.width = width;
        this.height = height; //get the basics right
        this.name = name;
        first = true; //fist level--slight changes in behavior, to display joptionpanes correctly
        nextLevel(); //sets up the game board
    }
    
    /**
     * Simply draws the component
     * @param g the graphics context
     */
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        b.draw(g2); //draw the board
        
        if(submitVisible)
        {
            //draws the submit button
            g2.setColor(Color.BLACK); //draw the button
            g2.setFont(new Font("cambria", Font.PLAIN, (int)b.getPegDiam()));
            g2.drawString("  ?  ", (int)submitButton.getX(), (int) (submitButton.getY() + b.getPegDiam()));
        }
        for(Peg p : pegs) //draw auxiliary pegs
        {
            p.draw(g2);
        }
        
        //draw the temp peg, if necessary
        if(temp != null)temp.draw(g2);
    }
    
    /**
     * Resets the board, but not the level. Also displays a description of the level.
     */
    public void newGame()
    {
        //estimate peg size
        b = new Board(xLeft, yTop, width, height,
            levels[level].getGuesses(), levels[level].getPegs());//board specs correspond to level
        pegSize = b.getPegDiam();//rough estimate of peg diameter--size board down so drag&drop pegs are in the right place
        
        b = new Board(xLeft, yTop, width - pegSize, height, 
            levels[level].getGuesses(), levels[level].getPegs()); //board specs redone with peg diameter.
        
        b.generateCode(); //prepare computer code
        b.evaluate(); //board is ready
        
        pegs = new Peg[Peg.COLORS.length - Peg.RESERVED]; //setting up an array of pegs that can
                                                          //be dropped and dragged--off to the side
        submitVisible = true; //yes, you can see the submit button
        resetPegs(); //sets up the drag&drop pegs
        resetSubmit(); //moves the submit button down a line
        repaint(); //display changes
    }
    
    /**
     * Resets the available pegs--do this every time one is added
     */
    public void resetPegs()
    {
        //the drag&drop pegs
        for(int i = 0; i < pegs.length; i++)
        {
            pegs[i] = new Peg(i + Peg.RESERVED, 
                b.getPegDiam(), width - b.getPegDiam(), 
                b.getPegDiam() * i); //sets the pegs to position on right of board
        }
    }
    
    /**
     * Submits the current code. The board object evaluates and moves to the next line
     */
    public void submit()
    {
        totalGuesses++;
        boolean correct = b.evaluate(); //check the answers
        if(correct)levelUp();
        else if(b.isFull()) startOver("Looks like you ran out of chances. Now you have to start over.", true);
        resetSubmit();  //move the submit button down a line
        repaint();
    }
    
    /**
     * This method displays the dialog prompting the user to choose the next move
     */
    public void levelUp()
    {
        submitVisible = false; //don't care to have the button showing while we display EOL messageboxes
        b.uncoverCode(); //display the code for the satisfaction of the user
        if(level == levels.length - 1) {endGame();} //if we're done, move to endgame
        else if(!competitive)
        {
            int response = JOptionPane.showConfirmDialog(this, 
                "You guessed my code! Would you like to move to level " + (level + 1) + "?",
                "Level up?", JOptionPane.YES_NO_OPTION); //level up? message box
            if(response == JOptionPane.YES_OPTION) {nextLevel();} //move up
            else 
            {
                Integer levelChoice = (Integer) JOptionPane.showInputDialog(this, 
                    "Which level would you like to play? ", "Choose level", JOptionPane.PLAIN_MESSAGE,
                    null, range(1, levels.length), 1);
                level = Math.min(levelChoice - 1, levels.length - 1); //move to one less than chosen level ---nextLevel() increments
                nextLevel();
            }
            
        }
        else nextLevel(); //skip all that stuff and move on
    }
    
    public void nextLevel()
    {
        level++; //move to next level
        newGame(); //construct new gameboard
        //Message box displays level description
        if(!first && !competitive)
        {
            JOptionPane.showMessageDialog(this, 
                levels[level].getDescription(), "Level " + level, 
                JOptionPane.PLAIN_MESSAGE);
        }
        first = false; //no longer on the first level --back to normal
        repaint();
    }
    
    /**
     * Adds a peg at the open position in the code closest to the peg's current location
     * @param peg the peg to add (roughly)
     */
    public void addPeg(Peg aPeg)
    {
        b.addPegAt(aPeg.getX(), aPeg.getColor()); //adds a peg on the board at (roughly) the position of the given peg
    }
    
    /**
     * Returns the diameter of a peg
     * @return the diameter of a peg
     */
    public double getPegDiam()
    {
        return b.getPegDiam();
    }
    
    /**
     * Yields the reference to a peg that can be moved. This handles the user click on a drop&drag peg or an 
     * already-placed peg
     * @param x the x-coordinate of the click
     * @param y the y-coordinate of the click
     * @return peg the reference to the peg in the drop&drag array
     */
    public Peg getPeg(double x, double y)
    {
        for(Peg p : pegs) //check all pegs to see if click falls within peg bounds (drop&drag)
        {
            if(p.getBounds().contains(x, y))
            {
                return p;
            }
        }
        
        Peg[] pegRow = b.getPegs(); //current row from the board 
        for(int i = 0; i < pegRow.length; i++) //check board pegs to see if peg falls within bounds
        {
            if(pegRow[i].getBounds().contains(x, y))
            {
                temp = pegRow[i]; //sets the temp reference in the board class for moving pegs already on the board
                //(it has to get drawn somewhere, so it gets its separate line in the drawing code.
                return b.removePeg(i);
            }
        }
        return null; //if a peg has not been found (most of the time...)
    }
    
    /**
     * @return the rectangle bounds of the submit button
     */
    public Rectangle2D.Double getSubmitBounds()
    {
        return submitButton; //for user clicks on the submit button
    }
    
    /**
     * Sets the submit button to a new, appropriate position
     */
    public void resetSubmit()
    {
        submitButton = new Rectangle2D.Double(b.getAnsPos()[0], b.getAnsPos()[1], 
            b.getPegDiam(), b.getPegDiam()); //moves the submit button down a line on the board
    }
    
    /**
     * Destroys the temp peg
     */
    public void removeTemp()
    {
        temp = null; //now it's gone
    }
    
    /**
     * Starts the game from the first level again
     * @param message the message to display that explains why we're restarting
     * @param punish true if the user is in competitive mode and should get a penalty
     */
    public void startOver(String message, boolean punish)
    {
        startTime = System.currentTimeMillis(); //reset game clock
        if(!punish)totalGuesses = 0; //reset # of guesses
        level = 0; //start at zero
        if(!competitive)
        {
            JOptionPane.showMessageDialog(this, message, //display a message
                                        "Restarting...", JOptionPane.WARNING_MESSAGE);
        }
        nextLevel(); //start over
    }
    
    /**
     * Endgame handler. Either kills the thread or restarts the game. 
     */
    public void endGame()
    {
        long endTime = System.currentTimeMillis(); //stop game clock
        long totalTime = (endTime - startTime) / 1000; //total time in seconds
        if(competitive)
        {
            JOptionPane.showMessageDialog(this, "You completed all the levels! Great job!\n" + 
                "It took you a total of " + totalTime + " seconds, \n" +
                "and " + totalGuesses + " guesses. Your total score was \n" +
                getScore(totalTime, totalGuesses), 
                "You won!", JOptionPane.PLAIN_MESSAGE); //encouraging message
            HighScores h = new HighScores(); //initialize high scores manager
            int newHigh = h.addScore(getScore(totalTime, totalGuesses), name); //newhigh = the place the user got
            if(newHigh > 0)
            {
                JOptionPane.showMessageDialog(this, "New high score! You're in " + 
                    " place " + newHigh, "High score!", JOptionPane.PLAIN_MESSAGE); //Wow, good job messagebox
            }
            h.save(); //save high scores
            
            h.display(this); //display high scores in current component
        }
        else 
        {
            JOptionPane.showMessageDialog(this, "You completed all the levels! Great job!", 
                "You won!", JOptionPane.PLAIN_MESSAGE); //noncompetitive--no scores, etc.
        }
        
        int response = JOptionPane.showConfirmDialog(this, 
            "Do you want to play again?",
            "Play again?", JOptionPane.YES_NO_OPTION); //play again? message box?
        if(response == JOptionPane.YES_OPTION) startOver("New game", false); //start over
        else  System.exit(0); //close everything
    }
    
    /**
     * Simple score calculator
     * @param time in seconds that the user took
     * @param guesses the total number of guesses the user took
     * @return score the score the user recieved
     */
    public int getScore(long time, int guesses)
    {
        return (int) (100000 * Math.pow(levels.length, 2) / guesses);
    }
    /**
     * Mimics the range function in python--gets an array of integers from a (inclusive) to b (exclusive)
     * @param a the start integer
     * @param b the stop integer
     * @return an array of integer objects (this is handier for my context)
     */
    public static Integer[] range(int a, int b)
    {
        Integer[] range = new Integer[b-a];
        for(int i = 0; i < b-a; i++)
        {
            range[i] = a+i;
        }
        return range;
    }
    
}