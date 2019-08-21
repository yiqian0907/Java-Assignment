import java.util.*;

public class OrQuery implements Query{
	ArrayList<Query> subQuerys = new ArrayList<>();
	public OrQuery(ArrayList<Query> querys){
		subQuerys.addAll(querys);
	}
	
	public Set<WebDoc> matches(WebIndex wind){
		ArrayList<Set<WebDoc>> matchedWebDocs = new ArrayList<>();
		for(Query subQuery: subQuerys){
			if(subQuery.matches(wind) != null)
				matchedWebDocs.add(subQuery.matches(wind));
		}
		
		if(matchedWebDocs.size() <= 0)
			return null;
		
		Set<WebDoc> union = new HashSet<>();
		for(Set<WebDoc> webDocs: matchedWebDocs){
			union.addAll(webDocs);
		}
		if(union.size() <= 0)
			return null;
		return union;
	}
	
	public String toString(){
		String str = "";
		str += "the OrQuery contains "+subQuerys.size()+" follwing subquerys: ";
		for(Query query: subQuerys){
			str += query.toString();
			str += " ";
		}
		return (str+";");
	}
}