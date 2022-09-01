package patternlab.pattern.combined;

import java.util.Arrays;

import patternlab.pattern.CartesianProductMonitor;
import patternlab.pattern.SequenceMonitor;

public class CombinedMonitor extends CartesianProductMonitor
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = CombinedPattern.NAME;
	
	public CombinedMonitor()
	{
		super(new SequenceMonitor<String>(Arrays.asList("a", "b")),
				new SequenceMonitor<String>(Arrays.asList("c", "d")),
				new SequenceMonitor<String>(Arrays.asList("e", "f")));
	}
}
