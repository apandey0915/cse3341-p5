import java.util.*;

class Function {
	String name;
	Parameter param;
	StmtSeq ss;
	
	void parse() {
		Parser.expectedToken(Core.PROCEDURE);
		Parser.scanner.nextToken();
		Parser.expectedToken(Core.ID);
		name = Parser.scanner.getId();
		Parser.scanner.nextToken();
		Parser.expectedToken(Core.LPAREN);
		Parser.scanner.nextToken();
		Parser.expectedToken(Core.OBJECT);
		Parser.scanner.nextToken();
		param = new Parameter();
		param.parse();
		Parser.expectedToken(Core.RPAREN);
		Parser.scanner.nextToken();
		Parser.expectedToken(Core.IS);
		Parser.scanner.nextToken();
		ss = new StmtSeq();
		ss.parse();
		Parser.expectedToken(Core.END);
		Parser.scanner.nextToken();
	}
	
	void print(int indent) {
		System.out.print("procedure " + name + " (");
		param.print();
		System.out.println(") is ");
		ss.print(indent+1);
		System.out.println("end");
	}
	
	void execute() {
		// Check if duplicate function name
		if (Memory.funcMap.containsKey(name)) {
			System.out.println("ERROR: " + name + " is a duplicate function!");
			System.exit(1);
		}
		
		// Store function in funcMap so we can use it is Call.execute
		Memory.funcMap.put(name, this);
		
		// Check for duplicated in the formal parameters
		List<String> fp = param.execute();
		HashSet<String> fpSet = new HashSet<String>(fp);
		if (fp.size() != fpSet.size()) {
			System.out.println("ERROR: Function " + name + " has duplicate formal paramters!");
			System.exit(1);
		}
	}
}