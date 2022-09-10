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

public class AllEventually extends UniformProcessor implements Stateful
{
	/*@ non_null @*/ protected final boolean[] m_seenTrue;
	
	/*@ non_null @*/ protected final int[] m_indexTrue;
	
	protected int m_numInputs;
	
	public AllEventually(int in_arity)
	{
		super(in_arity, 1);
		m_numInputs = 0;
		m_seenTrue = new boolean[in_arity];
		m_indexTrue = new int[in_arity];
		for (int i = 0; i < in_arity; i++)
		{
			m_seenTrue[i] = false;
			m_indexTrue[i] = -1;
		}
	}
	
	@Override
	public Object getState()
	{
		MathList<Boolean> list = new MathList<Boolean>();
		for (boolean b : m_seenTrue)
		{
			list.add(b);
		}
		return list;
	}
	
	@Override
	public boolean compute(Object[] inputs, Object[] outputs)
	{
		boolean all_true = true;
		for (int i = 0; i < inputs.length; i++)
		{
			if (inputs[i] instanceof Troolean.Value && (Troolean.Value) inputs[i] == Troolean.Value.TRUE)
			{
				m_seenTrue[i] = true;
				m_indexTrue[i] = m_numInputs;
			}
			if (!m_seenTrue[i])
			{
				all_true = false;
			}
		}
		if (all_true)
		{
			outputs[0] = Troolean.Value.TRUE;
			if (m_eventTracker != null)
			{
				for (int i = 0; i < m_indexTrue.length; i++)
				{
					m_eventTracker.associateToInput(getId(), i, m_indexTrue[i], 0, m_numInputs);
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
	public void reset()
	{
		super.reset();
		m_numInputs = 0;
		for (int i = 0; i < m_seenTrue.length; i++)
		{
			m_seenTrue[i] = false;
			m_indexTrue[i] = -1;
		}
	}
	
	@Override
	public AllEventually duplicate(boolean with_state)
	{
		AllEventually ae = new AllEventually(getInputArity());
		if (with_state)
		{
			ae.m_numInputs = m_numInputs;
			for (int i = 0; i < m_seenTrue.length; i++)
			{
				ae.m_seenTrue[i] = m_seenTrue[i];
				ae.m_indexTrue[i] = m_indexTrue[i];
			}
		}
		return ae;
	}
}
