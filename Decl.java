class Decl implements Stmt {
	DeclInteger declInt;
	DeclObject declObj;
	
	public void parse() {
		if (Parser.scanner.currentToken() == Core.INTEGER) {
			declInt = new DeclInteger();
			declInt.parse();
		} else {
			declObj = new DeclObject();
			declObj.parse();
		}
	}
	
	public void print(int indent) {
		if (declInt != null) {
			declInt.print(indent);
		} else {
			declObj.print(indent);
		}
	}
	
	public void execute() {
		if (declInt != null) {
			declInt.execute();
		} else {
			declObj.execute();
		}
	}
}