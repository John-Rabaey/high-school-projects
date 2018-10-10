import javax.swing.JFrame;
import javax.swing.JOptionPane;
/**
   This program sets up the entire mastermind game. See comments on main method
   */
public class Viewer
{
    /**
     * Main method of the mastermind game. Originally intended to be a driver method, it ballooned a bit as 
     * I discovered that some messageboxes needed to be called here... long story.
     */
    public static void main(String[] args)
    {
        //gives the option to play in competitive mode
        int response = JOptionPane.showConfirmDialog(null, 
            "Welcome to Mastermind. Would you like to play in competitive mode? Competitive mode\n" +
             "allows you to skip all the introductory dialog boxes, etc. It also allows you to " + 
             " recieve a score.",
            "Competitive mode?", JOptionPane.YES_NO_OPTION);
        boolean competitive = response == JOptionPane.YES_OPTION;
        String name = "";
        if(competitive) //ask name
        {
            name = JOptionPane.showInputDialog(null, "Enter your name: ", "Name?", JOptionPane.PLAIN_MESSAGE);
        }
        JFrame frame = new Frame(competitive, name); //now the autonomous frame takes over
        
        if(!competitive) //give an introduction to the game AFTER displaying it in the component. 
        {
            JOptionPane.showMessageDialog(frame, 
                "Ready to begin? Here's how it works:\nUnder the dark blue cover, I have created " +
                "a code, consisting of two colored circles. \nYour task is to guess my code. After " + 
                "you have chosen your guess by dragging a colored \ncircle from the pallette on the " + 
                "right to a place on the gameboard, click the question \nmark button, and I will give " + 
                "you a hint for the second try. Every red circle I place \nbeside your guess indicates that " +
                "one of the circles you placed (regardless of order) was the \ncorrect color and placed in " +
                "the correct order. Every white circle I place beside your guess \nindicates that one of the " + 
                "circles you placed was the right color, but in the wrong position. \nGood luck! Oh, and one " + 
                "more note: I do occasionally hide two pegs of the same color.", "Level " + 1, 
                JOptionPane.PLAIN_MESSAGE); //instructions
        }
    }
}