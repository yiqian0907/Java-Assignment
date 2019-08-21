import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.*;
import java.net.*;

/**
 *	the controller part of the MVC structure
 */
public class WebIndexController{
	private WebIndexFrame webIndexFrame;
	private WebIndex webIndex;
	private WebIndexPanel webIndexPanel;
	private Stack<String> browseHistoryBack = new Stack<>();	// store browsed urls for backing
	private Stack<String> browseHistoryForward = new Stack<>();	// store browsed urls for forwarding
	
	public WebIndexController(WebIndexFrame webIndexFrame, WebIndex webIndex){
		this.webIndexFrame = webIndexFrame;
		this.webIndex = webIndex;
		this.webIndexPanel = this.webIndexFrame.getWebIndexPanel();
		URLEntryPanel urlEntryPanel = webIndexPanel.getURLEntryPanel();
		urlEntryPanel.addBrowseListener(new BrowseListener());
		urlEntryPanel.addBackListener(new BackListener());
		urlEntryPanel.addForwardListener(new ForwardListener());
		webIndexPanel.getQueryEntryPanel().addSearchListener(new SearchListener());
	}
	
	/**
     * convert the content of all Jcomponents of prefix panel to the query as a string
     * 
     * @param prefixComponents all Jcomponents of prefix panel
     * 
     * @return a prefix query string
     */
	public String transferPrefixComponentsToQueryStr(ArrayList<JComponent> prefixComponents){
		Boolean searchable = false;
		int nestedNum = 0;
		String queryString = "";
		for(JComponent component: prefixComponents){
			if(component instanceof JComboBox){
				nestedNum++;
				queryString += ((JComboBox)component).getSelectedItem().toString();
				queryString += "(";
			}
			else{
				if(!((JTextField)component).getText().isEmpty()){
					// set the searchable to true when the JTextField has contents
					searchable = true;
					queryString += ((JTextField)component).getText();
					queryString += ",";
				}
			}
		}
		// users can search the query only after they have inputted some contents in the JTextField
		if(!searchable){
			JOptionPane.showMessageDialog(webIndexPanel, "Please input the query first", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
			return "";
		}
		queryString = queryString.substring(0, queryString.length()-1);
		queryString += ")";
		for(int i=1; i<nestedNum; i++){
			queryString+=")";
		}
		return queryString;
	}
	
	/**
     * convert the content of all Jcomponents of infix panel to the query as a string
     * 
     * @param prefixComponents all Jcomponents of infix panel
     * 
     * @return a infix query string
     */
	public String transferInfixComponentsToQueryStr(ArrayList<JComponent> infixComponents){
		Boolean searchable = false;
		String queryString="";
		for(JComponent component: infixComponents){
			if(component instanceof JComboBox){
				queryString += ((JComboBox)component).getSelectedItem().toString();
				queryString += " ";
			}else if(!((JTextField)component).getText().isEmpty()){
				searchable = true;
				queryString += ((JTextField)component).getText();
				queryString += " ";
			}
		}
		if(!searchable){
			JOptionPane.showMessageDialog(webIndexPanel, "Please input the query first", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
			return "";
		}
		return queryString;
	}
	
	/**
     * displays the HTML page according to the url
     * 
     * @param targetPane the place of displaying the HTML page
     * 
     * @param url users want to browse
     * @return true if the targetPane successfully analysed the HTML page
     */
	public Boolean browseURL(JEditorPane targetPane, String url){
		try{
			if(url.startsWith("file:")){
				new FileReader(url.substring(5));
			}else if(url.startsWith("http")){
				new InputStreamReader((new URL(url)).openStream());
			}
		}catch(MalformedURLException murle){
		}catch(FileNotFoundException fnfe){
			JOptionPane.showMessageDialog(webIndexPanel, "Cannot find the url: \""+url+"\"", "ERROR", JOptionPane.ERROR_MESSAGE);
			return false;
		}catch(IOException ioe){
		}
		try{
			targetPane.setPage(url);
			addBackHistory(url);
		}catch (IOException ioe) {
			if(url.isEmpty()){
				JOptionPane.showMessageDialog(webIndexPanel, "Please input the url first", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			JOptionPane.showMessageDialog(webIndexPanel, "Attempted to browse bad url: \""+url+"\"", "ERROR", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	/**
     * displays searched urls in the urlResultsPanel
     * 
     * @param searchedWebDocs a set of webDocs found according to the query
     */
	public void showURLResults(Set<WebDoc> searchedWebDocs){
		JEditorPane viewHTMLPane = webIndexPanel.getQueryResultsViewPanel().getDisplayHTMLPane();
		JPanel urlResultsPanel = webIndexPanel.getQueryResultsViewPanel().getURLResultsPanel();
		urlResultsPanel.removeAll();
		for(WebDoc doc: searchedWebDocs){
			String urlStr = doc.getURL();
			JLabel url = new JLabel(urlStr);
			// set the color of normal urls to blue
			url.setForeground(Color.BLUE.darker());
			url.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			url.addMouseListener(new MouseAdapter(){
				public void mouseEntered(MouseEvent e) {
					// when mouse entered in one specific url, set its' color red 
					url.setForeground(Color.RED);
				}
				public void mouseExited(MouseEvent e) {
					if(url.getName() == null){
						url.setForeground(Color.BLUE);
					}else
						// if the url has been clicked, set its' color to purple
						url.setForeground(new Color(187, 100, 170));
				}
				public void mouseClicked(MouseEvent e) {
					if(!browseURL(viewHTMLPane, urlStr))
						return;
					url.setName("browsed");
					// clear the forward urls stored in the "browseHistoryForward" and disable the forward button
					if(browseHistoryForward.size() > 0){
						browseHistoryForward.clear();
						disableForwardBtn();
					}
				}
			});
			urlResultsPanel.add(url);
		}
		urlResultsPanel.revalidate();
		urlResultsPanel.repaint();
	}
	
	//	add one history in the "browseHistoryBack"
	public void addBackHistory(String url){
		browseHistoryBack.push(url);
		JButton backBtn = webIndexPanel.getURLEntryPanel().getBackBtn();
		if(browseHistoryBack.size() >= 2 && !backBtn.isEnabled()){
			backBtn.setEnabled(true);
			backBtn.revalidate();
			backBtn.repaint();
			
		// if there is no urls left for browsing back, set the back button disabled
		}else if(browseHistoryBack.size() < 2 && backBtn.isEnabled()){
			backBtn.setEnabled(false);
			backBtn.revalidate();
			backBtn.repaint();
		}
	}
	
	//	add one history in the "browseHistoryBack" and enable the forward button
	public void addForwardHistory(String url){
		browseHistoryForward.push(url);
		JButton forwardBtn = webIndexPanel.getURLEntryPanel().getForwardBtn();
		if(!forwardBtn.isEnabled()){
			forwardBtn.setEnabled(true);
			forwardBtn.revalidate();
			forwardBtn.repaint();
		}
	}
	
	public void disableForwardBtn(){
		JButton forwardBtn = webIndexPanel.getURLEntryPanel().getForwardBtn();
		forwardBtn.setEnabled(false);
		forwardBtn.revalidate();
		forwardBtn.repaint();
	}
	
	//	listener for browse button in the URLEntryPanel
	private class BrowseListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			JEditorPane displayHTMLPane = webIndexPanel.getQueryResultsViewPanel().getDisplayHTMLPane();
			String url = webIndexPanel.getURLEntryPanel().getURL();
			if(!browseURL(displayHTMLPane, url)){
				return;
			}
			displayHTMLPane.revalidate();
			displayHTMLPane.repaint();
			// clear the forward urls stored in the "browseHistoryForward" and disable the forward button
			if(browseHistoryForward.size() > 0){
				browseHistoryForward.clear();
				disableForwardBtn();
			}	
			webIndex.add(new WebDoc(url));	// add the particular WebDoc to the index
		}
	}
	
	//	listener for back button in the URLEntryPanel
	private class BackListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			// pop two urls stored in "browseHistoryBack", one is the leaved one and another is the target
			String leavedURL = browseHistoryBack.pop();
			String targetURL = browseHistoryBack.pop();
			JEditorPane displayHTMLPane = webIndexPanel.getQueryResultsViewPanel().getDisplayHTMLPane();
			//	browse the url
			//	if failure to displaying the target HTML page, push the two history back to the "browseHistoryBack"  
			if(!browseURL(displayHTMLPane, targetURL)){
				browseHistoryBack.push(targetURL);
				browseHistoryBack.push(leavedURL);
			}else{
				//	if success, push the leaved url to the "browseHistoryForward"
				addForwardHistory(leavedURL);
			}
		}
	}
	
	// listener for forward button in the URLEntryPanel
	private class ForwardListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String targetURL = browseHistoryForward.pop();
			JEditorPane displayHTMLPane = webIndexPanel.getQueryResultsViewPanel().getDisplayHTMLPane();
			//if failure to displaying the target HTML page, push the history back to the "browseHistoryForward" and return
			if(!browseURL(displayHTMLPane, targetURL)){
				browseHistoryForward.push(targetURL);
				return;
			}
			// if there is no urls left for browsing forward, set the forward button disabled
			if(browseHistoryForward.size() <= 0){
				disableForwardBtn();
			}
		}
	}
	
	// listener for search button in the QueryEntryPanel
	private class SearchListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			QueryEntryPanel queryEntryPanel = webIndexPanel.getQueryEntryPanel();
			ArrayList<JComponent> components = new ArrayList<>();
			String queryString="";
			Query query = null;
			try{
				String queryMode = queryEntryPanel.getQueryModeVal();
				if(queryMode.equals("prefix")){
					components = queryEntryPanel.getPrefixComponents();
					queryString = transferPrefixComponentsToQueryStr(components);
					if(queryString.isEmpty())
						return;
					query = QueryBuilder.parse(queryString);
				}else if(queryMode.equals("infix")){
					components = queryEntryPanel.getInfixComponents();
					queryString = transferInfixComponentsToQueryStr(components);
					if(queryString.isEmpty())
						return;
					query = QueryBuilder.parseInfixForm(queryString);
				}else{
					queryString = queryEntryPanel.getManuallyInputedField().getText();
					if(queryString.isEmpty()){
						JOptionPane.showMessageDialog(webIndexPanel, "Please input the query first", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					queryString = queryString.toLowerCase();
					if(((queryString.startsWith("and") || queryString.startsWith("or") || queryString.startsWith("not")) && queryString.contains("(")) || !queryString.contains(" "))
						query = QueryBuilder.parse(queryString);
					else
						query = QueryBuilder.parseInfixForm(queryString);
				}
			}catch (IllegalQueryStringException iqse){
				JOptionPane.showMessageDialog(webIndexPanel, 
											iqse+"\n\nPlease input the legal query and try again",
											"Alert", 
											JOptionPane.ERROR_MESSAGE);
				return;
			}
			Set<WebDoc> searchedWebDocs = new HashSet<>();
			searchedWebDocs = query.matches(webIndex);
			if(searchedWebDocs == null || searchedWebDocs.size()<=0){
				JOptionPane.showMessageDialog(webIndexPanel, "Sorry, We cannot find anything revelant to the query \""+queryString+"\"" ,"Sorry", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			showURLResults(searchedWebDocs);
		}
	}
}