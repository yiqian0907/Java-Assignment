import java.util.*;

public interface Query{
	public Set<WebDoc> matches(WebIndex wind);
	
	public String toString();
}