class DeclSeq {
	Decl decl;
	Function func;
	DeclSeq ds;
	
	void parse() {
		if (Parser.scanner.currentToken() == Core.PROCEDURE) {
			func = new Function();
			func.parse();
		} else {
			decl = new Decl();
			decl.parse();
		}
		if (Parser.scanner.currentToken() != Core.BEGIN) {
			ds = new DeclSeq();
			ds.parse();
		}
	}
	
	void print(int indent) {
		if (func != null) {
			func.print(indent);
		} else {
			decl.print(indent);
		}
		if (ds != null) {
			ds.print(indent);
		}
	}
	
	void execute() {
		if (func != null) {
			func.execute();
		} else {
			decl.execute();
		}
		if (ds != null) {
			ds.execute();
		}
	}
}