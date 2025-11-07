import java.util.*;

class Memory {
	//scanner is stored here as a static field so it is avaiable to the execute method for factor
	public static CoreScanner data;
	
	// Class and data structures to represent variables
	static class Variable {
		Core type;
		int integerVal;
		String defaultKey;
		Map<String, Integer> mapVal;
		Variable(Core t) {
			this.type = t;
		}
	}
	
	public static HashMap<String, Variable> global;
	public static Stack<Stack<HashMap<String, Variable>>> local;

	
	public static HashMap<String, Function> funcMap;
	
	// Helper methods to manage memory
	
	// Inializes and clear the global memory structures
	// Called by Procedure before executing the DeclSeq
	public static void initializeGlobal() {
		global = new HashMap<String, Variable>();
		funcMap = new HashMap<String, Function>();
	}
	
	// Called at end of Procedure
	public static void clearGlobal() {
		global = null;
		funcMap = null;
	}
	
	// Initializes the local data structure
	// Called before executing the main StmtSeq
	public static void initializeLocal() {
		local = new Stack<Stack<HashMap<String, Variable>>>();
		local.push(new Stack<HashMap<String, Variable>>());
		local.peek().push(new HashMap<String, Variable>());
	}
	
	// Pushes a "scope" for if/loop stmts
	public static void pushScope() {
		local.peek().push(new HashMap<String, Variable>());
	}
	
	// Pops a "scope"
	public static void popScope() {
		local.peek().pop();
	}
	
	// Handles decl integer
	public static void declareInteger(String id) {
		Variable v = new Variable(Core.INTEGER);
		if (local != null) {
			local.peek().peek().put(id, v);
		} else {
			global.put(id, v);
		}
	}
	
	// Handles decl object
	public static void declareObject(String id) {
		Variable v = new Variable(Core.OBJECT);
		if (local != null) {
			local.peek().peek().put(id, v);
		} else {
			global.put(id, v);
		}
	}
	
	// Retrives a value from memory (integer or array at index 0)
	public static int load(String id) {
		int value;
		Variable v = getLocalOrGlobal(id);
		if (v.type == Core.INTEGER) {
			value = v.integerVal;
		} else {
			value = v.mapVal.get(v.defaultKey);
		}
		return value;
	}
	
	// Retrieves a value using the key
	public static int load(String id, String key) {
		Variable v = getLocalOrGlobal(id);
		return v.mapVal.get(key);
	}
	
	// Stores a value (integer or map at default key)
	public static void store(String id, int value) {
		Variable v = getLocalOrGlobal(id);
		if (v.type == Core.INTEGER) {
			v.integerVal = value;
		} else {
			v.mapVal.put(v.defaultKey, value);
		}
	}
	
	// Stores a value at key
	public static void store(String id, String key, int value) {
		Variable v = getLocalOrGlobal(id);
		v.mapVal.put(key, value);
	}
	
	// Handles "new object" assignment
	public static void allocate(String id, String key, int value) {
		Variable v = getLocalOrGlobal(id);
		v.mapVal = new HashMap<>();
		v.defaultKey = key;
		v.mapVal.put(v.defaultKey, value);
	}
	
	// Handles "id : id" assignment
	public static void alias(String lhs, String rhs) {
		Variable v1 = getLocalOrGlobal(lhs);
		Variable v2 = getLocalOrGlobal(rhs);
		v1.mapVal = v2.mapVal;
		v1.defaultKey = v2.defaultKey;
	}
	
	// Looks up value of the variables, searches local then global
	private static Variable getLocalOrGlobal(String id) {
		Variable result;
		if (local.peek().size() > 0) {
			if (local.peek().peek().containsKey(id)) {
				result = local.peek().peek().get(id);
			} else {
				HashMap<String, Variable> temp = local.peek().pop();
				result = getLocalOrGlobal(id);
				local.peek().push(temp);
			}
		} else {
			result = global.get(id);
		}
		return result;
	}
	
	
	/*
	 *
	 * New methods for pushing/popping frames
	 *
	 */

	 public static void pushFrameAndExecute(String name, Parameter args) {
		 Function f = funcMap.get(name);
		 
		 ArrayList<String> formals = f.param.execute();
		 ArrayList<String> arguments = args.execute();
		 
		 Stack<HashMap<String, Variable>> frame = new Stack<HashMap<String, Variable>>();
		 frame.push(new HashMap<String, Variable>());
		 
		 for (int i=0; i<arguments.size(); i++) {
			 Variable v1 = getLocalOrGlobal(arguments.get(i));
			 Variable v2 = new Variable(Core.OBJECT);
			 v2.mapVal = v1.mapVal;
			 v2.defaultKey = v1.defaultKey;
			 frame.peek().put(formals.get(i), v2);
		 }
		 
		 local.push(frame);
		 
		 f.ss.execute();
	 }
	 
	 public static void popFrame() {
		 local.pop();
	 }
	 
	 
}