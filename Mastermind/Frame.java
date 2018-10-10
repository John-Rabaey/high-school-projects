import javax.swing.JPanel;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JOptionPane;

/**
This frame contains a mostly autonomous game component.
 */
public class Frame extends JFrame
{
    private Component game; //the game object
    private Peg activePeg; //the peg that follows the mouse drag--null when not in use
    class MousePressListener extends MouseAdapter
    {
        /**
         * If a peg is pressed, make it the activePeg.
         * If the button is pressed, submit the current code.
         * @param event the click
         */
        public void mousePressed(MouseEvent event)
        {
            activePeg = game.getPeg(event.getX(), event.getY());
            game.repaint();
            if(game.getSubmitBounds().contains(event.getX(), event.getY())) game.submit();
        }
        /**
         * If a peg is released, add it to the game, delete the temporary peg, and reset the moveable pegs
         * @param event the mouse release
         */
        public void mouseReleased(MouseEvent event)
        {
            if(activePeg !=null)  
            {
                game.addPeg(new Peg(activePeg.getColor(), 0, event.getX(), event.getY()));
                //adds a peg at the mouse's location that's the right color. Don't worry about diameter
            }
           
            game.removeTemp(); //make sure the temp peg inside the game is null, too.
            activePeg = null; //the active peg disappears
            game.resetPegs(); //resets the drag&drop pegs
            game.repaint();
        }
    }
    
    class MouseMovements extends MouseMotionAdapter
    {
        /**
         * When the mouse is dragged, the active peg should follow the mouse. This is quite straightforward
         */
        public void mouseDragged(MouseEvent event)
        {
            if(activePeg != null)
            {
                activePeg.moveTo(event.getX(), event.getY());
            }
            game.repaint();
        }
    }
    
    /**
     * Constructs a frame.
     * @param competitive the mode for the game board component
     * @param name the name of the player
     */
    public Frame(boolean competitive, String name)
    {
        setSize(600,900); //these arbitrary values can be adjusted, depending on the development environment
        activePeg = null; //start w/o active peg
        game = new Component(0,0,500,800, competitive, name); //set game up
        add(game); //add game to frame
        MouseListener pegMover = new MousePressListener();
        MouseMotionListener pegMotion = new MouseMovements(); //setting up the listeners...
        game.addMouseListener(pegMover);
        game.addMouseMotionListener(pegMotion);
        //set a couple more basic specs...
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
