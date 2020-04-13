// going to be lazy about imports in this class
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;

/**
 * Write a description of class AverageZombie here.
 *
 * @author Cameron Costello, Kristi Boardman, Jacob Burch, Will Skelly
 * @version Spring 2020
 */
public class Hunchback extends Soldier
{
    
    private static final int SIZE = 80;
    
    private static final int STRENGTH = 10;
    
    private static final int SPEED = 5;

    /**
     * Constructor for objects of class AverageZombie
     */
    public Hunchback(Point2D.Double position, JComponent container)
    {
        super(position, container);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        typeFilePath = "soldierTypeThree.jpg";
        type = toolkit.getImage(typeFilePath).getScaledInstance(SIZE,SIZE + SIZE/2,0);
        hitsUntilDeath = 3;
        speed = SPEED;
        position.setLocation(position.x, position.y - SIZE);
    }

    @Override
    public void paint(Graphics g) 
    {
        if(!done) {
            g.drawImage(type, (int)position.x, (int)position.y, null);
        } else {

        }
    }
    
    @Override
    public int getStrength() 
    {
        return STRENGTH;
    }
    
    @Override
    public int getSize() 
    {
        return SIZE;
    }
    
}
