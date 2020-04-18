// going to be lazy about imports in this class
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;

/**
 * This game is loosely based off of a tower defense game. In this game, 
 * you must defend your tower against the invading zombie army. Your 
 * weapons of choice: explosives and boulders? You must shoot all of the
 * incoming zombies with your boulders, grenades, molotov cocktails, and 
 * your trusty tnt. The user may choose from three difficulty levels for
 * enemy waves and spawn as many enemies as you'd like. However, once 
 * your tower falls, you'll be exposed for the loser you are.
 *
 * @author Kristi Boardman, Cameron Costello, Will Skelly, Jake Burch
 * @version Spring 2020
 */
public class TowerDefense extends MouseAdapter implements Runnable, ActionListener
{
    //THE DELAY TIME FOR THE SLEEP METHOD
    private static final int DELAY_TIME = 33;
    
    //THE WIDTH AND HEIGHT OF THE PANEL
    private static final int PANEL_WIDTH = 1250;
    private static final int PANEL_HEIGHT = 700;

    //THE WIDTH AND HEIGHT OF THE FRAME
    private static final int FRAME_WIDTH = 1250;
    private static final int FRAME_HEIGHT = 800;

    //THE AMOUNT THE TOWER WILL BE DISPLACED FROM THE WIDTH AND HEIGHT OF THE PANEL
    private static final int TOWER_X_DISPLACEMENT = 325;
    private static final int TOWER_Y_DISPLACEMENT = 475;

    //THE START HEALTH OF THE TOWER
    private static final int START_HEALTH = 15;

    //THE THREE LEVELS OF THE GAME
    private static final int EASY = 0;
    private static final int MEDIUM = 1;
    private static final int HARD = 2;

    //THE SLING FACTOR FOR THROWING THE WEAPONS
    public static final double SLING_FACTOR = 2.5;

    //THE FILENAME OF THE TOWER IMAGE
    private static final String towerPicFilename = "towerImage.png";

    //THE COLORS FOR THE TOWER HEALTH LABEL AND THE DIFFICULTY BUTTONS
    private static final Color FULL_HEALTH = new Color(13, 201, 6);
    private static final Color MED_HEALTH = new Color(245, 243, 110);
    private static final Color LOW_HEALTH = new Color(176, 41, 32);

    //THE GRASS COLOR BASED ON WHETHER IT IS DAY OR NIGHT
    private static final Color DAY_GRASS = new Color(42, 153, 32);
    private static final Color NIGHT_GRASS = new Color(29, 112, 87);

    //THE SKY COLOR BASED ON WHETHER IT IS DAY OR NIGHT
    private static final Color DAY_SKY = new Color(73, 185, 230);
    private static final Color NIGHT_SKY = new Color(62, 53, 150);

    //THE FONTS TO USE THROUHGOUT THE PROGRAM FOR BUTTONS AND LABELS
    private static final Font FONT_USED = new Font("Rockwell", Font.BOLD, 20);
    private static final Font LARGER_FONT_USED = new Font("Rockwell", Font.BOLD, 50);

    //THE POSITION OF THE TOWER ON THE PANEL
    private int towerXPos;
    private int towerYPos;

    //THE Y VALUE OF THE GRASS LINE POSITION
    private int grassLine;

    //THE AMOUNT OF HEALTH THE TOWER HAS
    private int towerHealth;

    //FALSE WHEN IT IS DAYTIME, TRUE WHEN IT IS NIGHTTIME ON SCREEN
    private boolean nightTime;

    //HAS THE GAME STARTED OR NOT?
    private boolean gameStarted;

    //IS THE MOUSE BEING DRAGGED OR NOT?
    private boolean dragging;

    //THE IMAGE OF THE TOWER
    private static Image towerPic;

    //THE VECTORS THAT HOLD THE SOLDIER ARMY OR THE WEAPONS ON SCREEN
    private Vector<SoldierArmy> soldierArmyList;
    private Vector<Weapon> weaponList;

    //THE GAME PANEL (WHICH HOLDS THE GAME PLAY) OR THE START PANEL (WHICH HOLDS THE BUTTONS)
    private JPanel panel;
    private JPanel startPanel;

    //THE BUTTONS INDICATION THE LEVEL OF DIFFICULTY FOR THE GAME
    private JButton easyRound;
    private JButton mediumRound;
    private JButton hardRound;

    //THE BUTTON THAT STARTS THE GAME OR RESTARTS THE GAME SETTINGS
    private JButton startOrRestart;

    //THE BUTTON THAT SAVES THE SCORE
    private JButton score;

    //THE LABEL THAT DISPLAYS THE AMOUNT OF HEALTH THE TOWER HAS LEFT
    private JLabel healthBar;

    //THE SCOREBOARD OBJECT THAT WILL STORE THE SCORE IN THE DATABASE
    private Scoreboard scoreboard;

    //THE POINTS THAT KEEP TRACK OF PRESSING AND DRAGGING TO LAUNCH THE WEAPONS
    private Point pressPoint;
    private Point dragPoint;

    // OBJECTS THAT SERVVE AS LOCKS FOR THREAD SAFETY IN OUR LIST ACCESS
    private Object weaponLock = new Object();
    private Object soldierLock = new Object();
    private Object healthLock = new Object();

    /**
     * This method will repaint our background scene and the tower. It will also ensure that all enemies 
     * and weapons are repainted on the screen.
     * 
     * @param g The graphics object.
     */
    protected void redrawScene(Graphics g) {
        //SAVE THE WIDTH AND HEIGHT OF THE GAME PANEL FOR PROPER SPACING OF COMPONENETS
        int width = panel.getWidth();
        int height = panel.getHeight();

        //PLACE THE GRASSLINE 3/4 OF THE WAY DOWN THE SCREEN
        grassLine = height/4;

        //CREATE THE POSITION FOR THE TOWER BASED ON THE PANEL AND THE DISPLACEMENT
        towerXPos = width - TOWER_X_DISPLACEMENT;
        towerYPos = height - TOWER_Y_DISPLACEMENT;

        //PAINT THE BACKGROUND AS NIGHT OR AS DAY
        if(!nightTime) {
            g.setColor(DAY_SKY);
            g.fillRect(0, 0, width, height - grassLine);

            g.setColor(DAY_GRASS);
            g.fillRect(0, height - grassLine, width, height);
        } else {
            g.setColor(NIGHT_SKY);
            g.fillRect(0, 0, width, height - grassLine);

            g.setColor(NIGHT_GRASS);
            g.fillRect(0, height - grassLine, width, height);
        }

        //DRAW THE TOWER IN THE GRASS
        g.drawImage(towerPic, towerXPos, towerYPos, null);

        // if we are currently dragging, draw a sling line
        //IF WE ARE CURRENTLY DRAGGING, DRAW THE SLING LINE
        if (dragging) {
            g.setColor(Color.BLACK);
            g.drawLine(pressPoint.x, pressPoint.y,
                dragPoint.x, dragPoint.y);
        }

        //COLOR THE TOWER HEALTH MESSAGE BASED ON AMOUNT OF HEALTH LEFT
        if(towerHealth >= 2 * START_HEALTH / 3) {
            healthBar.setForeground(FULL_HEALTH);
            healthBar.setText("Tower Health: " + towerHealth);
        } else if(towerHealth > START_HEALTH / 3) {
            healthBar.setForeground(MED_HEALTH);
            healthBar.setText("Tower Health: " + towerHealth);
        } else {
            healthBar.setForeground(LOW_HEALTH);
            healthBar.setText("Tower Health: " + towerHealth);
        }

        //USE THIS LOCAL VARIABLE FOR INDEXING
        int i = 0;

        //REDRAW EACH SOLDIER AT ITS CURRENT POSITION AND REMOVE THE ONES THAT ARE DONE ALONG THE WAY
        //SINCE WE WILL BE MODIFYING THE LIST, WE WILL LOCK ACCESS SO THAT NO CONCURRENT EXCEPTION WILL OCCUR
        synchronized (soldierLock) {
            while (i < soldierArmyList.size()) {
                SoldierArmy s = soldierArmyList.get(i);

                if (s.done()) {
                    soldierArmyList.remove(i);
                }
                else {
                    s.paint(g);
                    s.setWeaponList(weaponList);
                    i++;
                }
            }
        }

        //RESET INDEX TO ZERO
        i = 0;
        
        //REDRAW EACH WEAPON AT ITS CURRENT POSITION AND REMOVE THE ONES THAT ARE DONE ALONG THE WAY
        //SINCE WE WILL BE MODIFYING THE LIST, WE WILL LOCK ACCESS SO THAT NO CONCURRENT EXCEPTION WILL OCCUR
        synchronized (weaponLock) {
            while (i < weaponList.size()) {
                Weapon w = weaponList.get(i);
                if (w.done()) {
                    weaponList.remove(i);
                }
                else {
                    w.paint(g);
                    i++;
                }
            }
        }

        //IF THE START BUTTON WAS PRESSED AND THE TOWER IS NOW 
        if( gameStarted && towerHealth <= 0) {
            int centerX = width/2;
            int centerY = height/2;

            g.setFont(LARGER_FONT_USED);

            FontMetrics fontInfo = g.getFontMetrics();
            centerX = centerX - (fontInfo.stringWidth("You're a loser.")/2);
            centerY = centerY - (fontInfo.getAscent()/2);

            g.setColor(Color.MAGENTA);
            g.drawString("You're a loser.", centerX, centerY);

        }
    }

    /**
     * This method creates the frame, the panels, the labels, and the buttons. The scene gets
     * painted and repainted based on a delay time. All action listeners are added to the
     * buttons and all buttons and labels except start are invisible upon startup of the game.
     * 
     */
    @Override
    public void run() {
        //SET UP THE GUI "LOOK AND FEEL" WHICH SHOULD MATCH THE OS ON WHICH IT IS RUN
        JFrame.setDefaultLookAndFeelDecorated(true);

        //CREATE A JFRAME IN WHICH WE WILL BUILD OUR GUI AND GIVE THE WINDOW A NAME
        JFrame frame = new JFrame("TowerDefenseGame");
        frame.setPreferredSize(new Dimension(FRAME_WIDTH,FRAME_HEIGHT));

        //TELL THE JFRAME THAT WHEN SOMEONE CLOSES THE WINDOW, THE APPLICATION SHOULD TERMINATE
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //CREATE THE START BUTTON
        startOrRestart = new JButton("Start");

        //CREATE THE SCORE BUTTON
        score = new JButton("Save score");

        //CREATE THE DIFFICULTY LEVEL BUTTONS
        easyRound = new JButton("Easy");      
        mediumRound = new JButton("Medium");
        hardRound = new JButton("Hard");

        //CREATE THE LABEL THAT DISPLAYS THE HEALTH
        healthBar = new JLabel();

        //CREATE A PANEL WITH A FLOWLAYOUT TO HOLD THE GAME AND THE BUTTONS PANEL AND ADD IT TO THE FRAME
        JPanel panelHolder = new JPanel(new FlowLayout());
        frame.add(panelHolder);

        // JPANEL WITH A PAINTCOMPONENT METHOD
        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                //CALL THE PAINTCOMPONENT METHOD WE ARE OVERRIDING IN JPANEL
                super.paintComponent(g);

                //REDRAW THE MAIN SCENE
                redrawScene(g);
            }

        };

        //CREATE THE SCOREBOARD OBJECT AND START THE THREAD
        scoreboard = new Scoreboard(panel);
        scoreboard.start();

        //CREATE A PANEL FOR THE GAME
        panel.setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
        
        //CREATE A PANEL FOR THE BUTTONS AND SET THE COLOR TO BLACK
        startPanel = new JPanel();
        startPanel.setBackground(Color.BLACK);

        //SET THE FONT AND COLORS OF THE START BUTTON
        startOrRestart.setFont(FONT_USED);
        startOrRestart.setForeground(Color.CYAN);
        startOrRestart.setBackground(Color.BLACK);

        //SET THE FONT AND COLORS OF THE EASY BUTTON
        easyRound.setFont(FONT_USED);
        easyRound.setForeground(FULL_HEALTH);
        easyRound.setBackground(Color.BLACK);

        //SET THE FONT AND COLORS OF THE MEDIUM BUTTON
        mediumRound.setFont(FONT_USED);
        mediumRound.setForeground(MED_HEALTH);
        mediumRound.setBackground(Color.BLACK);

        //SET THE FONT AND COLORS OF THE HARD BUTTON
        hardRound.setFont(FONT_USED);
        hardRound.setForeground(LOW_HEALTH);
        hardRound.setBackground(Color.BLACK);

        //SET THE FONT OF THE HEALTH LABEL
        healthBar.setFont(FONT_USED);
        
        
        //SET THE FONT AND COLORS OF THE SCORE BUTTON
        score.setFont(FONT_USED);
        score.setForeground(Color.MAGENTA);
        score.setBackground(Color.BLACK);

        //ADD THE START BUTTON TO THE BUTTON PANEL
        startPanel.add(startOrRestart);

        //ADD THE DIFFICULTY BUTTONS TO THE BUTTON PANEL
        startPanel.add(easyRound);
        startPanel.add(mediumRound);
        startPanel.add(hardRound);

        //ADD THE HEALTH LABEL TO THE BUTTON PANEL
        startPanel.add(healthBar);

        //ADD THE SCORE BUTTON TO THE BUTTON PANEL
        startPanel.add(score);

        //SET DIFFICULTY BUTTONS INVISIBLE
        easyRound.setVisible(false);
        mediumRound.setVisible(false);
        hardRound.setVisible(false);

        //SET SCORE BUTTON INVISIBLE
        score.setVisible(false);

        //SET HEALTH LABEL INVISIBLE
        healthBar.setVisible(false);

        //ADD THE GAME PANEL AND THE BUTTONS PANEL TO THE PANEL HOLDER ON THE JFRAME
        panelHolder.add(panel);
        panelHolder.add(startPanel);

        //ADD THE MOUSELISTENER TO THE GAME PANEL
        panel.addMouseListener(this);

        //ADD THE ACTION LISTENER TO THE START BUTTON
        startOrRestart.addActionListener(this);

        //ADD THE ACTION LISTENER TO THE DIFFICULTY BUTTONS
        easyRound.addActionListener(this);
        mediumRound.addActionListener(this);
        hardRound.addActionListener(this);

        //ADD THE ACTION LISTENER TO THE SCORE BUTTON
        score.addActionListener(this);

        //INITIALIZE THE WEAPONS AND ENEMY LISTS
        weaponList = new Vector<Weapon>();
        soldierArmyList = new Vector<SoldierArmy>();

        //DISPLAY THE WINDOW THAT WE CREATED
        frame.pack();
        frame.setVisible(true);

        //REPAINT THE SCENE AFTER A SET AMOUNT OF TIME
        new Thread() {
            @Override
            public void run() {
                while(true){
                    try{
                        sleep(DELAY_TIME);
                    } catch (InterruptedException e){
                        System.out.print(e);
                    }

                    panel.repaint();
                }
            }
        }.start(); 
    }

    
    /**
     * The action listener that handles events for each button.
     * Start button will start the game. Reset button will
     * reset all variables and reset screen so that game can be
     * started again. Difficulty buttons will begin enemy spawning.
     * Score button allows score to be saved.
     * 
     * @param e The action event to handle.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        //WHEN THE BUTTON PRESSED IS THE START OR RESTART BUTTONS
        if(e.getSource().equals(startOrRestart)) {
            //IF THE BUTTON CURRENTLY SAYS START (WHEN THE GAME BEGINS)
            if(startOrRestart.getText().equals("Start")) { 
                //THE GAME HAS STARTED
                gameStarted = true;

                //THE TOWER STARTS WITH MAXIMUM HEALTH
                towerHealth = START_HEALTH;

                //THE DIFFICULTY BUTTONS BECOME VISIBLE
                easyRound.setVisible(true);
                mediumRound.setVisible(true);
                hardRound.setVisible(true);

                //THE AMOUNT OF HEALTH LEFT BECOMES VISIBLE
                healthBar.setVisible(true);

                //THE SCORE BUTTON BECOMES VISIBLE
                score.setVisible(true);

                //THE START BUTTON BECOMES THE RESTART BUTTON
                startOrRestart.setText("Restart");
            } else {
                //IF THE BUTTON CURRENTLY SAYS RESTART (WHEN THE GAME HAS ALREADY BEGUN OR HAS ENDED)
                
                //END THE GAME
                gameStarted = false;

                //RESET THE BUTTON TO SAY START
                startOrRestart.setText("Start");

                //THE DIFFICULTY BUTTONS ARE HIDDEN
                easyRound.setVisible(false);
                mediumRound.setVisible(false);
                hardRound.setVisible(false);

                //THE AMOUNT OF HEALTH LEFT IS HIDDED / DOESN'T EXIST
                healthBar.setVisible(false);

                //THE SCORE BUTTON IS HIDDEN
                score.setVisible(false);

                //CLEAR THE SCREEN OF ANY WEAPONS OR ENEMIES
                weaponList.clear();
                soldierArmyList.clear();
            }
        }

        //IF THE PLAYER CHOOSES AN EASY ROUND
        if (e.getSource().equals(easyRound))
        {
            //AN EASY TO DEFEAT ARMY WILL BE CREATED, ADDED TO THE LIST, AND STARTED 
            SoldierArmy easy = new SoldierArmy(EASY, panel, this);
            soldierArmyList.add(easy);
            easy.start();
        }

        //IF THE PLAYER CHOOSES A MEDIUM ROUND
        if (e.getSource().equals(mediumRound))
        {
            //A MEDIUM LEVEL ARMY WILL BE CREATED, ADDED TO THE LIST, AND STARTED 
            SoldierArmy medium = new SoldierArmy(MEDIUM, panel, this);
            soldierArmyList.add(medium);
            medium.start();
        }

        //IF THE PLAYER CHOOSES A HARD ROUND
        if (e.getSource().equals(hardRound))
        {
            //A HARD TO DEFEAT ARMY WILL BE CREATED, ADDED TO THE LIST, AND STARTED 
            SoldierArmy hard = new SoldierArmy(HARD, panel, this);
            soldierArmyList.add(hard);
            hard.start();
        }

        //IF THE SCORE IS TO BE SAVED
        if(e.getSource().equals(score)) {
            //THE DIALOG BOX WILL POP UP TO SAVE THE SCORE
            scoreboard.show();
        }
    }

    
    /**
     * The mouse press event that will set up a weapon
     * to be launched upon release, if the game has begun.
     * 
     * @param e The mouse event to handle.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //IF THE START BUTTON HAS BEEN PRESSED
        if(gameStarted) {
            //SAVE THE ORIGINAL PRESS POINT
            pressPoint = e.getPoint();
        }
    }

    /**
     * The mouse drag event that will set up a weapon
     * to be launched upon release, if the game has begun.
     * Will draw a sling line.
     * 
     * @param e The mouse event to handle.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        //IS THE START BUTTON HAS BEEN PRESSED
        if(gameStarted) {
            //SAVE THE DRAG POINT FOR PAINT METHOD
            dragPoint = e.getPoint();
            
            //SET DRAG TO FALSE FOR PAINT METHOD
            dragging = true;
        }
    }

    /**
     * The mouse release event that will create a new weapon
     * and add it to the list if the game has begun.
     * 
     * @param e The mouse event to handle.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        //AS LONG AS THE START BUTTON HAS BEEN PRESSED
        if(gameStarted) {
            //CREATE A NEW WEAPON TO ADD TO THE WEAPON LIST
            Weapon newWeapon = QuarterMaster.getRandomWeapon(panel,
                    new Point2D.Double(e.getPoint().x , e.getPoint().y),
                    new Point2D.Double( 
                        SLING_FACTOR * (pressPoint.x - e.getPoint().x) , 
                        SLING_FACTOR * (pressPoint.y - e.getPoint().y) 
                    ) );

            //LOCK ACCESS TO THE LIST IN CASE paintComponent IS USING IT CONCURRENTLY
            synchronized (weaponLock) {
                //ADD THE NEW WEAPON TO THE LIST
                weaponList.add(newWeapon);
            }
            
            //START THE NEW WEAPON NOW THAT IT HAS BEEN ADDED
            newWeapon.start();
            
            //RESET DRAGGING TO FALSE FOR PAINT METHOD
            dragging = false;
        }
    }

    /**
     * Updates the tower health based on enemy damage.
     * 
     * @param numDamage The amount of damage done to the tower.
     */
    public void modifyTowerHealth(int numDamage) {
        //TOWER HEALTH WILL UPDATE AS ENEMIES ATTACK THE TOWER
        synchronized (healthLock) {
            //TOWER HEALTH CAN HAVE A MINUMUM OF ZERO
            towerHealth = (towerHealth - numDamage < 0 ? 0 : towerHealth - numDamage);
        }
    }

    /**
     * The main method for executing the GUI.
     * 
     * @param args Not used.
     */
    public static void main(String[] args) {
        //THIS WILL PRINT OUT ANY ERRORS IN THE ERROR LOG TEXT FILE
        try{
            System.setErr(new PrintStream("error_log.txt"));
        }catch(FileNotFoundException e){
            System.err.println("File Not Found: " + e);
        }

        //CREATES THE TOWER IMAGE
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        towerPic = toolkit.getImage(towerPicFilename);

        //LAUNCH THE MAIN THREAD THAT WILL MANAGAE THE GUI
        javax.swing.SwingUtilities.invokeLater(new TowerDefense());
    }

}
