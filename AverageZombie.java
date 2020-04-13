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
public class AverageZombie extends Soldier
{
    
    private static final int SIZE = 50;
    
    private static final int STRENGTH = 10;

    /**
     * Constructor for objects of class AverageZombie
     */
    public AverageZombie(Point2D.Double position, JComponent container)
    {
        super(position, container);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        typeFilePath = "soldierTypeOne.png";
        type = toolkit.getImage(typeFilePath).getScaledInstance(SIZE,SIZE,0);
        hitsUntilDeath = 1;
        
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
