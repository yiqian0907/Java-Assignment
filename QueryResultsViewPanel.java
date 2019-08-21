import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class QueryResultsViewPanel extends JPanel{
	private JPanel urlResultsPanel;	// a panel displaying the found url results based on the queries users inputted
	private ViewHTMLPanel viewHTMLPanel;	// a panel showing the HTML page users want to browse
	
	public QueryResultsViewPanel(){
		GridLayout gridLayout = new GridLayout(2,1,0, 10);
		setLayout(gridLayout);
		urlResultsPanel = new JPanel();
		urlResultsPanel.setLayout(new GridLayout(0, 1));
		urlResultsPanel.setBackground(Color.WHITE);
		viewHTMLPanel = new ViewHTMLPanel();
		add(urlResultsPanel);
		add(viewHTMLPanel);
	}
	
	public JPanel getURLResultsPanel(){
		return urlResultsPanel;
	}
	
	public JEditorPane getDisplayHTMLPane(){
		return viewHTMLPanel.getDisplayHTML();
	}
	
	private class ViewHTMLPanel extends JPanel{
		private JEditorPane displayHTML;
		public ViewHTMLPanel(){
			setLayout(new GridLayout(1, 1));
			displayHTML = new JEditorPane();
			displayHTML.setEditable(false);
			JScrollPane editorScrollPane = new JScrollPane(displayHTML);
			add(editorScrollPane);
		}
		
		public JEditorPane getDisplayHTML(){
			return displayHTML;
		}
	}
}