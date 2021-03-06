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
package SupermarketShoppers;

import java.util.Vector;

/**
 * This class models a shopper that Steals.
 * 
 * @author Cameron Costello, Kristi Boardman, Will Skelly, Jacob Burch
 * @version Spring 2020
 */
abstract public class StealingShopper extends Shopper{

    //variables for StealingShopper
    public static final int INCREASE_PROB = 5;
    public boolean startedStealing;
    private Object jailProbLock = new Object();
    
    public StealingShopper(Vector<Item> shoppingList, Inventory inventory, int number, Jail jail, ShopperManager supermarketManager) {
        super(shoppingList, inventory, number, jail, supermarketManager);
    }
    
    /**
     * Returns whether the StealingShopper has started stealing.
     * 
     */
    public boolean isStealing() {
        return startedStealing;
    }

     /**
     * Increases the jail probability of the StealingShopper.
     * 
     */
    public boolean increaseJailProb() {
        if (startedStealing) {
            synchronized (jailProbLock) {
                if (jailedProb < ONE_HUNDRED) {
                    jailedProb += INCREASE_PROB;
                }
            }
        }
        return startedStealing;
    }
    
    /**
     * Sends the Shopper to jail if their jail probability >= 100 percent and
     * sets the Shopper to Done if they go to jail.
     * 
     */
    protected void checkJailProb(){
        if (startedStealing) {
                synchronized (jailProbLock) {
                    if (jailedProb >= ONE_HUNDRED) {
                        done = true;
                        jail.getArrested(this);
                        this.done = true;
                    }
                }
            }
    }
    
    /**
     * Accepts a ShopperVisitor to visit with this Shopper.
     * 
     * @param shopperVisitor the shopperVisitor to visit
     * @return the result of visiting the ShopperVisitor
     */
    @Override
    public boolean accept(ShopperVisitor shopperVisitor){
        return shopperVisitor.visit(this);
    }
}
