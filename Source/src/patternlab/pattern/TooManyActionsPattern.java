package patternlab.pattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uqac.lif.synthia.NoMoreElementException;
import ca.uqac.lif.synthia.Picker;

public class TooManyActionsPattern implements Picker<List<Tuple>>, Notifiable<Tuple>
{
	protected static int s_idCounter = 10000;
	
	protected int m_id;
	
	protected List<String> m_totalPayloads;
	
	protected Set<String> m_availablePayloads;
	
	public TooManyActionsPattern(List<String> total_payloads)
	{
		super();
		m_id = s_idCounter;
		s_idCounter++;
		m_totalPayloads = total_payloads;
		m_availablePayloads = new HashSet<String>();
		m_availablePayloads.addAll(total_payloads);
	}
	
	protected TooManyActionsPattern(int id, List<String> total, Set<String> available)
	{
		super();
		m_id = id;
		m_totalPayloads = total;
		m_availablePayloads = new HashSet<String>();
		m_availablePayloads.addAll(available);
	}

	@Override
	public void notifyEvent(List<Tuple> list)
	{
		for (Tuple t : list)
		{
			notify(t);
		}
	}
	
	protected void notify(Tuple t)
	{
		if (t.m_id == m_id)
		{
			m_availablePayloads.remove(t.m_payload);
		}
	}

	@Override
	public Picker<List<Tuple>> duplicate(boolean with_state)
	{
		if (with_state)
		{
			return new TooManyActionsPattern(m_id, m_totalPayloads, m_availablePayloads);
		}
		return new TooManyActionsPattern(m_totalPayloads);
	}

	@Override
	public List<Tuple> pick()
	{
		if (m_availablePayloads.isEmpty())
		{
			throw new NoMoreElementException();
		}
		String chosen = null;
		for (String s : m_availablePayloads)
		{
			chosen = s;
			break;
		}
		m_availablePayloads.remove(chosen);
		List<Tuple> list = new ArrayList<Tuple>(1);
		list.add(new Tuple(m_id, chosen));
		return list;
	}

	@Override
	public void reset()
	{
		m_availablePayloads.clear();
		m_availablePayloads.addAll(m_totalPayloads);
	}
}
