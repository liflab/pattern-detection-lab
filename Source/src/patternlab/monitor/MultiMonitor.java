package patternlab.monitor;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.cep.util.Lists.MathList;

public abstract class MultiMonitor extends Monitor
{
	protected final Monitor[] m_monitors;
	
	protected final Pushable[] m_pushables;
	
	protected final SinkLast[] m_sinks;
	
	public MultiMonitor(Monitor ... patterns)
	{
		super(1, 1);
		m_monitors = patterns;
		m_pushables = new Pushable[m_monitors.length];
		m_sinks = new SinkLast[m_monitors.length];
		for (int i = 0; i < m_monitors.length; i++)
		{
			m_pushables[i] = m_monitors[i].getPushableInput();
			SinkLast sink = new SinkLast();
			Connector.connect(m_monitors[i], sink);
			m_sinks[i] = sink;
		}
	}
	
	@Override
	public Object getState() throws UnsupportedOperationException
	{
		MathList<Object> states = new MathList<Object>();
		for (Monitor m : m_monitors)
		{
			states.add(m.getState());
		}
		return states;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		for (int i = 0; i < m_monitors.length; i++)
		{
			m_monitors[i].reset();
			m_sinks[i].reset();
		}
	}
}
