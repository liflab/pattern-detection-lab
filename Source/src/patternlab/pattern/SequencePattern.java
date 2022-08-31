package patternlab.pattern;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.synthia.NoMoreElementException;
import ca.uqac.lif.synthia.Picker;

public class SequencePattern<T> implements Picker<List<T>>, Notifiable<T>
{
	protected int m_index;
	
	protected final List<T> m_events;
	
	public SequencePattern(List<T> events)
	{
		super();
		m_index = 0;
		m_events = events;
	}

	@Override
	public void notifyEvent(List<T> list)
	{
		for (T event : list)
		{
			notifyEvent(event);
		}
	}
	
	protected void notifyEvent(T e)
	{
		if (m_index < m_events.size() && e.equals(m_events.get(m_index)))
		{
			m_index++;
		}
	}

	@Override
	public SequencePattern<T> duplicate(boolean with_state)
	{
		SequencePattern<T> sp = new SequencePattern<T>(m_events);
		copyInto(sp, with_state);
		return sp;
	}
	
	protected void copyInto(SequencePattern<T> sp, boolean with_state)
	{
		if (with_state)
		{
			sp.m_index = m_index;
		}
	}

	@Override
	public List<T> pick()
	{
		if (m_index >= m_events.size())
		{
			throw new NoMoreElementException();
		}
		List<T> out = new ArrayList<T>(1);
		out.add(m_events.get(m_index));
		m_index++;
		return out;
	}

	@Override
	public void reset()
	{
		m_index = 0;
	}
}
