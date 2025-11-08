import java.util.*;

class Memory {
	//scanner is stored here as a static field so it is avaiable to the execute method for factor
	public static CoreScanner data;
	
	static class HeapObject {
		String defaultKey;
		Map<String,Integer> map = new HashMap<>();
		int rc = 0;
	}

	static class Variable {
		Core type;
		int integerVal;
		HeapObject obj;
		Variable(Core t) {
			this.type = t;
		}
	}
	
	public static HashMap<String, Variable> global;
	public static Stack<Stack<HashMap<String, Variable>>> local;
	public static HashMap<String, Function> funcMap;

	private static int reachableCount = 0;

	private static void incRef(HeapObject o) {
		if (o == null) return;
		if (o.rc == 0) {
			reachableCount++;
			System.out.println("gc:" + reachableCount);
		}
		o.rc++;
	}
	private static void decRef(HeapObject o) {
		if (o == null) return;
		o.rc--;
		if (o.rc == 0) {
			reachableCount--;
			System.out.println("gc:" + reachableCount);
		}
	}
	
	// Helper methods to manage memory
	
	// Inializes and clear the global memory structures
	// Called by Procedure before executing the DeclSeq
	public static void initializeGlobal() {
		global = new HashMap<String, Variable>();
		funcMap = new HashMap<String, Function>();
		reachableCount = 0;
	}
	
	// Called at end of Procedure
	public static void clearGlobal() {
		if (global != null) {
			for (Variable v : global.values()) {
				if (v.type == Core.OBJECT && v.obj != null) {
					decRef(v.obj);
				}
			}
		}
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
		HashMap<String, Variable> scope = local.peek().pop();
		for (Variable v : scope.values()) {
			if (v.type == Core.OBJECT && v.obj != null) {
				decRef(v.obj);
			}
		}
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
		Variable v = getLocalOrGlobal(id);
		if (v.type == Core.INTEGER) {
			return v.integerVal;
		}
		if (v.obj == null) {
			System.out.println("ERROR: Null object access: " + id);
			System.exit(0);
		}
		Integer val = v.obj.map.get(v.obj.defaultKey);
		return val == null ? 0 : val;
	}
	
	// Retrieves a value using the key
	public static int load(String id, String key) {
		Variable v = getLocalOrGlobal(id);
		if (v.type != Core.OBJECT || v.obj == null) {
			System.out.println("ERROR: Null object access: " + id);
			System.exit(0);
		}
		Integer val = v.obj.map.get(key);
		return val == null ? 0 : val;
	}
	
	// Stores a value (integer or map at default key)
	public static void store(String id, int value) {
		Variable v = getLocalOrGlobal(id);
		if (v.type == Core.INTEGER) {
			v.integerVal = value;
		} else {
			if (v.obj == null) {
				System.out.println("ERROR: Null object store: " + id);
				System.exit(0);
			}
			v.obj.map.put(v.obj.defaultKey, value);
		}
	}
	
	// Stores a value at key
	public static void store(String id, String key, int value) {
		Variable v = getLocalOrGlobal(id);
		if (v.type != Core.OBJECT || v.obj == null) {
			System.out.println("ERROR: Null object store: " + id);
			System.exit(0);
		}
		v.obj.map.put(key, value);
	}
	
	// Handles "new object" assignment
	public static void allocate(String id, String key, int value) {
		Variable v = getLocalOrGlobal(id);
		if (v.type != Core.OBJECT) {
			System.out.println("ERROR: allocate on non-object: " + id);
			System.exit(0);
		}
		if (v.obj != null) {
			decRef(v.obj);
		}

		HeapObject o = new HeapObject();
		o.defaultKey = key;
		o.map.put(key, value);
		incRef(o);
		v.obj = o;
	}
	
	// Handles "id : id" assignment
	public static void alias(String lhs, String rhs) {
		Variable v1 = getLocalOrGlobal(lhs);
		Variable v2 = getLocalOrGlobal(rhs);
		if (v1.type != Core.OBJECT || v2.type != Core.OBJECT) {
			System.out.println("ERROR: alias requires object variables: " + lhs + ", " + rhs);
			System.exit(0);
		}
		if (v1.obj != null) {
			decRef(v1.obj);
		}
		v1.obj = v2.obj;
		incRef(v1.obj);
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
			Variable v2;
			if (v1.type == Core.OBJECT) {
				v2 = new Variable(Core.OBJECT);
				v2.obj = v1.obj;
				incRef(v2.obj);
			} else {
				v2 = new Variable(Core.INTEGER);
				v2.integerVal = v1.integerVal;
			}
			frame.peek().put(formals.get(i), v2);
		}
		local.push(frame);	 
		f.ss.execute();
	}
	public static void popFrame() {
		Stack<HashMap<String, Variable>> frame = local.pop();
		while (!frame.isEmpty()) {
			HashMap<String, Variable> scope = frame.pop();
			for (Variable v : scope.values()) {
				if (v.type == Core.OBJECT && v.obj != null) {
					decRef(v.obj);
				}
			}
		}
	}
}