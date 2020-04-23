// going to be lazy about imports in this class
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;

/**
 * Grenade is a Weapon, it can bounce off of the floor.
 *
 * @author Kristi Boardman, Cameron Costello, Will Skelly, Jake Burch
 * @version Spring 2020
 */
public class Grenade extends Weapon
{

    //THE WEIGHT OF EVERY GRENADE WEAPON
    private static final int WEIGHT = 10;

    //THE SIZE OF EVERY GRENADE WEAPON
    private static final int SIZE = 50;

    //HOW LONG THE EXPLOSION IS ON SCREEN FOR
    private static final int EXPLOSION_TIME = 250;

    //WHEN THE GRENADE EXPLODES
    private static final int WHEN_TO_EXPLODE = 10;

    //DETERMINES WHEN GRENADE EXPLODES
    protected int countTillExplosion;

    //DETERMINES WHEN GRENADE EXPLODES
    protected boolean explodedInAir;

    /**
     * Creates a Grenade Weapon.
     * 
     * @param container The container to paint it in.
     * @param position The weapon's current position.
     * @param inertia Determines object velocity.
     */
    public Grenade(JComponent container, Point2D.Double position, Point2D.Double inertia)
    {
        //CALL THE SUPER CONSTRUCTOR OF THE WEAPON
        super(container, position, inertia);

        //CREATES THE GRENADE IMAGE
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        typeFilePath = "weaponTypeTwo.png";
        type = toolkit.getImage(typeFilePath).getScaledInstance(SIZE,SIZE,0);
    }

    /**
     * Paints the Grenade weapon on the container at its position.
     * 
     * @param g The graphics object.
     */
    @Override
    public void paint(Graphics g) {

        //IF THE GRENADE HAS NOT EXPLODED, DRAW IT.
        if (!explodedInAir)
        {
            g.drawImage(type, (int)position.x, (int)position.y, null);
        }
        else
        {
            //DRAW THE EXPLOSION
            g.setColor(Color.RED);
            g.fillOval((int)position.x - SIZE/4, (int)position.y - SIZE/4, SIZE * 3/2, SIZE * 3/2);
            g.setColor(Color.BLACK);
            g.drawOval((int)position.x - SIZE/4, (int)position.y - SIZE/4, SIZE * 3/2, SIZE * 3/2);

            g.setColor(Color.RED);
            g.fillOval((int)position.x - SIZE/2, (int)position.y- SIZE/2, SIZE, SIZE);
            g.setColor(Color.BLACK);
            g.drawOval((int)position.x- SIZE/2, (int)position.y- SIZE/2, SIZE, SIZE);

            g.setColor(Color.RED);
            g.fillOval((int)position.x + SIZE/2, (int)position.y - SIZE/2, SIZE, SIZE);
            g.setColor(Color.BLACK);
            g.drawOval((int)position.x + SIZE/2, (int)position.y - SIZE/2, SIZE, SIZE);

            g.setColor(Color.RED);
            g.fillOval((int)position.x + SIZE/2, (int)position.y + SIZE/2, SIZE, SIZE);
            g.setColor(Color.BLACK);
            g.drawOval((int)position.x + SIZE/2, (int)position.y + SIZE/2, SIZE, SIZE);

            g.setColor(Color.RED);
            g.fillOval((int)position.x - SIZE/2, (int)position.y + SIZE/2, SIZE, SIZE);
            g.setColor(Color.BLACK);
            g.drawOval((int)position.x - SIZE/2, (int)position.y + SIZE/2, SIZE, SIZE);

            velocity.x = 0;
            velocity.y = 0;

        }

    }

    /**
     * Moves the weapon across the screen based on its own velocity and the pull of gravity.
     * Weapons may bounce off of the ceiling and the floor.
     */
    @Override
    public void run(){
        while (!explodedInAir) {
            //WHILE THE WEAPON HAS NOT BEEN RELEASED ONTO THE SCREEN
            while(!released) {
                container.repaint();
            }
            
            //SLEEP BETWEEN REDRAWING FRAMES
            try {
                sleep(DELAY_TIME);
            }
            catch (InterruptedException e) {
            }

            //EVERY ITERATION, UPDATE COORDINATES
            position.x += velocity.x;
            position.y += velocity.y;

            //BOUNCE OFF OF THE FLOOR
            if (position.y > yMax) {
                position.y = yMax;
                bounced = true;
                velocity.y = -velocity.y;
            }

            //IF WE BOUNCED, WE'RE GOING TO DAMPEN SPEED IN BOTH DIMENSIONS
            if (bounced && countTillExplosion == 0) {
                velocity.x *= DAMPING;
                velocity.y *= DAMPING;
            }

            //KEEP COUNTING FOR EACH FRAME
            if(bounced){
                countTillExplosion++;
            }

            //WHEN THE COUNT REACHES OUR CONSTANT, THE GRENADE WILL EXPLODE
            if (countTillExplosion == WHEN_TO_EXPLODE){
                explodedInAir = true;
            }

            //IF WE'VE ALMOST STOPPED MOVING, LET'S END
            done = (position.y == yMax &&
                Math.abs(velocity.y) < ALMOST_STOPPED &&
                Math.abs(velocity.x) < ALMOST_STOPPED);

            //ADD IN GRAVITY TO THE VELOCITY
            velocity.y += GRAVITY;
        }

        //THIS IS HOW LONG THE GRENADE EXPLODES FOR ON SCREEN.
        try {
            sleep(EXPLOSION_TIME);
        }
        catch (InterruptedException e) {
        }

        done = true;

    }

    /**
     * Returns the weight of the Grenade weapon.
     * 
     * @return The weight of the Grenade weapon object.
     */
    @Override
    public int getWeight(){
        return WEIGHT;
    }

    /**
     * Returns the size of the Grenade weapon.
     * 
     * @return The size of the Grenade weapon object.
     */
    @Override
    public int getSize(){
        return SIZE;
    }
}