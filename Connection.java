
public class Connection {
	Gate parent;
	Gate child;
	int parentOutputPort;
	int childInputPort;
	
	public Connection(){
		
	}
	
	public Connection(Gate p, Gate c, int o, int i){
		parent = p;
		child = c;
		parentOutputPort = o;
		childInputPort = i;
	}
	
	public Gate getParent(){
		return parent;
	}
	
	public Gate getChild(){
		return child;
	}
}
