class Call implements Stmt {
	String name;
	Parameter param;
	
	public void parse() {
		Parser.expectedToken(Core.BEGIN);
		Parser.scanner.nextToken();
		Parser.expectedToken(Core.ID);
		name = Parser.scanner.getId();
		Parser.scanner.nextToken();
		Parser.expectedToken(Core.LPAREN);
		Parser.scanner.nextToken();
		param = new Parameter();
		param.parse();
		Parser.expectedToken(Core.RPAREN);
		Parser.scanner.nextToken();
		Parser.expectedToken(Core.SEMICOLON);
		Parser.scanner.nextToken();
	}
	
	public void print(int indent) {
		System.out.print("begin " + name + "(");
		param.print();
		System.out.print(");");
	}
	
	public void execute() {
		// Check if function is declated
		if (!Memory.funcMap.containsKey(name)) {
			System.out.println("ERROR: " + name + " is not declared as a function!");
			System.exit(1);
		}
		Memory.pushFrameAndExecute(name, param);
		Memory.popFrame();
		
	}
}