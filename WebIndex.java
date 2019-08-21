import java.util.*;
import java.io.*;

public class WebIndex implements Serializable{
	private String indexCategory;	// indicate what the index is for, content or keywords
	private int contentWordsCount = 0;	// the number of words in the index for content
	private int keywordsCount = 0;	// the number of words in the index for keywords
	Map<String, Set<WebDoc>> contentWordsMap = new HashMap<>();
	Map<String, Set<WebDoc>> keywordsMap = new HashMap<>();
	
	public WebIndex(String indexCategory){
		this.indexCategory = indexCategory;
	}
	
	public int getContentWordsCount(){
		return contentWordsCount;
	}
	
	public Map<String, Set<WebDoc>> getContentWordsMap(){
		return contentWordsMap;
	}
	
	public void setContentWordsCount(int num){
		this.contentWordsCount = num;
	}
	
	public void setContentWordsMap(Map<String, Set<WebDoc>> map){
		this.contentWordsMap = map;
	}

	/**
     * merges the set of words in the given web document into the index
     * 
     * @param doc
     *            WebDoc web document
     * @return true if the web document is successfully added in the index
     */
	public boolean add(WebDoc doc){
		boolean added = false;
		if(indexCategory == "content" && doc.getContentWordsSet() != null){
			Set<String> contentWordsSet = doc.getContentWordsSet();
			Iterator conIte = contentWordsSet.iterator();
			while(conIte.hasNext()){
				String key = (String)conIte.next();
				Set<WebDoc> webDocsValue=contentWordsMap.get(key);
				if(webDocsValue == null){
					Set<WebDoc> temp = new HashSet<>();
					temp.add(doc);
					webDocsValue = new HashSet<WebDoc>(temp);
					contentWordsMap.put(key, webDocsValue);
				}else{
					webDocsValue.add(doc);
					contentWordsMap.put(key, webDocsValue);
				}
			}
			added = true;
		}
		if(indexCategory == "keywords" && doc.getKeywordsSet() != null){
			Set<String> keywordsSet = doc.getKeywordsSet();
			Iterator keywordIte = keywordsSet.iterator();
			while(keywordIte.hasNext()){
				String key = (String)keywordIte.next();
				Set<WebDoc> webDocsValue=keywordsMap.get(key);
				if(webDocsValue == null){
					Set<WebDoc> temp = new HashSet<>();
					temp.add(doc);
					webDocsValue = new HashSet<WebDoc>(temp);
					keywordsMap.put(key, webDocsValue);
				}else{
					webDocsValue.add(doc);
					contentWordsMap.put(key, webDocsValue);
				}
			}
			added = true;
		}
		return added;
	}
	
	/**
     * returns all the documents which make up the index
     * 
     * @return an ArrayList contains all the documents
     */
	public Set<WebDoc> getAllDocuments(){
		Set<WebDoc> allDocuments = new HashSet<WebDoc>();
		if(indexCategory == "content"){
			Set<Map.Entry<String, Set<WebDoc>>> key_webDocConSet = contentWordsMap.entrySet();
			Iterator<Map.Entry<String, Set<WebDoc>>> conIte = key_webDocConSet.iterator();
			while(conIte.hasNext()){
				allDocuments.addAll(conIte.next().getValue());
			}
		}else{
			Set<Map.Entry<String, Set<WebDoc>>> key_webDocKeySet = keywordsMap.entrySet();
			Iterator<Map.Entry<String, Set<WebDoc>>> keyIte = key_webDocKeySet.iterator();
			while(keyIte.hasNext()){
				allDocuments.addAll(keyIte.next().getValue());
			}
		}
		return allDocuments;
	}
	
	/**
     * returns the documents which contain the given string
     * 
     * @param wd 
     *          word as a string user want to search
     * @return an ArrayList contains documents which contain the given string
     */
	public Set<WebDoc> getMatches(String wd){
		wd = wd.toLowerCase();
		if(indexCategory == "content"){
			//System.out.println(contentWordsMap.get(wd));
			return contentWordsMap.get(wd);
		}else{
			return keywordsMap.get(wd);
		}
	}
	
	/**
     * calculate the number of words included in the index
     * 
     */
	private void countWords(){
		if(indexCategory == "content"){
			Set<String> totalContentWords = new HashSet<>();
			Set<WebDoc> contentWebDocs = getAllDocuments();
			for(WebDoc contentWebDoc: contentWebDocs){
				totalContentWords.addAll(contentWebDoc.getContentWordsSet());
			}
			contentWordsCount = totalContentWords.size();
		}else{
			Set<String> totalKeywords = new HashSet<>();
			Set<WebDoc> keywordWebDocs = getAllDocuments();
			for(WebDoc keywordsWebDoc: keywordWebDocs){
				totalKeywords.addAll(keywordsWebDoc.getKeywordsSet());
			}
			keywordsCount = totalKeywords.size();
		}
	}
	
	/**
     * Return webIndex as a string
     * 
     * @return the string
     */
	public String toString(){
		countWords();
		if(indexCategory == "content"){
			return ("WebIndex over "+indexCategory+" contains "+contentWordsCount+ " words from "+getAllDocuments().size()+" documents");
		}else{
			return ("WebIndex over "+indexCategory+" contains "+keywordsCount+" words from "+getAllDocuments().size()+" documents");
		}
	}
}