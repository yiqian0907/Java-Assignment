import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class URLEntryPanel extends JPanel{
	
	private JButton back;	//button for backing to last page
	private JButton forward;	//button for going yo next page
	private JLabel urlLabel;
	private JTextField urlEntryField;
	private JButton browseBtn;	
	
	public URLEntryPanel(){
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		back = new JButton("Pre");
		back.setEnabled(false);
		forward = new JButton("Next");
		forward.setEnabled(false);
		urlLabel = new JLabel("Enter URL: ");
		urlEntryField = new JTextField(50);
		browseBtn = new JButton("BROWSE");
		add(Box.createGlue());
		add(back);
		add(forward);
		add(urlLabel);
		add(urlEntryField);
		add(browseBtn);
	}
	
	public String getURL(){
		return urlEntryField.getText();
	}
	
	public JButton getBackBtn(){
		return back;
	}
	
	public JButton getForwardBtn(){
		return forward;
	}
	
	public void addBrowseListener(ActionListener listenerForBrowseBtn){
		browseBtn.addActionListener(listenerForBrowseBtn);
	}
	
	public void addBackListener(ActionListener listenerForBackBtn){
		back.addActionListener(listenerForBackBtn);
	}
	
	public void addForwardListener(ActionListener listenerForForwardBtn){
		forward.addActionListener(listenerForForwardBtn);
	}
}
