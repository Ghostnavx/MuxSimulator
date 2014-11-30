import java.util.ArrayList;

// Each gate object represents a gate (duh). Program will consider the input and output objects as gates
// with no logic (data just passes through).
// Thus, adding "INPUT" and "OUTPUT" as a possible type for gates.
// Gates can have multiple inputs and outputs

public class Gate extends Executable{
	public String name;
	public ArrayList<Gate> input;  //parents, their index in the array will correspond to the input port
	public ArrayList<Gate> output;  //children, index in array will correspond to output port
	public GateType type;  //what type of gate
	public boolean activated;  //has it been activated?
	public boolean value;	//what is its signal?
	public int time;	//total propagation time at that gate

	//	possible alternative to having Executable check if all parents are activated, use a counter int
	//	stored in the gate to be incremented each time a parent gets activated.  When counter reaches 
	//	number of parents, automatically activate itself (and increment all children counters)

	//	possible alternative to storing arraylist of parents, just have an empty array instantiated
	//	to the size of the number of parents to be filled with primitive boolean input values and then
	//	process logic when array is full.  This would spare extra cycles having to manually update 
	//	parents/children with each movement since Java is pass by value not pass by reference.

	public Gate(){	//default constructor
	}

	public Gate(String newType, String newName){	//overloaded constructor
		type = GateType.getTypeFromString(newType);
		name = newName;
		input = new ArrayList<Gate>();
		output = new ArrayList<Gate>();
	}
	
	public boolean getValue(Gate g){
		return g.value;
	}

	//simple print method to return data about specified gate
	public void printGate(){
		System.out.println("ID: " + name + "\nType: " + type + "\nInputs:");
		for(Gate g: input){
			System.out.print(g.name + ", ");
		}
		System.out.println("\nOutputs:");
		for(Gate g: output){
			System.out.print(g.name + ", ");
		}
		System.out.println("\nActivated: " + activated + "\n");
	}

	//activates the gate and activates children if they're ready
	public void activate(){
		activated = true;	//set activated
		boolean allActive;	//have all the parents been activated?
		time = 0;

		//find the longest time from parents (critical time)
		for(Gate g:input){
			if(time < g.time)
				time = g.time;
		}

		//add current gate's propagation delay to critical time
		time += Circuit.propagationTimes.get(type.toString());

		//calculate the gate's output depending on inputs and gate type
		processGateLogic();

		//iterate through all the inputs of each child to check if they're ready to be activated
		for(Gate g : output){  //	Cycle through all outputs
			allActive = true;  //	variable to check if all inputs are activated
			for(Gate j : g.input){	//	iterate through all the inputs
				if(j.activated == false)	//	if one of the inputs on the child aren't activated, don't activate it
					allActive = false;
			}
			if(allActive == true)
				g.activate();	//	if all of the inputs on this child are active, then activate that child (yay recursion)
		}	
	}
	
	public void processGateLogic(){
		boolean truth;
		//big ol' switch statement, may add some methods to break this up for ease of reading
		switch (type.toString()){	//action will be different depending on what type of gate

		//start with a true value, if any inputs them are false, then value is false
		case "AND":
			truth = true;
			for( Gate gate : input){
				if(getValue(gate) == false)
					truth = false;
			}
			value = truth;
			break;

			//start with false value, if any inputs are true, then value is true
		case "OR":
			truth = false;
			for( Gate gate : input){
				if(getValue(gate) == true){
					truth = true;
					break;
				}
			}
			value = truth;
			break;

			//return opposite of input
		case "NOT":
			value = !(getValue(input.get(0)));
			break;

			//inverted and gate
		case "NAND":
			truth = true;
			for( Gate gate : input){
				if(getValue(gate) == false)
					truth = false;
			}
			value = !truth;
			break;

			//inverted or gate
		case "NOR":
			truth = false;
			for( Gate gate : input){
				if(getValue(gate) == true){
					truth = true;
					break;
				}
			}
			value = !truth;
			break;

			//exclusive or gate, if we find more than one input set to true, then return false
		case "XOR":
			truth = false;
			for( Gate gate : input){
				if(getValue(gate) == true && truth == false)
					truth = true;
				else if(getValue(gate) == true && truth == true){
					truth = false;
					break;
				}
			}
			value = truth;
			break;

			//no logic, just passes data through to start the circuit.  Value is already set
		case "INPUT":
			break;

			//same as input type, no logic, just passes value through
		case "OUTPUT":
			value = getValue(input.get(0));
			break;
		}
	}

}
