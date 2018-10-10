import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import java.awt.Font;
/**
 * Class for managing high scores
 *
 * @author John Rabaey
 * @version 5/25/2017
 */
public class HighScores 
{
    public static final int COUNT = 10; //number of high scores to display
    private ArrayList<String> scores = new ArrayList<String>(); //array where scores when active
    /**
     * Constructs a high scores manager
     */
    public HighScores()
    {
        try //because throwing is such a pain...
        {
            File highScores = new File("scores.txt"); //the file I'm using
            Scanner reader = new Scanner(highScores); 
            for(int i = 0; i < COUNT; i++) //store lines of document in my arraylist
            {
                scores.add(reader.nextLine());
            }
            reader.close();
        }
        catch(FileNotFoundException e) {System.out.print("error");} //for debugging
    }
    
    /**
     * Adds a score to the array list.
     * @param score the score to add
     * @param name the name of the player
     * @return the integer of the new score's place
     */
    public int addScore(int score, String name)
    {
        for(int i = 0; i < COUNT; i++) //check each score in the array
        {
            Scanner reader = new Scanner(scores.get(i)); //scanner to help analyze strings
            if(reader.nextInt() < score) //find a score less than the users
            {
                scores.add(i, score + " " + name); //add the new score before the smaller one
                scores.remove(COUNT); //the last one goes out
                return i+1;
            }
        }
        return 0; //if you didn't get a good score, you're in 0th place
    }
    
    /**
     * Saves the scores to the file.
     */
    public void save()
    {
        try
        {
            File highScores = new File("scores.txt"); //same file we read them from
            PrintStream writer = new PrintStream(highScores); 
            for(int i = 0; i < COUNT; i++)
            {
                writer.println(scores.get(i)); //write scores to file, each on a line
            }
            writer.close();
        }
        catch(FileNotFoundException e){}
    }
    
    /**
     * Displays the scores in a window.
     */
    public void display(Component parent)
    {
        String message = "";
        boolean first = true;
        int maxDigits = 0;
        for(int i = 0; i < COUNT; i++)
        {
            Scanner parser = new Scanner(scores.get(i));
            int score = parser.nextInt();
            if(first){maxDigits = (int) Math.log10(score); first = false;}
            int spaces = maxDigits - (int)Math.log10(score);
            message += score;
            message += copy(" ", spaces + 1);
            message += parser.next();
            message += "<br>";
        }
        JLabel label = new JLabel("<html>" + message + "<html>");
        label.setFont(new Font("Courier New", Font.PLAIN, 25));
        JOptionPane.showMessageDialog(parent, label, "HIGH SCORES",
            JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Utility function for producing repetitive strings of code
     * @param sequence the sequence of characters to repeat 
     * @param times the number of times to repeat it
     * @return the sequence, repeated the specified number of times
     */
    public static String copy(String sequence, int times) 
    {
        String result = "";
        for(int i = 0; i < times; i++)
        {
            result += sequence;
        }
        return result;
    }
}
