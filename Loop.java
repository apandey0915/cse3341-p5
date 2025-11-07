import java.util.*;

class Loop implements Stmt {
	Id id;
	Expr initial;
	Cond cond;
	Expr update;
	StmtSeq ss;
	
	public void parse() {
		Parser.scanner.nextToken();
		Parser.expectedToken(Core.LPAREN);
		Parser.scanner.nextToken();
		Parser.expectedToken(Core.ID);
		id = new Id();
		id.parse();
		Parser.expectedToken(Core.ASSIGN);
		Parser.scanner.nextToken();
		initial = new Expr();
		initial.parse();
		Parser.expectedToken(Core.SEMICOLON);
		Parser.scanner.nextToken();
		cond = new Cond();;
		cond.parse();
		Parser.expectedToken(Core.SEMICOLON);
		Parser.scanner.nextToken();
		update = new Expr();
		update.parse();
		Parser.expectedToken(Core.RPAREN);
		Parser.scanner.nextToken();
		Parser.expectedToken(Core.DO);
		Parser.scanner.nextToken();
		ss = new StmtSeq();
		ss.parse();
		Parser.expectedToken(Core.END);
		Parser.scanner.nextToken();
	}
	
	public void print(int indent) {
		for (int i=0; i<indent; i++) {
			System.out.print("\t");
		}
		System.out.print("for (");
		id.print();
		System.out.print("=");
		initial.print();
		System.out.print(";");
		cond.print();
		System.out.print(";");
		update.print();
		System.out.println(") do");
		ss.print(indent+1);
		for (int i=0; i<indent; i++) {
			System.out.print("\t");
		}
		System.out.println("end");
	}
	
	public void execute() {
		Memory.store(id.getId(), initial.execute());
		while (cond.execute()) {
			Memory.pushScope();
			ss.execute();
			Memory.popScope();
			Memory.store(id.getId(), update.execute());
		} 
	}
}