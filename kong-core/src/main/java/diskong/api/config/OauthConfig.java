/*
 * Copyright 2018 org.dpr & croger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package diskong.api.config;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class OauthConfig extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private static final String PRODUCT="discog";
	/** Directory to store user credentials. */
	  private static final java.io.File DATA_STORE_FILE =
	      new java.io.File(System.getProperty("user.home"), ".oauth_store/keys");
	  
	  Properties props;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			OauthConfig dialog = new OauthConfig();
	
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.loadData();
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadData() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Create the dialog.
	 * @throws IOException 
	 */
	public OauthConfig() throws IOException {
		setBounds(100, 100, 556, 291);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		init();
		final JComboBox comboName = new JComboBox();
		comboName.setEditable(true);
		comboName.setBounds(12, 25, 174, 22);
		contentPanel.add(comboName);

		final JTextArea txtRequestUrl = new JTextArea();
		txtRequestUrl.setBounds(209, 60, 301, 22);
		contentPanel.add(txtRequestUrl);
		txtRequestUrl.setText(props.getProperty(PRODUCT+".reqUrl"));

		JLabel lblRequestUrl = new JLabel("Request URL");
		lblRequestUrl.setBounds(12, 60, 174, 22);
		contentPanel.add(lblRequestUrl);

		JLabel lblAcessUrl = new JLabel("Access URL");
		lblAcessUrl.setBounds(12, 110, 174, 22);
		contentPanel.add(lblAcessUrl);

		JLabel lblAuthprizeUrl = new JLabel("Authorize URL");
		lblAuthprizeUrl.setBounds(12, 154, 174, 22);
		contentPanel.add(lblAuthprizeUrl);

		final JTextArea textAccessUrl = new JTextArea();
		textAccessUrl.setBounds(209, 110, 301, 22);
		contentPanel.add(textAccessUrl);
		textAccessUrl.setText(props.getProperty(PRODUCT+".accessUrl"));
		final JTextArea textAutUrl = new JTextArea();
		textAutUrl.setBounds(209, 154, 301, 22);
		contentPanel.add(textAutUrl);
		textAutUrl.setText(props.getProperty(PRODUCT+".autUrl"));
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						save((String)comboName.getSelectedItem(), txtRequestUrl.getText(), textAccessUrl.getText(),
								textAutUrl.getText());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		
			JButton cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);
		

	}

	
protected void init() throws IOException {
 props = new Properties();
 if (!DATA_STORE_FILE.exists()){
	 DATA_STORE_FILE.getParentFile().mkdirs();
	 DATA_STORE_FILE.createNewFile();
 }
	FileInputStream fis = new FileInputStream(DATA_STORE_FILE);
	
	props.load(fis);
	fis.close();

}
		
	protected void save(String valCombo, String reqUrl, String accessUrl, String autUrl) throws IOException {
	
	//	if (valCombo!=null){
			//props.setProperty("combo."+valCombo, valCombo);
			props.setProperty(PRODUCT+".reqUrl", reqUrl);
			props.setProperty(PRODUCT+".accessUrl", accessUrl);
			props.setProperty(PRODUCT+".autUrl", autUrl);
			FileOutputStream fileOut = new FileOutputStream(DATA_STORE_FILE);
			props.store(fileOut,"");
			fileOut.close();
//		}
//		if (props.getProperty("type."+selectedItem)==""){
//			
//		}

	}
}
