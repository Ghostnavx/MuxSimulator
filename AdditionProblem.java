import java.io.FileNotFoundException;
import java.util.ArrayList;


//TODO: add my own binary conversion, built-in java conversion doesn't work correctly with signed numbers.
public class AdditionProblem {
	int a,b, checkValue, smaller, diff, larger;
	int []  aBinary, bBinary;
	int [] vectors;
	int bothBinary [][];
	String largerString;
	ArrayList<Integer> sum;
	public AdditionProblem(int aNew, int bNew){
		a = aNew;
		b = bNew;
		String tempaBinary = Integer.toBinaryString(a);
		String tempbBinary = Integer.toBinaryString(b);


		checkValue = a+b;
		diff = Math.abs(tempaBinary.length() - tempbBinary.length());
		String temp = "";
		for(int i = 0; i < diff; i++){
			temp += "0";
		}

		if(tempaBinary.length() < tempbBinary.length()){
			tempaBinary = temp + tempaBinary;
			larger = tempbBinary.length();
		}
		else if(tempbBinary.length() < tempaBinary.length()){
			tempbBinary = temp + tempbBinary;
			larger = tempaBinary.length();
		}
		else{
			larger = tempaBinary.length();
		}

		System.out.println(tempaBinary + "\n" + tempbBinary);

		bothBinary = new int[2][larger];

		for(int i = 0; i < tempaBinary.length(); i++)
			bothBinary[0][i] = Character.getNumericValue(tempaBinary.charAt(tempaBinary.length() -1 -i));

		for(int i = 0; i < tempbBinary.length(); i++)
			bothBinary[1][i] = Character.getNumericValue(tempbBinary.charAt(tempbBinary.length() -1 -i));

	}

	public void execute() throws FileNotFoundException{
		vectors = new int[3];
		sum = new ArrayList<Integer>();
		Circuit circuit = new Circuit("D://adder.txt", "D://propagationTimes.txt");
		vectors[2] = 0;
		int [] tempData = null;

		for(int i = 0; i < bothBinary[0].length; i++){
			vectors[0] = bothBinary[0][i];
			vectors[1] = bothBinary[1][i];
			tempData = circuit.execute(vectors);
			sum.add(tempData[0]);
			vectors[2] = tempData[1];
		}

		sum.add(tempData[1]);
		sum.trimToSize();

		String out = "";
		for(int i = sum.size()-1; i >=0; i--){
			out += sum.get(i).toString();
		}
		int newOutput = Integer.parseInt(out, 2);
		System.out.println(newOutput);
	}
}
