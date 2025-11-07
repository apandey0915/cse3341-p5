class Main {
	public static void main(String[] args) {
		// Initialize the scanner with the input file
		CoreScanner S = new CoreScanner(args[0]);
		Parser.scanner = S;
		
		Procedure p = new Procedure();
		
		p.parse();
		
		Memory.data = new CoreScanner(args[1]);
		
		p.execute();
	}
}