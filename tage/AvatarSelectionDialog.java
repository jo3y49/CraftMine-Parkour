package tage;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AvatarSelectionDialog extends JDialog implements ActionListener
{
    private String selectedAvatar = null;
    private JComboBox<String> avatarComboBox;

    public AvatarSelectionDialog ()
    {
        setTitle("Choose an Avatar");
        setSize(300, 200);
        setLocation (200,200);
        setResizable(true);
        doMyLayout();

        // make the dialog modal, so that 'show' will block until dialog is dismissed
        setModal(true);
    }

    private void doMyLayout()
    {
        setLayout (new BorderLayout());

        // Create a panel containing a drop-down list of available avatars
        JPanel avatarPanel = new JPanel();
        avatarPanel.setBorder(BorderFactory.createTitledBorder("Choose an avatar:"));

        // List of avatar names. This could be replaced with actual images.
        String[] avatars = {"Avatar 1", "Avatar 2", "Avatar 3", "Avatar 4"};

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

        newButton = new JButton("Cancel");
        newButton.setActionCommand( "Cancel" );
        buttonPanel.add(newButton);
        this.add(buttonPanel, "South");
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("OK"))
        {
            // Get the avatar selected by the user
            selectedAvatar = (String) avatarComboBox.getSelectedItem();
        }

        setVisible(false);
    }

    public String getSelectedAvatar()
    {
        return selectedAvatar;
    }

    public void showIt()
    {
        this.setVisible(true);
    }
}
