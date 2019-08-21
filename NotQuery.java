import java.util.*;

public class NotQuery implements Query{
	private Query subQuery;
	public NotQuery(Query query){
		this.subQuery = query;
	}
	
	public Set<WebDoc> matches(WebIndex wind){
		Set<WebDoc> matchedWebDocs = subQuery.matches(wind);
		
		Set<WebDoc> allDocuments = wind.getAllDocuments();
		if(matchedWebDocs == null)
			return allDocuments;
		
		allDocuments.removeAll(matchedWebDocs);
		if(allDocuments.size() <= 0)
			return null;
		return allDocuments;
	}
	
	public String toString(){
		String str = "";
		str += "the NotQuery contains: ";
		str += subQuery.toString();
		return (str+";");
	}
}