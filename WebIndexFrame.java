import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WebIndexFrame extends JFrame{
	private WebIndexPanel webIndexPanel;
	public WebIndexFrame(){
		setTitle("Web Indexing System");
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		Dimension frameSize = new Dimension(screenSize.width/2, screenSize.height/2);
		setSize(screenSize.width/2, screenSize.height/2);
		Container cpane = getContentPane();
		webIndexPanel = new WebIndexPanel();
		webIndexPanel.setPreferredSize(frameSize);
		cpane.add(webIndexPanel);
	}
	
	public WebIndexPanel getWebIndexPanel(){
		return webIndexPanel;
	}
}