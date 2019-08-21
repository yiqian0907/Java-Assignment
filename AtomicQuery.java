import java.util.*;

public class AtomicQuery implements Query{
	private String queryWord;
	public AtomicQuery(String word){
		this.queryWord = word;
	}
	
	public Set<WebDoc> matches(WebIndex wind){
		return wind.getMatches(queryWord);
	}
	
	public String toString(){
		return "an AtomicQuery (for \""+queryWord+"\");";
	}
}