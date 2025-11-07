class StmtSeq {
	Stmt stmt;
	StmtSeq ss;
	
	void parse() {
		if (Parser.scanner.currentToken() == Core.ID) {
			stmt = new Assign();
		} else if (Parser.scanner.currentToken() == Core.PRINT) {
			stmt = new Print();
		} else if (Parser.scanner.currentToken() == Core.READ) {
			stmt = new Read();
		} else if (Parser.scanner.currentToken() == Core.IF) {
			stmt = new If();
		} else if (Parser.scanner.currentToken() == Core.FOR) {
			stmt = new Loop();
		}   else if (Parser.scanner.currentToken() == Core.INTEGER || Parser.scanner.currentToken() == Core.OBJECT) {
			stmt = new Decl();
		} else if (Parser.scanner.currentToken() == Core.BEGIN) {
			stmt = new Call();
		} else {
			System.out.println("ERROR: Bad start to statement: " + Parser.scanner.currentToken());
			System.exit(0);
		}
		stmt.parse();
		if (Parser.scanner.currentToken() != Core.END && Parser.scanner.currentToken() != Core.ELSE) {
			ss = new StmtSeq();
			ss.parse();
		}
	}
			
	void print(int indent) {
		stmt.print(indent);
		if (ss != null) {
			ss.print(indent);
		}
	}
	
	void execute() {
		stmt.execute();
		if (ss != null) {
			ss.execute();
		}
	}
}