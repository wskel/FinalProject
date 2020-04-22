import java.util.Vector;
/**
 * Write a description of class Shoppers here.
 *
 * @author Cameron Costello, Kristi Boardman, Will Skelly, Jacob Burch
 * @version Spring 2020
 */
abstract public class Shopper extends Thread
{

    protected Vector<Item> shoppingList;
    
    protected double cash;
    
    protected double jailedProb;

    protected int morality;
    
    protected boolean done;
    
    /**
     * Constructor for objects of class Shoppers
     */
    public Shopper(Vector<Item> shoppingList)
    {
        this.shoppingList = shoppingList;
    }
    
    abstract public void run();
    
    public boolean done()
    {
        return done;
    }
    
    public double getCash()
    {
        return cash;
    }
    

}
