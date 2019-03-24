
import java.util.ArrayList;

public class PoozList
{
	ArrayList<Object> list = new ArrayList<Object>();
	
	public PoozList(Object... objs){
		for(Object o : objs){
			list.add(o);
		}
	}
}
