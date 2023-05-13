package tage;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AvatarSelectionDialog extends JDialog implements ActionListener
{
    private String selectedAvatar = null;
    private String[] avatarPaths = {"assets/textures/Candle.png", "Avatar 2", "Avatar 3", "Avatar 4"};
    private ImageIcon[] avatars;
    private JComboBox<ImageIcon> avatarComboBox;
    private int selectedIndex = 0;

    public AvatarSelectionDialog ()
    {
        setTitle("Choose an Avatar");
        setSize(1000, 800);
        setLocation (200,0);
        setResizable(true);
        doMyLayout();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);
    }

    private void doMyLayout()
    {
        setLayout (new BorderLayout());

        // Create a panel containing a drop-down list of available avatars
        JPanel avatarPanel = new JPanel();
        avatarPanel.setBorder(BorderFactory.createTitledBorder("Choose an avatar:"));

        // Initialize the avatars array
        avatars = new ImageIcon[avatarPaths.length];

        for (int i = 0; i < avatarPaths.length; i++) {
            ImageIcon unscaledIcon = new ImageIcon(avatarPaths[i]);
            Image image = unscaledIcon.getImage(); // transform it 
            Image newimg = image.getScaledInstance(250, 250, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
            avatars[i] = new ImageIcon(newimg);  // transform it back
        }

        // Add a combo box containing the available avatars
        avatarComboBox = new JComboBox<>(avatars);
        avatarPanel.add(avatarComboBox);

        this.add(avatarPanel, BorderLayout.CENTER);

        //add a bottom panel containing control buttons (OK, Cancel)
        JPanel buttonPanel = new JPanel();
        JButton newButton = new JButton("OK");
        newButton.setActionCommand( "OK" );
        newButton.addActionListener(this);
        buttonPanel.add(newButton);

        
        this.add(buttonPanel, "South");
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("OK"))
        {
            // Get the avatar selected by the user
            selectedIndex = avatarComboBox.getSelectedIndex(); // Get the index of the selected item
        }

        setVisible(false);
    }

    public int getSelectedAvatar()
    {
        return selectedIndex;
    }

    public void showIt()
    {
        this.setVisible(true);
    }
}
