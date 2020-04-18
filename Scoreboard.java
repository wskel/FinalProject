// going to be lazy about imports in this class
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;

/**
 * Write a description of class TowerDefense here.
 *
 * @author Kristi Boardman, Cameron Costello, Will Skelly, Jake Burch
 * @version Spring 2020
 */
public class Scoreboard extends Thread implements ActionListener
{
    JDialog saveScoreDialog;
    JDialog addFriendDialog;
    JDialog addPlayerDialog;
    int score;
    JLabel yourScoreLabel;
    JTextField newPlayerName;
    JComboBox<String> yourNameSelect;
    JComboBox<String> friendNameSelect;
    JButton saveScoreButton;
    JButton addPlayerButton;
    JButton saveNameButton;
    JButton addFriendButton;
    JButton exitButton;
    JButton cancelAddPlayerButton;
    JButton cancelAddFriendButton;
    JComponent container;

    public Scoreboard(JComponent container){
        this.container = container;
        this.score = 0;
    }

    public void run(){
        DatabaseDriver.fetchDatabase(new File("createSampleDatabase.txt"));
        
        //dialog box for adding a highscore
        saveScoreDialog = new JDialog();
        saveScoreDialog.setTitle("Add New Highscore");
        saveScoreDialog.setSize(new Dimension(500,500));
        JPanel savePanel = new JPanel();
        savePanel.add(new JLabel("Select Your Player Name: "));
        yourNameSelect = new JComboBox<String>(DatabaseDriver.getAllPlayers());
        savePanel.add(yourNameSelect);
        yourScoreLabel = new JLabel("Your Highscore: 0");
        savePanel.add(yourScoreLabel);
        saveScoreButton = new JButton("Save Highscore");
        savePanel.add(saveScoreButton);
        addPlayerButton = new JButton("Add New Player");
        savePanel.add(addPlayerButton);
        exitButton = new JButton("Exit");
        savePanel.add(exitButton);
        saveScoreDialog.add(savePanel);

        //dialog box for adding a new player
        addPlayerDialog = new JDialog();
        addPlayerDialog.setTitle("Add New Player");
        addPlayerDialog.setSize(new Dimension(500,500));
        //fields for adding a player
        JPanel addPlayerPanel = new JPanel();
        addPlayerPanel.add(new JLabel("Enter Your Name: "));
        newPlayerName = new JTextField();
        addPlayerPanel.add(newPlayerName);
        saveNameButton = new JButton("Save Player Name");
        addPlayerPanel.add(saveNameButton);
        cancelAddPlayerButton = new JButton("Cancel");
        addPlayerPanel.add(cancelAddPlayerButton);
        addPlayerDialog.add(addPlayerPanel);

        //dialog box for adding a friend
        addPlayerDialog = new JDialog();
        addPlayerDialog.setTitle("Add Friend");
        addPlayerDialog.setSize(new Dimension(500,500));
        //fields for adding a friend
        JPanel addFriendPanel = new JPanel();
        addFriendPanel.add(new JLabel("Select Your Player Name: "));
        addFriendPanel.add(yourNameSelect);
        addFriendPanel.add(new JLabel("Select Your Friend's Player Name: "));
        addFriendPanel.add(friendNameSelect);
        addFriendButton = new JButton("Add Friend");
        addFriendPanel.add(addFriendButton);
        cancelAddFriendButton = new JButton("Cancel");
        addFriendPanel.add(cancelAddFriendButton);
        addFriendPanel.add(addFriendPanel);
        
        container.add(saveScoreDialog);
        container.add(addPlayerDialog);
        container.add(addFriendDialog);
        saveScoreDialog.setVisible(false);
        addPlayerDialog.setVisible(false);
        addFriendDialog.setVisible(false);

        saveScoreButton.addActionListener(this);
        addPlayerButton.addActionListener(this);
        addFriendButton.addActionListener(this);
        exitButton.addActionListener(this);
        cancelAddPlayerButton.addActionListener(this);
        cancelAddFriendButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource().equals(saveScoreButton)){
            DatabaseDriver.setScore((String)yourNameSelect.getSelectedItem(),this.score);
        } else if(e.getSource().equals(addPlayerButton)){
            addPlayerDialog.setVisible(true);
        } else if(e.getSource().equals(saveNameButton)){
            DatabaseDriver.addPlayer(newPlayerName.getText());
            newPlayerName.setText("");
            addPlayerDialog.setVisible(false);
        } else if(e.getSource().equals(addFriendButton)){
            DatabaseDriver.addFriendship((String)yourNameSelect.getSelectedItem(),(String)friendNameSelect.getSelectedItem());
            friendNameSelect.setSelectedItem(null);
            addFriendDialog.setVisible(false);
        }else if(e.getSource().equals(exitButton)){
            friendNameSelect.setSelectedItem(null);
            yourNameSelect.setSelectedItem(null);
            saveScoreDialog.setVisible(false);
        }else if(e.getSource().equals(cancelAddPlayerButton)){
            newPlayerName.setText("");
            addPlayerDialog.setVisible(false);
        }else if(e.getSource().equals(cancelAddFriendButton)){
            friendNameSelect.setSelectedItem(null);
            addFriendDialog.setVisible(false);
        }

    }

    public void show(){
        saveScoreDialog.setVisible(true);
    }

    public void setScore(int score){
        this.score = score;
        yourScoreLabel.setText("Your Highscore: " + score);
    }
}
