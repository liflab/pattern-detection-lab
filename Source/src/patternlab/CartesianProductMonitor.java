package patternlab;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.Stateful;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.tmf.SinkLast;
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
public class CartesianProductMonitor extends UniformProcessor implements Stateful
{
	protected final Processor[] m_monitors;
	
	protected final Pushable[] m_pushables;
	
	protected final SinkLast[] m_sinks;
	
	protected final Troolean.Value[] m_verdicts;
	
	public CartesianProductMonitor(Processor ... monitors)
	{
		super(1, 1);
		m_monitors = monitors;
		m_verdicts = new Troolean.Value[monitors.length];
		m_pushables = new Pushable[monitors.length];
		m_sinks = new SinkLast[monitors.length];
		for (int i = 0; i < m_verdicts.length; i++)
		{
			m_pushables[i] = monitors[i].getPushableInput();
			m_sinks[i] = new SinkLast();
			Connector.connect(m_monitors[i], m_sinks[i]);
			m_verdicts[i] = Troolean.Value.INCONCLUSIVE;
		}
	}
	
	@Override
	public CartesianProductMonitor duplicate(boolean with_state)
	{
		Processor[] dup_mons = new Processor[m_monitors.length];
		for (int i = 0; i < dup_mons.length; i++)
		{
			dup_mons[i] = m_monitors[i].duplicate(with_state);
		}
		CartesianProductMonitor cpm = new CartesianProductMonitor(dup_mons);
		if (with_state)
		{
			for (int i = 0; i < m_verdicts.length; i++)
			{
				cpm.m_verdicts[i] = m_verdicts[i];
			}
		}
		return cpm;
	}
	
	@Override
	public Object getState()
	{
		MathList<Object> states = new MathList<Object>();
		for (Processor p : m_monitors)
		{
			if (p instanceof Stateful)
			{
				states.add(((Stateful) p).getState());
			}
		}
		return states;
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
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
		outputs[0] = Troolean.and(m_verdicts);
		return true;
	}
}
