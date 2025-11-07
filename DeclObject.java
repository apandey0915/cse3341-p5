class DeclObject {
	Id id;
	
	public void parse() {
		Parser.expectedToken(Core.OBJECT);
		Parser.scanner.nextToken();
		id = new Id();
		id.parse();
		Parser.expectedToken(Core.SEMICOLON);
		Parser.scanner.nextToken();
	}
	
	public void print(int indent) {
		for (int i=0; i<indent; i++) {
			System.out.print("\t");
		}
		System.out.print("object ");
		id.print();
		System.out.println(";");
	}
	
	public void execute() {
		Memory.declareObject(id.getId());
	}
}