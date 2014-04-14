package Project;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class PasswordPanel extends JFrame implements ActionListener{
	private static final long serialVersionUID = 8343171159981586851L;
	private String password = "";
	private JPasswordField tField_Password;
	/**
	 * This is a simple JFrame that prompts the user for a password.
	 */
	PasswordPanel() {
		
		JLabel label_Password = new JLabel("Password");
		tField_Password = new JPasswordField(10);
		tField_Password.addActionListener(this);
		this.add(label_Password, BorderLayout.WEST);
		this.add(tField_Password, BorderLayout.CENTER);
		this.pack();
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setVisible(true);
		
	}
	/**
	 * Gets the password the user entered
	 * @return The password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * Action lisener
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o.equals(tField_Password)){
			char[] text = tField_Password.getPassword();
			password = new String(text);
			this.setVisible(false);
		}
	}
}
