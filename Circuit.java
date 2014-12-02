import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//TODO: clean up hanging references in reset method
public class Circuit {
	public HashMap<String, Gate> gates;
	public static HashMap<String, Integer> propagationTimes;
	public int [] numIO;
	public ArrayList<String> inputLabels;
	public ArrayList<String> outputLabels;
	public int propagationTime;
	public int count;
	String propagationDataKeep;

	public Circuit(String circuitData, String propagationData) throws FileNotFoundException{
		count = 0;
		propagationDataKeep = propagationData;
		gates = new HashMap<String, Gate>();	//initialize HashMap to store the gate information
		propagationTimes = InputParser.getPropagationTime(propagationData);	//read prop times from file

		//process ALL that data into the circuit
		processData(InputParser.parse(circuitData));
	}

	//read first "flag" string to determine how to process data
	//then builds the circuit data structure accordingly
	//might change to a switch block for microscopic performance enhancement
	//remove flag data first, then process remaining Strings
	public void processData(ArrayList<ArrayList<String>> data) throws FileNotFoundException{
		for(int i = 0; i < data.size(); i++){
			if(data.get(i).get(0).equals("IOMATCH")){
				data.get(i).remove(0);
				processIO(data.get(i));
			}
			else if(data.get(i).get(0).equals("LABELMATCH")){
				data.get(i).remove(0);
				processLabels(data.get(i));
			}
			else if(data.get(i).get(0).equals("GATEMATCH")){
				data.get(i).remove(0);
				processGate(data.get(i));
			}
			else if(data.get(i).get(0).equals("CONNECTIONMATCH")){
				data.get(i).remove(0);
				connectGates(data.get(i));
			}
			else if(data.get(i).get(0).equals("INPUTMATCH")){
				data.get(i).remove(0);
				processInput(data.get(i));
			}
			else if(data.get(i).get(0).equals("OUTPUTMATCH")){
				data.get(i).remove(0);
				processOutput(data.get(i));
			}
			else if (data.get(i).get(0).equals("CELLMATCH")){
				data.get(i).remove(0);
				processCell(data.get(i));
			}
			else{
				System.err.println("Syntax Error while processing data");
				System.exit(0);
			}	
		}
	}

	//resets all of the nodes back to their base state for next iteration of execution
	public void reset(){
		for(Gate g:gates.values())
			g.activated = false;
	}

	//activate the inputs starting the recursive function
	public int [] execute(int [] vectors){
		if(vectors.length == 3)
			System.out.println("Vectors" + Arrays.toString(vectors));
		reset();
//		for(int j = 0; j < vectors.length; j++){
//			gates.get(inputLabels.get(j)).value = intToBoolean(vectors[j]);	//set the signal from the vector input
//			gates.get(inputLabels.get(j)).activate();	//activate that input
//		}
		int q = 0;
		for(String s: inputLabels){
			gates.get(s).value = intToBoolean(vectors[q]);
			gates.get(s).activate();
			q++;
		}

		int tempTime = 0;
		for(String s : outputLabels){
			if(gates.get(s).time > tempTime)
				tempTime = gates.get(s).time;
		}
		propagationTime = tempTime;	//store the critical time for circuit execution

		int [] returnData = new int[outputLabels.size()];
		for(int i = 0; i < returnData.length; i++){
			returnData[i] = booleanToInt(gates.get(outputLabels.get(i)).value);
		}
		//printArray(returnData, vectors.length);
		return returnData;
	}

	public void printArray(int [] a, int b){
		if(b == 2)
			System.out.print("HalfAdder Data: " + a[0] + " " + a[1]);
		else
			System.out.print("Sum: " + a[0] + " Carry: " + a[1]);
		System.out.println();
	}
	public static int booleanToInt(boolean boo){
		if (boo == true)
			return 1;
		else
			return 0;
	}

	public static boolean intToBoolean(int i){
		if (i == 1)
			return true;
		else
			return false;
	}

//	//method for dumping entire HashMap if needed for debugging
//	@SuppressWarnings("rawtypes")
//	public void printMap() {
//		//use a soft copy of gates to prevent it from being destroyed
//		HashMap<String, Gate> temp = new HashMap<String, Gate>(gates);
//		Iterator it = temp.entrySet().iterator();
//		while (it.hasNext()) {
//			Map.Entry pairs = (Map.Entry)it.next();
//			((Gate) pairs.getValue()).printGate();
//			it.remove(); // avoids a ConcurrentModificationException
//		}
//	}

	//process IO statement
	public void processIO(ArrayList<String> data){
		numIO = new int []{Integer.parseInt(data.get(0)), Integer.parseInt(data.get(1))};
		inputLabels = new ArrayList<String>(numIO[0]);
		outputLabels = new ArrayList<String>(numIO[1]);
	}

	//process input/output labels statement
	public void processLabels(ArrayList<String> data){
		for(int i = 0; i < numIO[0]+numIO[1]; i++){
			if(i < numIO[0]){
				gates.put(data.get(i), new Gate("INPUT", data.get(i)));
				inputLabels.add(data.get(i));
			}
			else{
				gates.put(data.get(i), new Gate("OUTPUT", data.get(i)));
				outputLabels.add(data.get(i));
			}
		}
	}

	//process gate statement
	public void processGate(ArrayList<String> data){
		gates.put(data.get(1), new Gate(data.get(0), data.get(1)));
	}
	
	public void processCell(ArrayList<String> data) throws FileNotFoundException{
		gates.put(data.get(1), new Cell(new Circuit(data.get(0), propagationDataKeep), data.get(1)));
	}

	//process input statement
	public void processInput(ArrayList<String> data){
		Gate parent = gates.get(data.get(0));
		Gate child = gates.get(data.get(1));
		int parentOutputPort = count;
		int childInputPort = Integer.parseInt(data.get(2));
		Connection newConnection = new Connection(parent, child, parentOutputPort, childInputPort);
		
		parent.output.add(newConnection);
		child.input.add(newConnection);
		count++;
	}

	//process output statement
	public void processOutput(ArrayList<String> data){
		Gate parent = gates.get(data.get(0));
		Gate child = gates.get(data.get(2));
		int parentOutputPort = Integer.parseInt(data.get(1));
		int childInputPort = 0;		//output will only have one input
		Connection newConnection = new Connection(parent, child, parentOutputPort, childInputPort);
		
		parent.output.add(newConnection);
		child.input.add(newConnection);
	}

	//create a connection between two gates
	public void connectGates(ArrayList<String> data){
		Gate parent = gates.get(data.get(0));
		Gate child = gates.get(data.get(2));
		int parentOutputPort = Integer.parseInt(data.get(1));
		int childInputPort = Integer.parseInt(data.get(3));
		Connection newConnection = new Connection(parent, child, parentOutputPort, childInputPort);
		
		//make both gates reference the same connection object
		parent.output.add(newConnection);
		child.input.add(newConnection);
	}
}
