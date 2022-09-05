package patternlab.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.tmf.SinkLast;

public class Eventually extends Monitor
{
	protected final Monitor m_pattern;

	protected final List<MonitorInstance> m_instances;

	protected final List<Pushable> m_pushables;

	protected final List<SinkLast> m_sinks;
	
	protected MonitorInstance m_winner;
	
	protected int m_numInputs;

	public Eventually(Monitor pattern)
	{
		super(1, 1);
		m_pattern = pattern;
		m_instances = new ArrayList<MonitorInstance>();
		m_pushables = new ArrayList<Pushable>();
		m_sinks = new ArrayList<SinkLast>();
		m_winner = null;
		m_numInputs = 0;
	}

	@Override
	public Object getState() throws UnsupportedOperationException
	{
		return m_winner != null;
	}

	@Override
	public List<Integer> getSequence()
	{
		if (m_winner == null)
		{
			return null;
		}
		List<Integer> w_seq = m_winner.m_mon.getSequence();
		List<Integer> seq = new ArrayList<Integer>(w_seq.size());
		for (int index : w_seq)
		{
			seq.add(index + m_winner.m_startIndex);
		}
		return seq;
	}

	@Override
	public Eventually duplicate(boolean with_state)
	{
		Eventually e = new Eventually(m_pattern);
		if (with_state)
		{
			// TODO
		}
		return e;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		if (m_winner != null)
		{
			outputs.add(new Object[] {Troolean.Value.TRUE});
			return true;
		}
		Object e = inputs[0];
		{
			Monitor m = m_pattern.duplicate(false);
			m_instances.add(new MonitorInstance(m, m_numInputs));
			m_pushables.add(m.getPushableInput());
			SinkLast sink = new SinkLast();
			Connector.connect(m, sink);
			m_sinks.add(sink);
		}
		m_numInputs++;
		for (int i = 0; i < m_instances.size(); i++)
		{
			m_pushables.get(i).push(e);
			Object[] objs = m_sinks.get(i).getLast();
			if (objs != null && objs.length == 1)
			{
				Troolean.Value v = (Troolean.Value) objs[0];
				if (v == Troolean.Value.FALSE)
				{
					m_sinks.remove(i);
					m_pushables.remove(i);
					m_instances.remove(i);
					i--;
					continue;
				}
				if (v == Troolean.Value.TRUE)
				{
					m_winner = m_instances.get(i);
					outputs.add(new Object[] {Troolean.Value.TRUE});
					return true;
				}
			}
		}
		outputs.add(new Object[] {Troolean.Value.INCONCLUSIVE});
		return true;
	}
	
	protected class MonitorInstance
	{
		public Monitor m_mon;
		
		public int m_startIndex;
		
		public MonitorInstance(Monitor mon, int start_index)
		{
			super();
			m_mon = mon;
			m_startIndex = start_index;
		}
	}

}
