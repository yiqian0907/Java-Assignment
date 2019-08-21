import java.util.*;
import java.util.regex.*;

public class QueryBuilder{
	
	public static Query parse(String queryString) throws IllegalQueryStringException{
		queryString = queryString.replaceAll("\\s", "");
		ArrayList<Query> subQuerys = new ArrayList<>();
		Stack<String> stack = new Stack<>();
		int beforeLeftParenCount = 0;   // the counter is used to check whether the comma is the seperator of subQuerys
										// or just seperating the query words. 
										// it plus one when meeting with left parenthesis and minus one when it comes to right
										// excludes the outmost left and right parentheses which belong to the Query not subQuerys.
		
		/*
		** iterating the whole queryString
		** put the outmost operator, left parenthesis, each subquery as a string, right parenthesis into the stack in turn
		*/
		String pushWord = "";
		for(int i=0; i<queryString.length(); i++){
			char character = queryString.charAt(i);
			if(character == '('){
				if(!stack.contains("(")){
					if(pushWord.isEmpty())
						throw new IllegalQueryStringException("inappropriate operator position.");
					stack.push(pushWord);
					pushWord = "";
					stack.push("(");
					continue;
				}
				beforeLeftParenCount++;
			}else if(character == ','){
				if(beforeLeftParenCount == 0){
					stack.push(pushWord);
					pushWord = "";
					continue;
				}
			}else if(character == ')'){
				if(i == queryString.lastIndexOf(")")){
					if(!stack.contains("("))
						throw new IllegalQueryStringException("the parentheses are not balanced.");
					if(pushWord.isEmpty())
						throw new IllegalQueryStringException("there is no content between the parentheses.");
					stack.push(pushWord);
					pushWord = "";
					stack.push(")");
					continue;
				}else if(beforeLeftParenCount == 0)
					throw new IllegalQueryStringException("the parentheses are not balanced.");
				beforeLeftParenCount--;
			}
			pushWord += character;
		}
		// for the atomic pushWord that does not contains "(", "," and ")"
		if(!pushWord.isEmpty())
			stack.push(pushWord);
		
		String popWord="";
		if(!stack.contains("(")){	// represents it is a atomic word
			popWord = stack.pop();
			if(popWord.equals("and") || popWord.equals("or") || popWord.equals("not"))
				throw new IllegalQueryStringException("operator cannot be a query word");
			return new AtomicQuery(popWord);
		}
		
		while (!stack.empty()){
			popWord = stack.pop();
			if(popWord.equals("(")){	// represents that all of subQuerys of this Query are handled
				String operator = stack.pop();	// obtain the operator and then return the according specific Query
				if(operator.equals("and"))
					return new AndQuery(subQuerys);
				else if(operator.equals("or"))
					return new OrQuery(subQuerys);
				else if(operator.equals("not")){
					if(subQuerys.size() > 1)
						throw new IllegalQueryStringException("the NotQuery can only receive one pushWord.");
					return new NotQuery(subQuerys.get(0));
				}else
					throw new IllegalQueryStringException("the operator can just be either 'and', 'or' or 'not'.");
			}
			if(!popWord.equals(")"))
				subQuerys.add(parse(popWord));	// invoke the parse method again and pass the subquery as the parameter
												// and then add the returned Query to the ArrayList which stores the subQuerys
		}
		return null;
	}
	
	public static Query parseInfixForm(String queryString) throws IllegalQueryStringException{
		queryString+=" ";
		Stack<String> stack = new Stack<>();
		String infixStr = "";
		String pushWord = "";
		String popWord = "";
		for(int i=0; i<queryString.length(); i++){
			char character = queryString.charAt(i);
			if(character == ' '){	// the symbol of the ending of last word
				if(!pushWord.isEmpty())
					stack.push(pushWord);
				pushWord="";
				continue;
			}else if(character == '('){
				pushWord="";
				stack.push("(");
				continue;
			}else if(character == ')'){	// the ending of one bracket
				stack.push(pushWord);
				pushWord="";
				while((popWord=stack.pop()) != "(")
					infixStr = popWord+" "+infixStr;	// linked together the words as a string between the bracket 
				String prefixStr = convertInfixToPrefix(infixStr);	// convert the above string to the prefix form
				infixStr="";
				stack.push(prefixStr);	// put the prefix form into the stack
				continue;
			}
			pushWord+=character;
		}
		/*
		** after the above for loop, all of words are putted into the stack and the words in the brackets are converted to prefix form first
		** therefore the follwing code is to convert the left infix form string to prefix form string
		*/
		while(!stack.isEmpty())
			infixStr=stack.pop()+" "+infixStr;
		String prefixStr = convertInfixToPrefix(infixStr);
		System.out.println(prefixStr);
		if(!prefixStr.matches("^\\w+\\(.*\\)$"))
			throw new IllegalQueryStringException();
		System.out.println(prefixStr);
		return parse(prefixStr);
	}
	
	private static String convertInfixToPrefix(String infixStr){
		infixStr += " ";
		String scannedpushWord = "";
		String leftParseStr="";
		for(int i=0; i<infixStr.length(); i++){
			char character = infixStr.charAt(i);
			if(character == ' '){	// whenever a new word is added to leftParseStr, check whether the current string can be converted
				leftParseStr = leftParseStr.replaceAll("not\\s(\\w+)", "not($1)");	// if "not abc", then => "not(abc)"
				Pattern queryPattern = Pattern.compile("(.+)\\s(and|or)\\s([^\\(].*)");
				Matcher queryMatcher = queryPattern.matcher(leftParseStr);
				if(queryMatcher.find() && !(queryMatcher.group(3).equals("not")))
					leftParseStr = leftParseStr.replaceAll("(.+)\\s(and|or)\\s([^\\(].*)", "$2($1,$3)"); // if "a and b", then => "and(a,b)"
				scannedpushWord="";
			}
		leftParseStr += character;
		scannedpushWord += character;
		}
		leftParseStr= leftParseStr.replaceAll(" ", "");	// remove the last space
		return leftParseStr;
	}
}