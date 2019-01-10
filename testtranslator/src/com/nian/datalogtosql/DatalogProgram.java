package com.nian.datalogtosql;

public class DatalogProgram {

	protected String input;
	// protected Mapping mapping;

	public DatalogProgram(String input) {
		// input passed here is actually being used
		this.input = input;

		// mapping is used to evaluate Datalog program, which will be used later
		/*
		 * InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		 * MappingParser parser = new MappingParser(inputStream); Mapping mapping =
		 * null; try { mapping = parser.mapping(); } catch (ParseException e) {
		 * e.printStackTrace(); } this.mapping = mapping;
		 */
	}

	public ProgramType getProgramType() {

		// facts/rules
		if (isPositive())
			return ProgramType.POSITIVE;
		// negate EDB
		if (isSemiPositive())
			return ProgramType.SEMI_POSITIVE;

		// negate IDB
		boolean stratifiable = true;

		// A datalog program P is stratifiable if its precedence graph GP has no cycle
		// containing a negative edge.
		// to be used later
		// now do nothing regarding ProgramType: POSITIVE, SEMI_POSITIVE, STRATIFIABLE
		/*
		 * DirectedGraph<String, SignedEdge> graph = generateMappingGraph();
		 * 
		 * CycleDetector<String, SignedEdge> cycleDetector = new CycleDetector<String,
		 * SignedEdge>(graph); Set<String> nodesInCycle = cycleDetector.findCycles();
		 * 
		 * for (String node : nodesInCycle) { for (String checkNode : nodesInCycle) { if
		 * (checkNode.equals(node)) {
		 * 
		 * if (graph.containsEdge(node, checkNode)) { SignedEdge edge =
		 * graph.getEdge(node, checkNode);
		 * 
		 * if (!edge.getSign()) { stratifiable = false; } } } } }
		 */

		if (stratifiable) {
			return ProgramType.STRATIFIABLE;

		} else {
			return ProgramType.UNKNOW;
		}
	}

	private boolean isPositive() {
		/*
		 * for (Tgd t : mapping.getTgds()) { for (Literal l : t.getLeft()) { if
		 * (!l.getFlag()) { return false; } }
		 * 
		 * }
		 */
		return true;
	}

	private boolean isSemiPositive() {
		/*
		 * boolean semiPositive = false; for (Tgd t : mapping.getTgds()) { for (Literal
		 * l : t.getLeft()) { if (!l.getFlag()) { semiPositive = false; for (Relation r
		 * : mapping.getEDB()) { if (r.getName().equals(l.getAtom().getName())) {
		 * semiPositive = true; } } } }
		 * 
		 * }
		 * 
		 * return semiPositive;
		 */
		return false;
	}

	// not using this framework to parse strings, parse directly instead; might use
	// the framework later, if more complicated work is on the way
	/*
	 * private Set<String> getAllPredicates() {
	 * 
	 * Set<String> set = new HashSet<String>();
	 * 
	 * for (Relation r : mapping.getEDB()) { set.add(r.getName()); } for
	 * (AbstractRelation r : mapping.getIDB()) { set.add(r.getName()); }
	 * 
	 * for (Tgd t : mapping.getTgds()) {
	 * 
	 * set.add(t.getRight().getName()); for (Literal l : t.getLeft()) {
	 * set.add(l.getAtom().getName()); } }
	 * 
	 * return set;
	 * 
	 * }
	 */

	/*
	 * private DirectedGraph<String, SignedEdge> generateMappingGraph() {
	 * 
	 * DirectedGraph<String, SignedEdge> g = new DefaultDirectedGraph<String,
	 * SignedEdge>(SignedEdge.class);
	 * 
	 * for (Tgd t : mapping.getTgds()) {
	 * 
	 * t.getRight().getName(); if (!g.containsVertex(t.getRight().getName())) {
	 * g.addVertex(t.getRight().getName()); }
	 * 
	 * for (Literal l : t.getLeft()) {
	 * 
	 * String exprName = l.getAtom().getName(); if (!g.containsVertex(exprName)) {
	 * g.addVertex(exprName); }
	 * 
	 * SignedEdge edge = new SignedEdge(l.getFlag());
	 * 
	 * g.addEdge(exprName, t.getRight().getName(), edge);
	 * 
	 * } }
	 * 
	 * return g;
	 * 
	 * }
	 */

	/*
	 * public Mapping getMapping() { return mapping; }
	 */
	/*
	 * private class SignedEdge { private boolean sign;
	 * 
	 * public SignedEdge(boolean sign) { this.sign = sign; }
	 * 
	 * public boolean getSign() { return sign; } }
	 */

}
