package patternlab.pattern.bfollowsa;

import patternlab.monitor.AtomicSequence;

public class BFollowsAMonitor extends AtomicSequence
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = BFollowsAPattern.NAME;
	
	public BFollowsAMonitor()
	{
		super("a", "b");
	}
}
