package patternlab.monitor;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.functions.Function;

public class Condition extends Monitor
{
	protected final Function m_function;
	
	protected Troolean.Value m_verdict;
	
	protected int m_numInputs;
	
	public Condition(Function f)
	{
		super(f.getInputArity(), f.getOutputArity());
		m_function = f;
		m_verdict = null;
		m_numInputs = 0;
	}

	@Override
	public Object getState() throws UnsupportedOperationException
	{
		return m_numInputs == 0;
	}

	@Override
	public List<Integer> getSequence()
	{
		if (m_verdict != Troolean.Value.TRUE)
		{
			return null;
		}
		return Arrays.asList(0);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		if (m_numInputs == 0)
		{
			Object[] f_out = new Object[m_function.getOutputArity()];
			m_function.evaluate(inputs, f_out);
			m_verdict = Troolean.trooleanValue(f_out[0]);
			m_numInputs = 1;
		}
		outputs.add(new Object[] {m_verdict});
		return true;
	}

	@Override
	public Condition duplicate(boolean with_state)
	{
		Condition c = new Condition(m_function.duplicate(with_state));
		copyInto(c, with_state);
		return c;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_numInputs = 0;
	}
	
	protected void copyInto(Condition c, boolean with_state)
	{
		if (with_state)
		{
			c.m_verdict = m_verdict;
			c.m_numInputs = m_numInputs;
		}
	}
}
