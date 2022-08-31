package patternlab.pattern;

import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Stateful;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.ltl.Troolean;

/**
 * A monitor that checks if a particular sequence of events has been seen in a
 * trace, with arbitrary interleaving events.
 *
 * @param <T> The type of the events
 */
public class SequenceMonitor<T> extends SynchronousProcessor implements Stateful
{
	protected int m_index;
	
	protected final List<T> m_events;
	
	public SequenceMonitor(List<T> events)
	{
		super(1, 1);
		m_index = 0;
		m_events = events;
	}

	@Override
	public Object getState() throws UnsupportedOperationException
	{
		return m_index;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		T event = (T) inputs[0];
		if (m_index >= m_events.size())
		{
			return false;
		}
		if (event.equals(m_events.get(m_index)))
		{
			m_index++;
			if (m_index == m_events.size())
			{
				outputs.add(new Object[] {Troolean.Value.TRUE});
			}
			else
			{
				outputs.add(new Object[] {Troolean.Value.INCONCLUSIVE});
			}
		}
		return true;
	}

	@Override
	public SequenceMonitor<T> duplicate(boolean with_state)
	{
		SequenceMonitor<T> seq_mon = new SequenceMonitor<T>(m_events);
		copyInto(seq_mon, with_state);
		return seq_mon;
	}
	
	protected void copyInto(SequenceMonitor<T> mon, boolean with_state)
	{
		if (with_state)
		{
			mon.m_index = m_index;
		}
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_index = 0;
	}
}
