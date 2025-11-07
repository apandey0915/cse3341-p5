class Print implements Stmt {
	Expr expr;
	
	public void parse() {
		Parser.expectedToken(Core.PRINT);
		Parser.scanner.nextToken();
		Parser.expectedToken(Core.LPAREN);
		Parser.scanner.nextToken();
		expr = new Expr();
		expr.parse();
		Parser.expectedToken(Core.RPAREN);
		Parser.scanner.nextToken();
		Parser.expectedToken(Core.SEMICOLON);
		Parser.scanner.nextToken();
	}
	
	public void print(int indent) {
		for (int i=0; i<indent; i++) {
			System.out.print("\t");
		}
		System.out.print("print(");
		expr.print();
		System.out.println(");");
	}
	
	public void execute() {
		System.out.println(expr.execute());
	}
}