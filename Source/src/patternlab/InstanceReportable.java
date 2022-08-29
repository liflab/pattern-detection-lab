package patternlab;

public interface InstanceReportable
{
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
}
