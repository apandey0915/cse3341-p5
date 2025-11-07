class Id {
	String identifier;
	
	void parse() {
		Parser.expectedToken(Core.ID);
		identifier = Parser.scanner.getId();
		Parser.scanner.nextToken();
	}
	
	void print() {
		System.out.print(identifier);
	}
	
	String getId() {
		return identifier;
	}
}