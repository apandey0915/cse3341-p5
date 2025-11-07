import java.util.*;

class Parameter {
	String identifier;
	Parameter param;
	
	void parse() {
		Parser.expectedToken(Core.ID);
		identifier = Parser.scanner.getId();
		Parser.scanner.nextToken();
		if (Parser.scanner.currentToken() == Core.COMMA) {
			Parser.scanner.nextToken();
			param = new Parameter();
			param.parse();
		}
	}
	
	void print() {
		System.out.print(identifier);
		if (param != null) {
			System.out.print(", ");
			param.print();
		}
	}
	
	ArrayList<String> execute() {
		ArrayList<String> result;
		if (param == null) {
			result = new ArrayList<String>();
		} else {
			result = param.execute();
		}
		result.add(identifier);
		return result;
	}
}