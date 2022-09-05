package patternlab.pattern.toomanyactions;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.synthia.NoMoreElementException;
import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.random.RandomInteger;
import patternlab.pattern.Notifiable;

public class TooManyActionsPattern implements Picker<List<Tuple>>, Notifiable<Tuple>
{
	public static final String NAME = "Port scanning";
	
	public static final String P_PAYLOADS = "Number of payloads";
	
	protected static int s_idCounter = 1000001;
	
	protected int m_id;
	
	protected List<String> m_totalPayloads;
	
	protected List<String> m_availablePayloads;
	
	protected List<String> m_emittedPayloads;
	
	protected Picker<Boolean> m_repeat;
	
	protected RandomInteger m_indexPicker;
	
	public TooManyActionsPattern(List<String> total_payloads, Picker<Boolean> repeat, RandomInteger index_picker)
	{
		super();
		m_id = s_idCounter;
		s_idCounter++;
		m_totalPayloads = total_payloads;
		m_availablePayloads = new ArrayList<String>();
		m_availablePayloads.addAll(total_payloads);
		m_emittedPayloads = new ArrayList<String>();
		m_repeat = repeat;
		m_indexPicker = index_picker;
	}
	
	protected TooManyActionsPattern(int id, List<String> total, List<String> available, List<String> emitted, Picker<Boolean> repeat, RandomInteger index_picker)
	{
		super();
		m_id = id;
		m_totalPayloads = total;
		m_availablePayloads = new ArrayList<String>();
		m_availablePayloads.addAll(available);
		m_emittedPayloads = new ArrayList<String>();
		m_emittedPayloads.addAll(emitted);
		m_repeat = repeat;
		m_indexPicker = index_picker;
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
			m_emittedPayloads.add(t.m_payload);
		}
	}

	@Override
	public Picker<List<Tuple>> duplicate(boolean with_state)
	{
		if (with_state)
		{
			return new TooManyActionsPattern(m_id, m_totalPayloads, m_availablePayloads, m_emittedPayloads, m_repeat.duplicate(true), m_indexPicker.duplicate(true));
		}
		return new TooManyActionsPattern(m_totalPayloads, m_repeat.duplicate(false), m_indexPicker.duplicate(false));
	}

	@Override
	public List<Tuple> pick()
	{
		List<String> pick_from = (m_repeat.pick() && !m_emittedPayloads.isEmpty() ? m_emittedPayloads : m_availablePayloads);
		if (pick_from.isEmpty())
		{
			throw new NoMoreElementException();
		}
		m_indexPicker.setInterval(0, pick_from.size());
		String chosen = pick_from.get(m_indexPicker.pick()); 
		m_availablePayloads.remove(chosen);
		m_emittedPayloads.add(chosen);
		List<Tuple> list = new ArrayList<Tuple>(1);
		Tuple tuple = new Tuple(m_id, chosen);
		list.add(tuple);
		//System.out.println(tuple);
		return list;
	}

	@Override
	public void reset()
	{
		m_availablePayloads.clear();
		m_availablePayloads.addAll(m_totalPayloads);
		m_emittedPayloads.clear();
	}
}
