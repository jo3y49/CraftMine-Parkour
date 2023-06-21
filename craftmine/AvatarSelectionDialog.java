package craftmine;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class AvatarSelectionDialog extends JDialog implements ActionListener
{
    private String[] avatarPaths = {"assets/textures/avatar1.png", "assets/textures/avatar2.png", "assets/textures/avatar3.png"};
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
        avatarPanel.setBorder(BorderFactory.createTitledBorder(null, "Choose an avatar:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("times new roman",Font.BOLD,24), Color.BLACK));

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
        newButton.setFont(new Font("Arial", Font.BOLD, 20));

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
