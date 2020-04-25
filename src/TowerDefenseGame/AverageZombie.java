package TowerDefenseGame;

 
//imports
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
/**
 * The AverageZombie Soldier.
 *
 * @author Cameron Costello, Kristi Boardman, Jacob Burch, Will Skelly
 * @version Spring 2020
 */
public class AverageZombie extends Soldier
{
    //THE SIZE OF THE AVERAGEZOMBIE SOLDIER
    private static final int SIZE = 80;
    
    //THE STRENGTH OF THE AVERAGEZOMBIE SOLDIER
    private static final int STRENGTH = 1;
    
    //THE SPEED OF THE AVERAGEZOMBIE SOLDIER
    private static final int SPEED = 2;
    
    //THE AMOUNT OF POINTS THE AVERAGEZOMBIE SOLDIER IS WORTH
    private static final int POINTS = 100;

    /**
     * Creates a AverageZombie Soldier object.
     * 
     * @param position The position of the soldier.
     * @param container The container to repaint the soldier in.
     * @param army The reference to the army that this soldier will belong to.
     */
    public AverageZombie(Point2D.Double position, JComponent container, SoldierArmy army)
    {
        //CALLS THE SUPER CONTRUCTOR FOR THE SOLDIER
        super(position, container, army);
        
        //CREATES THE IMAGE OF THE AVERAGEZOMBIE SOLDIER AND GIVES IT A SIZE
        try {
            typeFilePath = "/soldierTypeOne.png";
            InputStream imageStream = new BufferedInputStream(DatabaseDriver.class.getResourceAsStream(typeFilePath));
            Image image = ImageIO.read(imageStream);
            type = image.getScaledInstance(SIZE,SIZE + SIZE/2,0);
        } catch (IOException e) {
            System.out.println("AverageZombie: Error loading image");
            e.printStackTrace();
        }
        
        //SETS THE HEALTH OF THE SOLDIER
        hitsUntilDeath = 1;
        
        //SETS THE SPEED OF THE SOLDIER
        speed = SPEED;
        
        //SETS THE LOCATION OF THE SOLDIER AT ITS POSITION
        position.setLocation(position.x, position.y - SIZE);
    }
    
    /**
     * Gets the amount of points each soldier is worth.
     * 
     * @return The amount of points each soldier is worth.
     */
    @Override
    public int getPoints() {
        return POINTS;
    }
    
    /**
     * Returns the strength of the AverageZombie Soldier.
     * 
     * @return The strength of this AverageZombie soldier.
     */
    @Override
    public int getStrength() 
    {
        return STRENGTH;
    }
    
    /**
     * Returns the size of the AverageZombie Soldier.
     * 
     * @return The size of this AverageZombie soldier.
     */
    @Override
    public int getSize() 
    {
        return SIZE;
    }
    
}
