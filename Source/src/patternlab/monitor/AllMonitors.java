package patternlab.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.util.Lists.MathList;

/**
 * A monitor that produces a verdict based on the verdict of multiple other
 * monitors <i>M</i><sub>1</sub>, &hellip;, <i>M</i><sub><i>n</i></sub>. Given
 * a stream of events, the Cartesian monitor returns:
 * <ul>
 * <li>{@link Troolean.Value.TRUE} if each <i>M</i><sub><i>i</i></sub> produces
 * the true verdict</li>
 * <li>{@link Troolean.Value.FALSE} if at least one <i>M</i><sub><i>n</i></sub>
 * produces a false verdict</li>
 * <li>{@link Troolean.Value.INCONCLUSIVE} otherwise</li>
 * </ul>
 * 
 * @author Sylvain Hallé
 *
 */
public class AllMonitors extends MultiMonitor
{
	protected final Troolean.Value[] m_verdicts;

	public AllMonitors(Monitor ... monitors)
	{
		super(monitors);
		m_verdicts = new Troolean.Value[monitors.length];
		for (int i = 0; i < m_verdicts.length; i++)
		{
			m_verdicts[i] = Troolean.Value.INCONCLUSIVE;
		}
	}

	@Override
	public List<Integer> getSequence()
	{
		if (Troolean.and(m_verdicts) != Troolean.Value.TRUE)
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
	public AllMonitors duplicate(boolean with_state)
	{
		Monitor[] dup_mons = new Monitor[m_monitors.length];
		for (int i = 0; i < dup_mons.length; i++)
		{
			dup_mons[i] = m_monitors[i].duplicate(with_state);
		}
		AllMonitors cpm = new AllMonitors(dup_mons);
		return cpm;
	}

	protected void copyInto(AllMonitors cpm, boolean with_state)
	{
		if (with_state)
		{
			for (int i = 0; i < m_verdicts.length; i++)
			{
				cpm.m_verdicts[i] = m_verdicts[i];
			}
		}
	}

	@Override
	public Object getState()
	{
		MathList<Object> states = new MathList<Object>();
		for (Monitor p : m_monitors)
		{
			states.add(p.getState());
		}
		return states;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		Object event = inputs[0];
		for (int i = 0; i < m_pushables.length; i++)
		{
			if (m_verdicts[i] == Troolean.Value.INCONCLUSIVE)
			{
				m_pushables[i].push(event);
			}
			Object[] outs = m_sinks[i].getLast();
			if (outs != null && outs.length == 1 && outs[0] instanceof Troolean.Value)
			{
				m_verdicts[i] = (Troolean.Value) outs[0];
			}
		}
		outputs.add(new Object[] {Troolean.and(m_verdicts)});
		return true;
	}
}
