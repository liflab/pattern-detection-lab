package patternlab;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.ltl.Troolean;

public class Sequence extends UniformProcessor
{
	protected int m_waitingIndex;
	
	protected int m_numInputs;
	
	/*@ non_null @*/ protected final int[] m_witnessIndices;
	
	public Sequence(int in_arity)
	{
		super(in_arity, 1);
		m_waitingIndex = 0;
		m_numInputs = 0;
		m_witnessIndices = new int[in_arity];
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		if (m_waitingIndex >= 0 && m_waitingIndex < getInputArity())
		{
			if (inputs[m_waitingIndex] == Troolean.Value.TRUE)
			{
				m_witnessIndices[m_waitingIndex] = m_numInputs;
				m_waitingIndex++;
			}
			else if (inputs[m_waitingIndex] == Troolean.Value.FALSE)
			{
				m_waitingIndex = -1;
			}
		}
		if (m_waitingIndex < 0)
		{
			outputs[0] = Troolean.Value.FALSE;
		}
		else if (m_waitingIndex == getInputArity())
		{
			outputs[0] = Troolean.Value.TRUE;
			if (m_eventTracker != null)
			{
				for (int i = 0; i < m_witnessIndices.length; i++)
				{
					m_eventTracker.associateToInput(getId(), i, m_witnessIndices[i], 0, m_numInputs);
				}
			}
		}
		else
		{
			outputs[0] = Troolean.Value.INCONCLUSIVE;
		}
		m_numInputs++;
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		Sequence seq = new Sequence(getInputArity());
		if (with_state)
		{
			seq.m_numInputs = m_numInputs;
			seq.m_waitingIndex = m_waitingIndex;
			for (int i = 0; i < m_witnessIndices.length; i++)
			{
				seq.m_witnessIndices[i] = m_witnessIndices[i];
			}
		}
		return seq;
	}
}
