import java.util.*;

public class AndQuery implements Query{
	ArrayList<Query> subQuerys = new ArrayList<>();
	public AndQuery(ArrayList<Query> querys){
		subQuerys.addAll(querys);
	}
	public Set<WebDoc> matches(WebIndex wind){
		ArrayList<Set<WebDoc>> matchedWebDocs = new ArrayList<>();
		for(Query subQuery: subQuerys){
			if(subQuery.matches(wind) == null){
				return null;
			}
			matchedWebDocs.add(subQuery.matches(wind));
		}
		Set<WebDoc> intersection = new HashSet<>();
		intersection.addAll(matchedWebDocs.get(0));
		for(int i=1; i<matchedWebDocs.size(); i++){
			intersection.retainAll(matchedWebDocs.get(i));
		}
		if(intersection.size() <= 0)
			return null;
		return intersection;
	}
	public String toString(){
		String str = "";
		str += "the AndQuery contains "+subQuerys.size()+" follwing subquerys: ";
		for(Query query: subQuerys){
			str += query.toString();
			str += " ";
		}
		return (str+";");
	}
}