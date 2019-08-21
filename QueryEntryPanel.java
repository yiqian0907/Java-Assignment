import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
	
public class QueryEntryPanel extends JPanel{
	final String[] operator = {"and", "or", "not"};
	final String[] queryMode = {"prefix", "infix", "manual"};
	private EditPanel editPanel;
	private PrefixPanel prefixPanel;
	private InfixPanel infixPanel;
	private ManualInputPanel manualInputPanel;
	private SearchPanel searchPanel;
	private String queryModeValue;
	
	public QueryEntryPanel(){
		setPreferredSize(new Dimension(400, 400));
		setLayout(new BorderLayout(10, 30));
		editPanel = new EditPanel(this);
		prefixPanel = new PrefixPanel();
		infixPanel = new InfixPanel();
		manualInputPanel = new ManualInputPanel();
		searchPanel = new SearchPanel();
		queryModeValue = "prefix";
		add(editPanel, BorderLayout.NORTH);
		add(prefixPanel, BorderLayout.WEST);
		add(searchPanel, BorderLayout.SOUTH);
	}
	
	public PrefixPanel getPrefixPanel(){
		return prefixPanel;
	}
	
	public InfixPanel getInfixPanel(){
		return infixPanel;
	}
	
	public ArrayList<JComponent> getPrefixComponents(){
		return prefixPanel.getAllComponents();
	}
	
	public ArrayList<JComponent> getInfixComponents(){
		return infixPanel.getAllComponents();
	}
	
	public String getQueryModeVal(){
		return queryModeValue;
	}
	
	public JTextField getManuallyInputedField(){
		return manualInputPanel.getInputedField();
	}
	
	public void addSearchListener(ActionListener listenerForSearchBtn){
		searchPanel.getSearchBtn().addActionListener(listenerForSearchBtn);
	}

	//contains a JComboBox for user to choose the query mode (prefix, infix or manually input)
	private class EditPanel extends JPanel{
		private JLabel reminder;
		private JComboBox<String> selectQueryMode;
		public EditPanel(QueryEntryPanel parentPanel){
			setPreferredSize(new Dimension(400, 60));
			reminder = new JLabel("Select the query mode: ");
			selectQueryMode = new JComboBox<>(queryMode);
			selectQueryMode.setPreferredSize(new Dimension(150, 30));
			selectQueryMode.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
			selectQueryMode.addItemListener(new QueryModeChangeListener(parentPanel));
			add(reminder);
			add(selectQueryMode);
		}
		
		public String getQueryMode(){
			return selectQueryMode.getSelectedItem().toString();
		}
	}
	
	// a panel for user to input their prefix queries
	private class PrefixPanel extends JPanel{
		ArrayList<JComponent> prefixComponents = new ArrayList<>();	//	store all of components used in prefix panel
																	//	in order to create a whole prefix string later
		private JButton addPrefixQueryWord;
		private JButton addPrefixNestedQuery;
		public PrefixPanel(){
			setPreferredSize(new Dimension(200, 340));
			addEditorButtons();
			addPrefixQuery();
		}
		
		//	add two buttons for user to create their expected prefix queries as much as possible
		public void addEditorButtons(){
			addPrefixQueryWord = new JButton("AddPrefixQueryWord");
			addPrefixNestedQuery = new JButton("AddPrefixNestedQuery");
			addPrefixQueryWord.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					prefixPanel.addPrefixTextField();
				}
			});
			addPrefixNestedQuery.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					prefixPanel.addPrefixQuery();
				}
			});
			add(addPrefixQueryWord);
			add(addPrefixNestedQuery);
		}
		
		// add one more JTextField when users click the "AddPrefixQueryWord" button
		public void addPrefixTextField(){
			JTextField newPrefixWord = new JTextField(10);
			this.add(newPrefixWord);
			prefixComponents.add(newPrefixWord);
			this.revalidate();
			this.repaint();
		}
		
		/**
		 *	add one nested prefix queries (one JComboBox, two JTextFields)
		 *	when users click the "AddPrefixNestedQuery" button
		 */
		public void addPrefixQuery(){
			JComboBox<String> newPreOperatorSel = new JComboBox<>(operator);
			JTextField newPrefixWord1 = new JTextField(10);
			JTextField newPrefixWord2 = new JTextField(10);
			newPreOperatorSel.addItemListener(new ItemChangeListener());
			this.add(newPreOperatorSel);
			prefixComponents.add(newPreOperatorSel);
			this.add(newPrefixWord1);
			prefixComponents.add(newPrefixWord1);
			this.add(newPrefixWord2);
			prefixComponents.add(newPrefixWord2);
			this.revalidate();
			this.repaint();
		}
		
		public ArrayList<JComponent> getAllComponents(){
			return prefixComponents;
		}
	}
		
	//	a panel for user to input their infix queries
	private class InfixPanel extends JPanel{
		ArrayList<JComponent> infixComponents = new ArrayList<>();
		private JButton addInfixQuery;
		private JTextField firstInfixWord;
		public InfixPanel(){
			setPreferredSize(new Dimension(200, 340));
			addEditorButtonAndFirstWord();
			addInfixQuery();
		}
		
		public void addEditorButtonAndFirstWord(){
			addInfixQuery = new JButton("AddInfixQuery");
			addInfixQuery.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					infixPanel.addInfixQuery();
				}
			});
			add(addInfixQuery);
			firstInfixWord = new JTextField(10);
			add(firstInfixWord);
			infixComponents.add(firstInfixWord);
		}
		
		//	add one more JComboBox and JTextField when users click the "AddInfixQuery" button
		public void addInfixQuery(){
			JComboBox<String> newInOperatorSel = new JComboBox<>(operator);
			JTextField newInfixWord = new JTextField(10);
			this.add(newInOperatorSel);
			infixComponents.add(newInOperatorSel);
			this.add(newInfixWord);
			infixComponents.add(newInfixWord);
			this.revalidate();
			this.repaint();
		}
		
		public ArrayList<JComponent> getAllComponents(){
			return infixComponents;
		}
	}
		
	//	a panel for user to manually input either prefix or infix queries
	private class ManualInputPanel extends JPanel{
		private JTextField inputField;
		
		public ManualInputPanel(){
			inputField = new JTextField(30);
			add(inputField);
		}
		public JTextField getInputedField(){
			return inputField;
		}
	}
	
	/**	
	 *	contains two buttons for user to reset the query they have inputed
	 *	or search the queries they inputed
	 */
	private class SearchPanel extends JPanel{
		private JButton reset;
		private JButton search;
		public SearchPanel(){
			setPreferredSize(new Dimension(400, 40));
			reset = new JButton("Reset");
			reset.addActionListener(new ResetListener());
			search = new JButton("Search");
			add(reset);
			add(search);
		}
		public JButton getSearchBtn(){
			return search;
		}
	}
	
	/**
	 *	listener for the JComboBox in the prefix panel
	 *	when user select the "not" operator, the number of JTextField following it should be one
	 * 	otherwise, it should be two 
	 */
	private class ItemChangeListener implements ItemListener{
		public void itemStateChanged(ItemEvent e){
			if(e.getStateChange() == ItemEvent.SELECTED && e.getItem().toString().equals("not")){
				ArrayList<JComponent> prefixComponents = prefixPanel.getAllComponents();
				prefixPanel.remove(prefixComponents.get(prefixComponents.size()-1));
				prefixComponents.remove(prefixComponents.size()-1);
				prefixPanel.revalidate();
				prefixPanel.repaint();
			}
			if(e.getStateChange() == ItemEvent.DESELECTED && e.getItem().toString().equals("not")){
				prefixPanel.addPrefixTextField();
			}
		}
	}
	
	/**
	 *	listener for the reset button in the search panel
	 *	set the prefix, infix or manual panel to the original state
	 */
	private class ResetListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(editPanel.getQueryMode().equals("prefix")){
				ArrayList<JComponent> prefixComponents = prefixPanel.getAllComponents();
				prefixComponents.clear();
				prefixPanel.removeAll();
				prefixPanel.addEditorButtons();
				prefixPanel.addPrefixQuery();
			}else if(editPanel.getQueryMode().equals("infix")){
				ArrayList<JComponent> infixComponents = infixPanel.getAllComponents();
				infixComponents.clear();
				infixPanel.removeAll();
				infixPanel.addEditorButtonAndFirstWord();
				infixPanel.addInfixQuery();
			}else{
				manualInputPanel.getInputedField().setText("");
			}
		}
	}
	
	/**
	 *	listener for the JComboBox in the editPanel
	 *	show the different panel according to the value user selected
	 */
	private class QueryModeChangeListener implements ItemListener{
		private QueryEntryPanel queryEntryPanel;
		public QueryModeChangeListener(QueryEntryPanel queryEntryPanel){
			this.queryEntryPanel = queryEntryPanel;
		}
		public void itemStateChanged(ItemEvent e){
			if(e.getStateChange() == ItemEvent.SELECTED){
				if(e.getItem().toString().equals("prefix")){
					queryEntryPanel.queryModeValue = "prefix";
					queryEntryPanel.remove(infixPanel);
					queryEntryPanel.remove(manualInputPanel);
					queryEntryPanel.add(prefixPanel, BorderLayout.WEST);
				}else if(e.getItem().toString().equals("infix")) {
					queryEntryPanel.queryModeValue = "infix";
					queryEntryPanel.remove(prefixPanel);
					queryEntryPanel.remove(manualInputPanel);
					queryEntryPanel.add(infixPanel, BorderLayout.WEST);
				}else if(e.getItem().toString().equals("manual")){
					queryEntryPanel.queryModeValue = "manual";
					queryEntryPanel.remove(prefixPanel);
					queryEntryPanel.remove(infixPanel);
					queryEntryPanel.add(manualInputPanel, BorderLayout.WEST);
				}
			}
			queryEntryPanel.revalidate();
			queryEntryPanel.repaint();
		}
	}
}