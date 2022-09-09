/*
  Experimentation of pattern detection by monitors
  Copyright (C) 2022 Sylvain Hallé

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package patternlab;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.ltl.Troolean;

public class Sequence extends UniformProcessor
{
	protected int m_waitingIndex;
	
	protected int m_numInputs;
	
	/*@ non_null @*/ protected final int[] m_witnessIndices;
	
	protected final boolean m_soft;
	
	public Sequence(int in_arity)
	{
		this(in_arity, false);
	}
	
	public Sequence(int in_arity, boolean soft)
	{
		super(in_arity, 1);
		m_waitingIndex = 0;
		m_numInputs = 0;
		m_witnessIndices = new int[in_arity];
		m_soft = soft;
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
			else if (!m_soft && inputs[m_waitingIndex] == Troolean.Value.FALSE)
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
		Sequence seq = new Sequence(getInputArity(), m_soft);
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
