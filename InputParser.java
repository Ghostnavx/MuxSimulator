import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.*;

//TODO: add user specified input files, possibly combine file reading into one and then splitting out to differeng parsing
//probably wouldn't help to combine file reading since each file is it's own object anyways
//I'll leave that decision up to non-sleep-deprived Jeremy or David
public class InputParser {
	//crap ton of regex patterns and matchers
	public final static Pattern IO = Pattern.compile("IO *\\( *\\d, *\\d *\\)");
	public final static Pattern IOLabels = Pattern.compile("IOLABELS *\\( *(\\w+, *)+\\w+? *\\)");
	public final static Pattern gate = Pattern.compile("GATE *\\( *\\w+ *, *\\w+ *\\)");
	public final static Pattern connection = Pattern.compile("CONN *\\( *\\w+ *, *\\d+ *, *\\w+ *, *\\w+ *\\)");
	public final static Pattern input = Pattern.compile("INPUT *\\( *\\w+ *, *\\w+ *, *\\d+ *\\)");
	public final static Pattern output = Pattern.compile("OUTPUT *\\( *\\w+ *, *\\d+ *, *\\w+ *\\)");
	public final static Pattern AND = Pattern.compile("^AND *\\( *\\d *\\)");
	public final static Pattern OR = Pattern.compile("^OR *\\( *\\d *\\)");
	public final static Pattern NOT = Pattern.compile("^NOT *\\( *\\d *\\)");
	public final static Pattern NAND = Pattern.compile("^NAND *\\( *\\d *\\)");
	public final static Pattern NOR = Pattern.compile("^NOR *\\( *\\d *\\)");
	public final static Pattern XOR = Pattern.compile("^XOR *\\( *\\d *\\)");
	public final static Pattern cell = Pattern.compile("^CELL *\\(");
	public static Matcher ANDMatch;
	public static Matcher ORMatch;
	public static Matcher NOTMatch;
	public static Matcher NANDMatch;
	public static Matcher NORMatch;
	public static Matcher XORMatch;
	public static Matcher IOMatch;
	public static Matcher labelMatch;
	public static Matcher gateMatch;
	public static Matcher connectionMatch;
	public static Matcher inputMatch;
	public static Matcher outputMatch;
	public static Matcher cellMatch;

	//reads vector inputs from hardcoded file
	//I know there are better ways than using the scanner, but the scanner and I go way back
	//it's just how I roll
	public static int [][] getVectors(String fileLocation) throws FileNotFoundException{
		File file = new File(fileLocation);
		Scanner scan = new Scanner(file);

		//instantiate ArrayList to store inputs
		ArrayList<String> input = new ArrayList<String>();
		while(scan.hasNextLine())
			input.add(scan.nextLine());	//add inputs to ArrayList

		//instantiate 2D boolean array
		int [][] vectors = new int[input.size()][input.get(0).length()];
		for(int i = 0; i < input.size(); i++){
			for(int j = 0; j < input.get(i).length(); j++){
				//convert 0's and 1's into boolean variables
				//then insert them into 2D array
				vectors[i][j] = Integer.parseInt(input.get(i).substring(j, j+1));
			}
		}
		scan.close();	//gotta watch them memory leaks, yo
		return vectors;
	}

	//reads propagation times from file, also hardcoded at the moment
	public static HashMap<String, Integer> getPropagationTime(String fileLocation) throws FileNotFoundException{
		HashMap<String, Integer> time = new HashMap<String, Integer>();
		File file = new File(fileLocation);
		Scanner scan = new Scanner(file);	//<3 scanner 4ever
		String line;	//String to store each line as it's read
		int count = 0;
		while(scan.hasNext()){
			line = scan.nextLine();	//store String into local variable

			//more regex matchers
			ANDMatch = AND.matcher(line);
			ORMatch = OR.matcher(line);
			NOTMatch = NOT.matcher(line);
			NORMatch = NOR.matcher(line);
			NANDMatch = NAND.matcher(line);
			XORMatch = XOR.matcher(line);
			cellMatch = cell.matcher(line);

			//another big ol' block of if/else shenanigans
			//stores each time into a HashMap for reference later on
			if(ANDMatch.find())
				time.put("AND", Integer.parseInt(line.substring(line.indexOf("(") +1, line.indexOf(")"))));
			else if(ORMatch.find())
				time.put("OR", Integer.parseInt(line.substring(line.indexOf("(") +1, line.indexOf(")"))));
			else if(NOTMatch.find())
				time.put("NOT", Integer.parseInt(line.substring(line.indexOf("(") +1, line.indexOf(")"))));
			else if(NORMatch.find())
				time.put("NOR", Integer.parseInt(line.substring(line.indexOf("(") +1, line.indexOf(")"))));
			else if(NANDMatch.find())
				time.put("NAND", Integer.parseInt(line.substring(line.indexOf("(") +1, line.indexOf(")"))));
			else if(XORMatch.find())
				time.put("XOR", Integer.parseInt(line.substring(line.indexOf("(") +1, line.indexOf(")"))));
			else if (cellMatch.find())
				time.put("CELL", Integer.parseInt(line.substring(line.indexOf("(") +1, line.indexOf(")"))));
			else{
				//if you're reading this, your input file was wonky
				System.err.println("Syntax Error on line " + count + " while parsing propagation data");
				System.exit(1);	//you killed the program, good job
			}
			count++;
		}
		time.put("INPUT", 0);
		time.put("OUTPUT", 0);
		scan.close();	//java garbage collection is, well, garbage.  Guess I'll do it myself
		return time;
	}

	//gets rid of annoying stuff like parenthesis and punctuation, and adds an identifier to the beginning
	public static ArrayList<ArrayList<String>> parse(String circuitData) throws FileNotFoundException{
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		File file = new File(circuitData);
		Scanner scan = new Scanner(file);	//scanner is life, scanner is love
		String line;
		int count = 0;	//using a while loop instead of for, have to keep count the ol' fashioned way

		while(scan.hasNextLine()){
			line = scan.nextLine();
			IOMatch = IO.matcher(line);
			labelMatch = IOLabels.matcher(line);
			gateMatch = gate.matcher(line);
			connectionMatch = connection.matcher(line);
			inputMatch = input.matcher(line);
			outputMatch = output.matcher(line);
			cellMatch = cell.matcher(line);

			//for each, remove all the punctuation and add an identifier to the front
			if(IOMatch.find()){
				data.add( parseLine(line));
				data.get(count).add(0, "IOMATCH");
			}
			else if(labelMatch.find()){
				data.add( parseLine(line));
				data.get(count).add(0, "LABELMATCH");
			}
			else if(gateMatch.find()){
				data.add( parseLine(line));
				data.get(count).add(0, "GATEMATCH");
			}
			else if(connectionMatch.find()){
				data.add( parseLine(line));
				data.get(count).add(0, "CONNECTIONMATCH");
			}
			else if(inputMatch.find()){
				data.add( parseLine(line));
				data.get(count).add(0, "INPUTMATCH");
			}
			else if(outputMatch.find()){
				data.add( parseLine(line));
				data.get(count).add(0, "OUTPUTMATCH");
			}
			else if (cellMatch.find()){
				data.add(parseLine(line));
				data.get(count).add(0, "CELLMATCH");
			}
			else{
				System.err.println("Syntax Error on line " + count + " while parsing circuit data");
				System.exit(1);
			}
			count++;
		}
		scan.close();	//goodnight 
		data.trimToSize();	//I should really eat healthier
		return data;
	}

	//removes punctuation and parses raw data into an ArrayList
	public static ArrayList<String> parseLine(String input){
		ArrayList<String> data = new ArrayList<String>();	//to store the data from this line

		//gotta get rid of that pesky punctuation
		String newString = input.substring(input.indexOf("(") +1, input.indexOf(")"));
		Scanner parse = new Scanner(newString);	//scanner does EVERYTHING
		parse.useDelimiter(", *");	//allows for whitespace too, because why the hell not

		while(parse.hasNext())
			data.add(parse.next());

		data.trimToSize();	//I'll take $500 for "things CS majors say on the bathroom scale"
		parse.close();	//There's no mercy for memory leaks in this dojo
		return data;
	}
}
