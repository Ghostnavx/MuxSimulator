public enum GateType
{
	AND,
	OR,
	NOT,
	NAND,
	NOR,
	XOR,
	//added input and output as a gate type
	INPUT,
	OUTPUT;
	static GateType getTypeFromString(String type)
	{
		if (type.toUpperCase().trim().equals("AND"))
			return AND;
		else if (type.toUpperCase().trim().equals("OR"))
			return OR;
		else if (type.toUpperCase().trim().equals("NOT"))
			return NOT;
		else if (type.toUpperCase().trim().equals("NAND"))
			return NAND;
		else if (type.toUpperCase().trim().equals("NOR"))
			return NOR;
		else if (type.toUpperCase().trim().equals("INPUT"))
			return INPUT;
		else if (type.toUpperCase().trim().equals("OUTPUT"))
			return OUTPUT;
		else if(type.toUpperCase().trim().equals("XOR"))
			return XOR;
		//somebody didn't type in the input correctly
		else{
			System.err.println("You dun messed up son!  " + type.toUpperCase().trim() + " isn't a valid gate type!");
			System.exit(1);
			return null;	//stupid compiler making me put this in even though it'll never be reached
		}
	}
}
