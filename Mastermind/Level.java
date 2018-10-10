
/**
 * A very simple class containing a number of pegs, a number of guesses, and a description of the level.
 * 
 * @author John Rabaey
 * @version 5/25/2017
 */
public class Level
{
    private int pegs;
    private int guesses;
    private String description;
    /**
     * Constructor for a level
     * @param guesses the number of guesses the user gets for the code
     * @param pegs the number of pegs in the code
     * @param description a description of the level
     */
    public Level(int guesses, int pegs, String description)
    {
        this.pegs = pegs;
        this.guesses = guesses;
        this.description = description;
    }
    /**
     * @return the number of pegs
     */
    public int getPegs()
    {
        return pegs;
    }
    /**
     * @return the number of guesses
     */
    public int getGuesses()
    {
        return guesses;
    }
    /**
     * @return the number of pegs
     */
    public String getDescription()
    {
        return description;
    }
}
