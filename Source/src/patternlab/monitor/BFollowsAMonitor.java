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
package patternlab.monitor;

import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Stateful;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.ltl.Troolean;

/**
 * A simple monitor used for testing the lab. It checks that an event "a" is
 * eventually followed by a "b".
 */
public class BFollowsAMonitor extends SynchronousProcessor implements Stateful
{
	/**
	 * The internal state of the monitor.
	 */
	protected int m_state = 0;
	
	/**
	 * Creates a new instance of the monitor.
	 */
	public BFollowsAMonitor()
	{
		super(1, 1);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_state = 0;
	}
	
	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		String event = (String) inputs[0];
		if (event.compareTo("a") == 0)
		{
			if (m_state == 0)
			{
				m_state = 1;
			}
		}
		if (event.compareTo("b") == 0)
		{
			if (m_state == 1)
			{
				m_state = 2;
				outputs.add(new Object[] {Troolean.Value.TRUE});
				return true;
			}
		}
		outputs.add(new Object[] {Troolean.Value.INCONCLUSIVE});
		return true;
	}
	
	@Override
	public Processor duplicate(boolean with_state)
	{
		BFollowsAMonitor bfa = new BFollowsAMonitor();
		if (with_state)
		{
			bfa.m_state = m_state;
		}
		return bfa;
	}

	@Override
	public Object getState() throws UnsupportedOperationException
	{
		return m_state;
	}
}
