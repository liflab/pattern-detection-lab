package patternlab.pattern;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.random.RandomInteger;

public class NormalActionsPattern implements Picker<List<Tuple>>, Notifiable<Tuple>
{
	protected final List<String> m_totalPayloads;
	
	protected final RandomInteger m_indexPicker;
	
	protected final Picker<Integer> m_idPicker;
	
	public NormalActionsPattern(List<String> total_payloads, Picker<Integer> id_picker, int seed)
	{
		super();
		m_totalPayloads = total_payloads;
		m_indexPicker = new RandomInteger(0, m_totalPayloads.size()).setSeed(seed);
		m_idPicker = id_picker;
	}
	
	public NormalActionsPattern(List<String> total_payloads, RandomInteger rand_int, Picker<Integer> id_picker)
	{
		super();
		m_totalPayloads = total_payloads;
		m_indexPicker = rand_int;
		m_idPicker = id_picker;
	}

	@Override
	public void notifyEvent(List<Tuple> list)
	{
		// Nothing to do
	}

	@Override
	public Picker<List<Tuple>> duplicate(boolean with_state)
	{
		return new NormalActionsPattern(m_totalPayloads, m_indexPicker.duplicate(with_state), m_idPicker.duplicate(with_state));
	}

	@Override
	public List<Tuple> pick()
	{
		List<Tuple> list = new ArrayList<Tuple>(1);
		list.add(new Tuple(m_idPicker.pick(), m_totalPayloads.get(m_indexPicker.pick())));
		return list;
	}

	@Override
	public void reset()
	{
		m_indexPicker.reset();
		m_idPicker.reset();
	}
}
