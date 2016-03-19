package diskong.ihm;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import diskong.old.DiscogClient;

public class SearchFrame extends JFrame {

	private JPanel contentPane;
	private JTextField query;
	private DiscogClient app;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SearchFrame frame = new SearchFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SearchFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 932, 616);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Query");
		lblNewLabel.setBounds(6, 39, 91, 33);
		contentPane.add(lblNewLabel);
		
		query = new JTextField();
		query.setBounds(80, 41, 495, 30);
		contentPane.add(query);
		query.setColumns(10);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				app.search(query.getText());
			}
		});
		btnNewButton.setBounds(469, 102, 
				106, 28);
		contentPane.add(btnNewButton);
		app = new DiscogClient();
	}
}
