import java.util.ArrayList;
import java.util.HashMap;

//TODO: make sure the io port numbers are consistent, cannot rely on things being ordered correctly, need more reliable transfer method
public class Cell extends Gate {
	public Circuit box;
	public int [] vectors;
	public boolean [] outputPorts;
	public boolean [] inputPorts;

	public Cell(Circuit c, String n){
		box = c;
		inputPorts = new boolean[box.inputLabels.size()];
		outputPorts = new boolean[box.outputLabels.size()];
		input = new ArrayList<Connection>();
		output = new ArrayList<Connection>();
		name = n;
		type = GateType.getTypeFromString("CELL");
	}

	public void activate(){
		activated = true;
		vectors = new int[input.size()];
		for(int i = 0; i < input.size(); i++){
			vectors[i] = Circuit.booleanToInt(getValue(input.get(i)));
		}
		box.execute(vectors);
		for(int i = 0; i < outputPorts.length; i++){
			outputPorts[i] = box.gates.get(box.outputLabels.get(i)).value;
		}
		boolean allActive = true;
		for(Connection g : output){  //	Cycle through all outputs
			allActive = true;  //	variable to check if all inputs are activated
			for(Connection j : g.getChild().input){	//	iterate through all the inputs
				if(j.getParent().activated == false)	//	if one of the inputs on the child aren't activated, don't activate it
					allActive = false;
			}
			if(allActive == true)
				g.getChild().activate();	//	if all of the inputs on this child are active, then activate that child (yay recursion)
		}	
	}

	public boolean getValue(Connection g){
		if(g.getParent() instanceof Cell){
			Cell stuff = (Cell)g.getParent();
			return stuff.outputPorts[g.parentOutputPort];
		}
		else
			return super.getValue(g);
	}

	public int [] convertFromArrayList(ArrayList<Integer> temp){
		int [] newArray = new int[temp.size()];
		for(int i = 0; i < temp.size(); i++){
			newArray[i] = temp.get(i);
		}
		return newArray;
	}


}
