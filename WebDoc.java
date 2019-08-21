import java.io.*;
import java.net.*;
import java.util.regex.*;
import java.util.*;

public class WebDoc implements Serializable{
	private String url;
	private Set<String> contentWordSet;	//a set of words of visible HTML content
	private Set<String> keywordsSet;	//a set of HTML keywords
	private int contentWordsCount = 0;	//the number of words of visible HTML content
	private int keywordsCount = 0;	//the number of HTML keywords
	private String contentFirstWord = "";	//the first alphabetically word of HTML content
	private String contentLastWord = "";	//the last alphabetically word of HTML content
	private boolean emptyFile = false;	//whether the file url pointed to is empty 
	private boolean fileNotFound = false;	//whether the file url pointed to does not exist
	private String formatStatus = "well-formed";	//the format status of HTML content
	

	public WebDoc(String url){
		this.url = url;
	}
	
	public int hashCode(){
		return 20 * url.hashCode();
	}
	
	public boolean equals(Object object){
		if(!(object instanceof WebDoc)){
			return false;
		}
		WebDoc otherWeb = (WebDoc)object;
		return otherWeb.getURL().equals(getURL());
	}
	
	public String getURL(){
		return url;
	}
	/**
     * Return the set of words of visible HTML content
     * 
     * @return set of words of visible HTML content as a Set<String>
     */
	public Set<String> getContentWordsSet(){
		extractContentWords();
		if(contentWordsCount <= 0){
			return null;
		}
		return contentWordSet;
	}
	
	/**
     * Return the set of HTML keywords
     * 
     * @return set of HTML keywords as a Set<String>
     */
	public Set<String> getKeywordsSet(){
		extractKeywords();
		if(keywordsCount <= 0){
			return null;
		}
		return keywordsSet;
	}
	
	
	/**
     * Return the whole HTML contents that the url pointed to
     * 
     * @return the whole HTML contents as a String
     */
	private String getHTMLContent() throws MalformedURLException, FileNotFoundException, IOException{
		BufferedReader htmlReader;
		String htmlTotalContent = "";
		String htmlContent;
		if(url.startsWith("file:")){
			String localUrl = url.substring(5);
			htmlReader = new BufferedReader(new FileReader(localUrl));
			while((htmlContent = htmlReader.readLine()) != null){
				htmlTotalContent += htmlContent;	
				htmlTotalContent += " ";
			}
			htmlReader.close();
		}else if(url.startsWith("http")){
			URL webUrl = new URL(url);
			htmlReader = new BufferedReader(new InputStreamReader(webUrl.openStream()));
			while((htmlContent = htmlReader.readLine()) != null){
				htmlTotalContent += htmlContent;
				htmlTotalContent += " ";
			}
			htmlReader.close();
		}
		if(htmlTotalContent.length() <= 0){
			emptyFile = true;
		}
		htmlTotalContent.replaceAll("[\\t\\r\\n]+", " ");
		return htmlTotalContent;
	}
	
	
	/**
     * Extract the visible HTML content from the whole HTML document
     * 
     */
	private void extractContentWords(){
		String bodyCon="";
		try{
			bodyCon = getHTMLContent();
			formatStatus(bodyCon);
			Pattern patternScript = Pattern.compile("<script[^>]*>.*</script>", Pattern.CASE_INSENSITIVE);
			Matcher marcherScript = patternScript.matcher(bodyCon);
			while(marcherScript.find()){
				bodyCon = bodyCon.replace(marcherScript.group(0), " ");
			}
			
			Pattern PatternStyle = Pattern.compile("<style[^>]*>.*</style>", Pattern.CASE_INSENSITIVE);
			Matcher marcherStyle = PatternStyle.matcher(bodyCon);
			while(marcherStyle.find()){
				bodyCon = bodyCon.replace(marcherStyle.group(0), " ");
			}
			
			bodyCon = bodyCon.replaceAll("\\d+", "");
			Pattern patternMark = Pattern.compile("<[^>]*>", Pattern.CASE_INSENSITIVE);
			Matcher matcherMark = patternMark.matcher(bodyCon);
			while(matcherMark.find()){
				bodyCon = bodyCon.replace(matcherMark.group(0), " ");
			}
			contentWordSet = new TreeSet<String>();
			
			String conWithoutPlace = bodyCon.replaceAll(" ","");
			if(conWithoutPlace.length() <=0){
				contentWordsCount = 0;
				return;
			}
			storeWords(bodyCon, contentWordSet);
			contentFirstWord = (String)(((TreeSet)contentWordSet).first());
			contentLastWord = (String)(((TreeSet)contentWordSet).last());
			contentWordsCount = contentWordSet.size();
		}catch(MalformedURLException murle){
			System.out.println(murle);
		}catch(FileNotFoundException fnfe){
			fileNotFound = true;
		}catch(IOException ioe){
			System.out.println(ioe);
			System.exit(1);
		}catch(NullPointerException npe){
			System.out.println();
		}
	}
	
	
	/**
     * Extract keywords from the whole HTML document
     * 
     */
	private void extractKeywords(){
		String bodyCon = "";
		try{
			bodyCon = getHTMLContent();
			Pattern patternKeywords = Pattern.compile("<meta.*?name.*?=.*?keywords.*?>",Pattern.CASE_INSENSITIVE);
			Matcher matchKeywords = patternKeywords.matcher(bodyCon);
			String keywords = "";
			while(matchKeywords.find()){
				Pattern metaCon = Pattern.compile("<.*?content.*?=.*?\"(.*?)\".*?>",Pattern.CASE_INSENSITIVE);
				Matcher matchMetaCon = metaCon.matcher(matchKeywords.group(0));
				if(matchMetaCon.matches()){
					keywords += matchMetaCon.group(1);
					keywords += " ";
				}
			}
			if(keywords.length() <=0){
				keywordsCount = 0;
				return;
			}
			keywordsSet = new HashSet<String>();
			storeWords(keywords, keywordsSet);
			keywordsCount = keywordsSet.size();
		}catch(MalformedURLException murle){
			System.out.println(murle);
		}catch(FileNotFoundException fnfe){
			fileNotFound = true;
		}catch(IOException ioe){
			System.out.println(ioe);
			System.exit(1);
		}
	}
	
	
	/**
     * Store words in the Set<String>
     * 
     * @param words String that contains many words    
	 * @param wordsSet 
	 *					A set used to store words
     */ 
	private void storeWords(String words, Set<String> wordsSet){
		String oneWord = "";
		char character;
		for(int i = 0; i<words.length(); i++){
			character = words.charAt(i);
			if(character == ' '  || character  == '\r' || character == '\n' || !Character.isLetter(character)){
				if(oneWord.length() > 0){
					wordsSet.add(oneWord.toLowerCase());
					oneWord = "";
				}
			}else{
				oneWord += character;
			}
		}
	}
	
	/**
     * Check if a string is well-formed, partly well-formed or badly-formed
     * 
     * @param HTMLCon the whole HTML content string
     */
	private void formatStatus(String HTMLCon){
		Pattern closingTagPat = Pattern.compile("<([^>]*)>");
		Matcher closingTagMat = closingTagPat.matcher(HTMLCon);
		Stack<String> stack = new Stack<>();
		while(closingTagMat.find()){
			try{
				String findStr = closingTagMat.group(0);
				if(!findStr.startsWith("</") && !findStr.startsWith("<!")){
					if(!findStr.endsWith("/>")){
						stack.push(findStr);
					}
					if(findStr.startsWith("<meta") || findStr.startsWith("<p") || findStr.startsWith("<hr") || findStr.startsWith("<br") || findStr.startsWith("<link")){
						formatStatus = "partly well-formed";
					}
				}else if(findStr.startsWith("</")){       
					String slashTagName = closingTagMat.group(1);
					String tagName = slashTagName.substring(1);
					String popTagName = "";
					if(stack.empty() || !((popTagName=stack.pop()).startsWith("<"+tagName))){
						if(popTagName.startsWith("<meta") || popTagName.startsWith("<p") || popTagName.startsWith("<hr") || popTagName.startsWith("<br") || popTagName.startsWith("<link")){
							if((stack.pop()).startsWith("<"+tagName)){
								formatStatus = "partly well-formed";
								continue;
							}
						}
						formatStatus = "badly-formed";
					}
				}
			}catch(EmptyStackException ese){
				continue;
			}
		}
	}
	
	
	/**
     * Return web document as a string
     * 
     * @return the string
     */
	public String toString(){
		extractContentWords();
		extractKeywords();
		if(emptyFile){
			return ("The file: \""+url+ "\" is empty");
		}
		if(fileNotFound){
			return ("ERROR: Cannot find the file "+url);
		}else{
			return (url + " "+contentWordsCount+" ("+contentFirstWord+"-"+contentLastWord+") "+keywordsCount+" "+formatStatus);
		}
		
	}
	
}