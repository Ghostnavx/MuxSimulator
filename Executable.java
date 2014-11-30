import java.io.FileNotFoundException;
import java.util.Arrays;

//TODO: optimize, document, test the shit outta everything, make method/variable names more user-friendly, remove sarcastic comments
//	This is the main module, it's going to activate the circuit, feeding it the appropiate inputs
//	Circuit constructor takes two parameters, the file locations for the circuit data and propagation data respectively
//	Possibly add a file picker
public class Executable {
	public static void main(String args []) throws FileNotFoundException{
//		AdditionProblem problem = new AdditionProblem(123, 7);
//		problem.execute();
		Circuit circuit = new Circuit("D://ClassCircuit.txt", "D://propagationTimes.txt");	//construct circuit
		execute(circuit, "D://vectors.txt");
	}

	public static void execute(Circuit circuit, String filePath) throws FileNotFoundException{
		int [][] vectors = InputParser.getVectors(filePath);		//read vectors from file

		//This module runs the circuit, then combines the inputs and outputs into a 2D array for printing later
		//It's an ugly hairball of code to make our truth table look uniform and pretty
		String [][] results = new String[vectors.length +1][circuit.outputLabels.size() + circuit.inputLabels.size()];
		System.arraycopy(circuit.inputLabels.toArray(), 0, results[0], 0, circuit.inputLabels.size());
		System.arraycopy(circuit.outputLabels.toArray(), 0, results[0], circuit.inputLabels.size(), circuit.outputLabels.size());

		for(int i = 1; i < results.length; i++){
			String [] tempArray = Arrays.toString(circuit.execute(vectors[i-1])).split("[\\[\\]]")[1].split(", ");
			System.arraycopy(Arrays.toString(vectors[i-1]).split("[\\[\\]]")[1].split(", "), 0, results[i], 0, vectors[i-1].length);
			System.arraycopy(tempArray, 0, results[i], vectors[i-1].length, tempArray.length);
		}
		printTruthTable(results, circuit.propagationTime);
	}

	//pain in the ass string concatenation nightmare to make a truth table
	public static void printTruthTable(String [][] results, int propagationTime){
		for(int i = 0; i < results.length; i++){
			for(int j = 0; j < results[0].length; j++){
				if(i == 0 || results[0][j].length() == 1)
					System.out.print("|" + results[i][j]);
				else
					System.out.print("|" + results[i][j] + String.format("%1$" + (results[0][j].length()-1) + "s", "" ));
			}
			System.out.println("|");
		}
		System.out.println("Propagation Time: " + propagationTime + "ns");
	}
}
