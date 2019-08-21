import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WebIndexTest{
	public static void main(String[] args){
		WebIndexFrame webIndexFrame = new WebIndexFrame();
		final WebIndex webIndex = new WebIndex("content");
		
		/** 
		 *	read the saved webIndex from the external file when everytime the window is opened
		 *	and change the content of own webIndex according to the content of saved webIndex
		 */
		webIndexFrame.addWindowListener(new WindowAdapter(){
			public void windowOpened(WindowEvent e){
				try{
					ObjectInputStream readWebIndex = new ObjectInputStream(new FileInputStream("/savedWebIndex.ser"));
					WebIndex savedWebIndex = (WebIndex)readWebIndex.readObject();
					webIndex.setContentWordsCount(savedWebIndex.getContentWordsCount());
					webIndex.setContentWordsMap(savedWebIndex.getContentWordsMap());
					readWebIndex.close();
				}catch(FileNotFoundException fnfe){
				}catch(ClassNotFoundException cnfe){
					JOptionPane.showMessageDialog(webIndexFrame.getWebIndexPanel(),
													"Cannot find approriate Class to deserialize the saved index history",
													"WARNING",
													JOptionPane.WARNING_MESSAGE);
				}catch(IOException ioe){
					JOptionPane.showMessageDialog(webIndexFrame.getWebIndexPanel(),
													"Failed to load the index history",
													"WARNING",
													JOptionPane.WARNING_MESSAGE);
				}
				WebIndexController webIndexController = new WebIndexController(webIndexFrame, webIndex);
			}
		});
		
		// when the window is closing, save the webIndex to external file
		webIndexFrame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try{
					ObjectOutputStream savedWebIndex = new ObjectOutputStream(new FileOutputStream("/savedWebIndex.ser"));
					savedWebIndex.writeObject(webIndex);
					savedWebIndex.close();
				}catch(IOException ioe){
					int confirm = JOptionPane.showConfirmDialog(webIndexFrame.getWebIndexPanel(), 
																"The index may lost if closed the window.\nAre you sure to close it?",
																"Closing Confirmation",
																JOptionPane.YES_NO_OPTION,
																JOptionPane.QUESTION_MESSAGE);
					if(confirm == JOptionPane.YES_OPTION){
						webIndexFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					}else{
						webIndexFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					}
				}
			}
		});
		webIndexFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		webIndexFrame.setVisible(true);
	}
}