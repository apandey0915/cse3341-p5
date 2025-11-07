class Cond {
	Cmpr cmpr;
	Cond cond;
	int option;
	
	void parse() {
		option = 0;
		if (Parser.scanner.currentToken() == Core.NOT){
			option = 1;
			Parser.scanner.nextToken();
			cond = new Cond();
			cond.parse();
		} else if (Parser.scanner.currentToken() == Core.LSQUARE) {
			option = 2;
			Parser.scanner.nextToken();
			cond = new Cond();
			cond.parse();
			Parser.expectedToken(Core.RSQUARE);
			Parser.scanner.nextToken();
		} else {
			cmpr = new Cmpr();
			cmpr.parse();
			if (Parser.scanner.currentToken() == Core.OR) {
				option = 3;
				Parser.scanner.nextToken();
				cond = new Cond();
				cond.parse();
			} else if (Parser.scanner.currentToken() == Core.AND) {
				option = 4;
				Parser.scanner.nextToken();
				cond = new Cond();
				cond.parse();
			}
		}
	}
	
	void print() {
		if (option == 1) {
			System.out.print("not ");
			cond.print();
		} else if (option == 2) {
			System.out.print("[");
			cond.print();
			System.out.print("]");
		}else {
			cmpr.print();
			if (cond != null) {
				if (option == 3) System.out.print(" or ");
				if (option == 4) System.out.print(" and ");
				cond.print();
			}
		}
	}
	
	boolean execute() {
		boolean result;
		if (option == 1) {
			result = !cond.execute();
		} else if (option == 2) {
			result = cond.execute();
		} else {
			result = cmpr.execute();
			if (option == 3) {
				result = result || cond.execute();
			} else if (option == 4) {
				result = result && cond.execute();
			}
		}
		return result;
	}
}