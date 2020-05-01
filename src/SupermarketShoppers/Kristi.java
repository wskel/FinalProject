package SupermarketShoppers;

import java.util.Vector;
import java.util.Random;
/**
 * Write a description of class Shoppers here.
 *
 * @author Cameron Costello, Kristi Boardman, Will Skelly, Jacob Burch
 * @version Spring 2020
 */
public class Kristi extends Shopper
{

    public static final int MORALITY_NUM = 3;

    public static final int MAX_CASH = 1000;

    public static final int INCREASE_PROB = 5;

    public static final int SENT_TO_JAIL = 65;

    public static final int ONE_HUNDRED = 100;

    public boolean startedStealing;

    private Object lock = new Object();

    /**
     * Constructor for objects of class Shoppers
     */
    public Kristi(Vector<Item> shoppingList, Inventory inventory, int number, Jail jail, SupermarketManager supermarket)
    {
        super(shoppingList, inventory, number, jail, supermarket);

        morality = MORALITY_NUM;
        cash = random.nextInt(MAX_CASH / MORALITY_NUM) + 1;
        jailedProb = (random.nextDouble() * ONE_HUNDRED) * MORALITY_NUM;
    }

    @Override
    public void run()
    {
        setMinimumPrice();
        
        int i = 0;
        while (!done && i < shoppingList.size()) {
            Item currentItem = shoppingList.get(i);

            int index = inventory.containsItem(currentItem);

            Item itemToCheck = inventory.getList().get(index);

            int itemQuantity = currentItem.getItemQuantity();
            if (cash > 0) {

                int qPurchased = itemToCheck.attemptToBuy(itemQuantity, Double.MAX_VALUE);

                if (qPurchased == 0) {
                    startedStealing = true;
                    increaseList();
                }

                if (!startedStealing) {
                    cash -= qPurchased * currentItem.getPrice();
                }
            }
            
            i++;
            done = i >= shoppingList.size() || cash <= getMinimumPrice();            
        }
        int j = 0;
        while(j < shoppingList.size()) {
            if(shoppingList.get(j).getItemQuantity() <= 0) {
                shoppingList.remove(j);
                j++;
            }else{
                j++;
            }
        }

        try{
            sleep(ONE_HUNDRED);
        } catch (InterruptedException e){
            System.err.println(e);
        } 

    }

    public boolean isStealing()
    {
        return startedStealing;
    }

    public void increaseJailProb()
    {
        if(jailedProb < SENT_TO_JAIL)
        {
            jailedProb += INCREASE_PROB;
        }
        else
        {
            done = true;
            jail.getArrested(this);
            supermarketManager.removeShopper(shopperNumber);
        }
    }

    @Override
    public String toString(){

        return
        "\n\nKristi's Shopping List\n" +
        "Shopper number " + shopperNumber + "\n" +
        "Cash left in wallet: " + cash + "\n" +
        "Items the shopper was unable to purchase:\n" +
        shoppingList.toString();

    }
}
