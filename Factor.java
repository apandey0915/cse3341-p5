class Factor {
	Id id;
	String key;
	int constant;
	Expr expr;
	
	void parse() {
		if (Parser.scanner.currentToken() == Core.ID) {
			id = new Id();
			id.parse();
			if (Parser.scanner.currentToken() == Core.LSQUARE) {
				Parser.scanner.nextToken();
				key = Parser.scanner.getString();
				Parser.scanner.nextToken();
				Parser.expectedToken(Core.RSQUARE);
				Parser.scanner.nextToken();
			}
		} else if (Parser.scanner.currentToken() == Core.CONST) {
			constant = Parser.scanner.getConst();
			Parser.scanner.nextToken();
		} else if (Parser.scanner.currentToken() == Core.LPAREN) {
			Parser.scanner.nextToken();
			expr = new Expr();
			expr.parse();
			Parser.expectedToken(Core.RPAREN);
			Parser.scanner.nextToken();
		} else {
			System.out.println("ERROR: Expected ID, CONST, LPAREN, recieved " + Parser.scanner.currentToken());
			System.exit(0);
		}
	}
	
	void print() {
		if (id != null) {
			id.print();
			if (key != null) {
				System.out.print("['" + key + "']");
			}
		} else if (expr != null) {
			System.out.print("(");
			expr.print();
			System.out.print(")");
		} else {
			System.out.print(constant);
		}
	}
	
	int execute() {
		int value;
		if (id != null && key != null) {
			value = Memory.load(id.getId(), key);
		} else if (id != null) {
			value = Memory.load(id.getId());
		} else if (expr != null) {
			value = expr.execute();
		} else if (constant == -1) {
			if (Memory.data != null && Memory.data.currentToken() == Core.CONST) {
				value = Memory.data.getConst();
				Memory.data.nextToken();
			} else {
				value = -1;
				System.out.println("ERROR: Data file not provided or out of values!");
				System.exit(1);
			}
		} else {
			value = constant;
		}
		return value;
	}
}