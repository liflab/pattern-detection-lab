package patternlab.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import ca.uqac.lif.cep.ltl.Troolean;

public class Sequence extends MultiMonitor
{	
	protected int m_index;
	
	public Sequence(Monitor ... patterns)
	{
		super(patterns);
		m_index = 0;
	}

	@Override
	public List<Integer> getSequence()
	{
		if (m_index != m_monitors.length)
		{
			return null;
		}
		Set<Integer> all_indices = new HashSet<Integer>();
		for (Monitor m : m_monitors)
		{
			List<Integer> seq = m.getSequence();
			if (seq != null)
			{
				all_indices.addAll(seq);
			}
		}
		List<Integer> indices = new ArrayList<Integer>();
		indices.addAll(all_indices);
		Collections.sort(indices);
		return indices;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		if (m_index == m_monitors.length)
		{
			outputs.add(new Object[] {Troolean.Value.TRUE});
			return true;
		}
		if (m_index == -1)
		{
			outputs.add(new Object[] {Troolean.Value.FALSE});
			return true;
		}
		m_pushables[m_index].push(inputs[0]);
		Object[] objs = m_sinks[m_index].getLast();
		if (objs[0] == Troolean.Value.TRUE)
		{
			m_index++;
			if (m_index == m_monitors.length)
			{
				outputs.add(new Object[] {Troolean.Value.TRUE});
				return true;
			}
		}
		else if (objs[0] == Troolean.Value.FALSE)
		{
			m_index = -1;
			outputs.add(new Object[] {Troolean.Value.FALSE});
			return true;
		}
		outputs.add(new Object[] {Troolean.Value.INCONCLUSIVE});
		return true;
	}

	@Override
	public Monitor duplicate(boolean with_state)
	{
		Monitor[] dup_mons = new Monitor[m_monitors.length];
		for (int i = 0; i < m_monitors.length; i++)
		{
			dup_mons[i] = m_monitors[i].duplicate(with_state);
		}
		Sequence ls = new Sequence(dup_mons);
		copyInto(ls, with_state);
		return ls;
	}
	
	protected void copyInto(Sequence ls, boolean with_state)
	{
		if (with_state)
		{
			ls.m_index = m_index;
		}
	}
}
