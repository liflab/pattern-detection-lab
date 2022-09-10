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

import ca.uqac.lif.cep.Stateful;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.util.Lists.MathList;

public class SomeEventually extends UniformProcessor implements Stateful
{
	protected int m_lastStream;
	
	protected int m_lastIndex;
	
	public SomeEventually(int in_arity)
	{
		super(in_arity, 1);
		m_lastStream = -1;
		m_lastIndex = -1;
		m_inputCount = 0;
	}
	
	@Override
	public Object getState()
	{
		MathList<Integer> list = new MathList<Integer>();
		list.add(m_lastStream);
		list.add(m_lastIndex);
		return list;
	}
	
	@Override
	public boolean compute(Object[] inputs, Object[] outputs)
	{
		for (int i = 0; i < inputs.length; i++)
		{
			if (inputs[i] instanceof Troolean.Value && (Troolean.Value) inputs[i] == Troolean.Value.TRUE)
			{
				m_lastIndex = m_inputCount;
				m_lastStream = i;
			}
		}
		if (m_lastIndex >= 0)
		{
			outputs[0] = Troolean.Value.TRUE;
			if (m_eventTracker != null)
			{
				m_eventTracker.associateToInput(getId(), m_lastStream, m_lastIndex, 0, m_inputCount);
			}
		}
		else
		{
			outputs[0] = Troolean.Value.INCONCLUSIVE;
		}
		m_inputCount++;
		return true;
	}
	
	@Override
	public SomeEventually duplicate(boolean with_state)
	{
		SomeEventually se = new SomeEventually(getInputArity());
		if (with_state)
		{
			se.m_lastIndex = m_lastIndex;
			se.m_lastStream = m_lastStream;
			se.m_inputCount = m_inputCount;
		}
		return se;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_lastStream = -1;
		m_lastIndex = -1;
		m_inputCount = 0;
	}
}
