import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Dimension;
import java.io.IOException;

public class WebIndexPanel extends JPanel{
	private URLEntryPanel urlEntryPanel;	// a panel for user to input urls they want to browse
	private QueryEntryPanel queryEntryPanel;	// a panel for user to input queries they want to search
	private QueryResultsViewPanel queryResultsViewPanel;	// a panel displaying search results
	
	public WebIndexPanel(){
		setLayout(new BorderLayout(20, 0));
		urlEntryPanel = new URLEntryPanel();
		queryEntryPanel = new QueryEntryPanel();
		queryResultsViewPanel = new QueryResultsViewPanel();
		add(urlEntryPanel, BorderLayout.NORTH);
		add(queryEntryPanel, BorderLayout.WEST);
		add(queryResultsViewPanel, BorderLayout.CENTER);
	}
	
	public URLEntryPanel getURLEntryPanel(){
		return urlEntryPanel;
	}
	
	public QueryEntryPanel getQueryEntryPanel(){
		return queryEntryPanel;
	}
	
	public QueryResultsViewPanel getQueryResultsViewPanel(){
		return queryResultsViewPanel;
	}
}