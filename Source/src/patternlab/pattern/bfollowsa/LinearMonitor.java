package patternlab.pattern.bfollowsa;

import java.util.List;

import ca.uqac.lif.cep.EventTracker;
import patternlab.monitor.AtomicSequence;

public class LinearMonitor<T> extends AtomicSequence<T>
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = LinearPattern.NAME;
	
	protected final List<T> m_elements;
	
	public LinearMonitor(EventTracker tracker, List<T> elements)
	{
		super(tracker, elements);
		m_elements = elements;
	}
}
