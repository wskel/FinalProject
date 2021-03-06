/*
 * Copyright (C) 2020 William Skelly, Kristi Boardman, Cameron Costello, and Jacob Burch
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package TowerDefenseGame;

 
//imports
import java.awt.geom.Point2D;
import java.util.Random;
import javax.swing.JComponent;

/**
 * Procures Soldiers for the SoldierArmy class.
 *
 * @author Kristi Boardman, Cameron Costello, Will Skelly, Jake Burch
 * @version Spring 2020
 */
public class SoldierProvider
{    
    /**
     * Returns a random Soldier from our four options.
     * 
     * @param container The container to paint it in.
     * @param army The SoldierArmy that is calling this SoldierProvider
     * @param x_start The starting position along the x-axis
     * @param y_stop The factor determining the upper bound of the random starting position along the y-axis
     * @param difficultyLevel The difficulty level.
     * 
     * @return The Weapon that the QuarterMaster has chosen for the user.
     */
    public static Soldier getRandom(JComponent container, SoldierArmy army, int x_start, int y_stop, Difficulty difficultyLevel){
        //CREATE A RANDOM OBJECT
        Random r = new Random();

        //GIVE EACH ENEMY A RANDOM Y POSITION SO THE ENEMIES ARE STAGGERED
        int yCoord = (r.nextInt(container.getHeight() / 4 - y_stop) + 3 * container.getHeight() / 4 - y_stop / 14 );

        //THE STARTING POSITION OF THE SOLDIER
        Point2D.Double startSpot = new Point2D.Double(x_start, yCoord);

        //THE TYPE OF SOLDIER TO BE CHOSEN
        int soldierType;
        /* DIFFERENT TYPES OF ENEMIES CAN SPAWN DEPENDING ON THE DIFFICULTY LEVEL OF
        EACH INDIVIDUAL WAVE:
        EASY IS ONLY WEAKEST TWO OPTIONS
        MEDIUM IS ALL BUT THE STRONGEST OPTION
        HARD IS EVERY TYPE OF SOLDIER CAN SPAWN
         */
        switch(difficultyLevel){
            case EASY:
            soldierType = r.nextInt(2);
            break;
            case MEDIUM:
            soldierType = r.nextInt(3);
            break;
            case HARD:
            soldierType = r.nextInt(4);
            break;
            default:
            soldierType = r.nextInt(2);
        }

        //THE TYPES OF SOLDIERS BEING CREATED BASED ON THEIR SPEEDS
        //CREATE A SOLDIER TO RETURN
        Soldier soldier;
        switch(soldierType){
            case 1:
            //A LITTLE FASTER THAN THE AVERAGE ZOMBIE
            soldier = new Hunchback(startSpot, container, army);
            break;
            case 2:
            //THIS GUY ISN'T A SLOUCH WHEN IT COMES TO SPEED WALKING
            soldier = new BigEye(startSpot, container,  army);
            break;
            case 3:
            //ARR, HE'S COMING TO GET YOUR BOOTY
            soldier = new Pirate(startSpot, container,  army);
            break;
            default:
            //WEAKEST SOLDIER
            soldier = new AverageZombie(startSpot, container,  army);
        }

        //RETURN THE NEW SOLDIER
        return soldier;
    }
}

