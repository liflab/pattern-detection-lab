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

import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.synthia.NoMoreElementException;

/**
 * Wraps a picker into a BeepBeep source that can be connected to other
 * processors.
 * @param <T> The type of events produced by the picker (and hence the source)
 */
public class InjectedPatternSource<T> extends SynchronousProcessor
{
	/**
	 * The picker producing the event log.
	 */
	/*@ non_null @*/ protected final InjectedPatternPicker<T> m_picker;
	
	/**
	 * The maximum number of events to produce. The source will produce that
	 * number of events and then stop, unless the underlying picker stops
	 * producing an output before.
	 */
	protected final int m_maxLength;

	/**
	 * Creates a new injected pattern source.
	 * @param picker The picker producing the event log
	 * @param max_length  The maximum number of events to produce. The source
	 * will produce that number of events and then stop, unless the
	 * underlying picker stops producing an output before.
	 */
	public InjectedPatternSource(InjectedPatternPicker<T> picker, int max_length)
	{
		super(0, 1);
		m_picker = picker;
		m_maxLength = max_length;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		if (m_outputCount > m_maxLength)
		{
			return false;
		}
		try
		{
			List<T> picked = m_picker.pick();
			for (T t : picked)
			{
				outputs.add(new Object[] {t});
			}
			m_outputCount += picked.size();
		}
		catch (NoMoreElementException e)
		{
			return false;
		}
		return true;
	}

	@Override
	public InjectedPatternSource<T> duplicate(boolean with_state)
	{
		return new InjectedPatternSource<T>(m_picker.duplicate(with_state), m_maxLength);
	}
}
