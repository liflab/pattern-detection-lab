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
package patternlab.pattern;

import java.util.Arrays;
import java.util.List;

import ca.uqac.lif.synthia.NoMoreElementException;
import ca.uqac.lif.synthia.Picker;

public class BFollowsAPattern implements Picker<List<String>>, Notifiable<String>
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = "b follows a";
	
	/**
	 * The current state of the pattern.
	 */
	protected int m_state = 0;
	
	@Override
	public void notifyEvent(List<String> list)
	{
		for (String e : list)
		{
			notify(e);
		}
	}
	
	protected void notify(String event)
	{
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
			}
		}
	}

	@Override
	public Picker<List<String>> duplicate(boolean with_state)
	{
		BFollowsAPattern bfa = new BFollowsAPattern();
		if (with_state)
		{
			bfa.m_state = m_state;
		}
		return bfa;
	}

	@Override
	public List<String> pick()
	{
		if (m_state == 0)
		{
			m_state = 1;
			return Arrays.asList("a");
		}
		if (m_state == 1)
		{
			m_state = 2;
			return Arrays.asList("b");
		}
		throw new NoMoreElementException();
	}

	@Override
	public void reset()
	{
		m_state = 0;
	}
}
