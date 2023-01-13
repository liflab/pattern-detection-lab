package patternlab;

public interface Monitor
{
	/**
	 * A string indicating that the processor only keeps monitor instances
	 * in distinct states.
	 */
	public static final String DISTINCT_STATES = "Distinct states";
	
	/**
	 * A string indicating that the processor only keeps progressing
	 * subsequences.
	 */
	public static final String PROGRESSING = "Progressing";
	
	/**
	 * A string indicating that the processor only keeps monitor instances that
	 * move out of their initial state on the first event consumed.
	 */
	public static final String FIRST_STEP = "First step";
	
	/**
	 * A string indicating that all monitor instances are kept until they produce
	 * a definite verdict.
	 */
	public static final String DIRECT = "Direct";
	
	/**
	 * Gets the number of monitor instances currently active inside the
	 * processor.
	 * @return The number of monitor instances
	 */
	public int getInstances();
	
	public void setRemoveNonMatches(boolean b);
	
	public void setRemoveImmobileOnStart(boolean b);
	
	public void setRemoveNonProgressing(boolean b);
	
	public void setRemoveSameState(boolean b);
	
	public void setSpawn(boolean b);
}
