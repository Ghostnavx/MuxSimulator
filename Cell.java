import java.util.ArrayList;
import java.util.HashMap;


public class Cell extends Gate {
	public Circuit box;
	public ArrayList<Gate> input;  //parents, their index in the array will correspond to the input port
	public ArrayList<Gate> output;  //children, index in array will correspond to output port
	public ArrayList<Gate> vals; //output ports
	public int [] vectors;
	
	public Cell(Circuit c){
		ArrayList<Integer> temp = new ArrayList<Integer>();
		box = c;
		for(String s: box.inputLabels){
			if (box.gates.get(s).type == GateType.getTypeFromString("INPUT")){
				temp.add(Circuit.booleanToInt(box.gates.get(s).value));
			}
		}
		vectors = convertFromArrayList(temp);
	}
	
	public void execute(){
		box.execute(vectors);
	}
	
	public boolean getValue(Gate g){
		output.trimToSize();
		for(Gate gate: output){
			if (g.name.equals(gate.name))
				return gate.value;
		}
		System.err.println("Gate not found");
		System.exit(1);
		return true;
	}
	
	public int [] convertFromArrayList(ArrayList<Integer> temp){
		int [] newArray = new int[temp.size()];
		for(int i = 0; i < temp.size(); i++){
			newArray[i] = temp.get(i);
		}
		return newArray;
	}
	
	
}
