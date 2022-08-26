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

import ca.uqac.lif.synthia.NoMoreElementException;
import ca.uqac.lif.synthia.Picker;

/**
 * A picker that interleaves events from a "normal" event source to one or
 * more "pattern" event sources.
 * @param <T> The type of events produced by the sources
 */
public class InjectedPatternPicker<T> implements Picker<List<T>>
{
	/**
	 * The maximum number of tries before the picker gives up producing an event
	 * (if too many {@link NoMoreElementException}s are thrown by the underlying
	 * pickers.
	 */
	protected static int s_maxTries = 100;

	/**
	 * The instances of pickers that produce the actual events to be produced.
	 */
	protected PatternChoice<T> m_instances;

	/**
	 * Creates a new pattern picker.
	 * @param normal A picker providing "normal" events
	 * @param pattern A picker providing the "pattern" events to interleave with
	 * the normal events
	 * @param num_overlaps The number <i>n</i> of distinct pattern instances that
	 * are allowed to overlap at any moment. A value of 1 indicates a single
	 * instance of the pattern may be ongoing at any time. A value of 0 will
	 * cause no pattern instance to be interleaved at all. 
	 * @param p_normal The probability <i>p</i> that each event be picked from
	 * the "normal" picker. Must be a value between 0 and 1. The remaining
	 * "pattern" pickers are each chosen with a probability of
	 * (1 &minus; <i>p</i>) / <i>n</i>.
	 * @param float_source A float picker acting as the source of choices
	 */
	public InjectedPatternPicker(Picker<List<T>> normal, Picker<List<T>> pattern, int num_overlaps, float p_normal, Picker<Float> float_source)
	{
		super();
		m_instances = new PatternChoice<T>(float_source);
		m_instances.add(normal, p_normal);
		float p_pattern = (1 - p_normal) / (float) num_overlaps;
		for (int i = 0; i < num_overlaps; i++)
		{
			m_instances.add(pattern.duplicate(false), p_pattern);
		}
	}

	/**
	 * Creates a new pattern picker by directly providing a {@link Choice}
	 * picker. This constructor is only used internally to create duplicates of
	 * an instance.
	 * @param choice The choice picker
	 */
	protected InjectedPatternPicker(PatternChoice<T> choice)
	{
		super();
		m_instances = choice;
	}

	@Override
	public List<T> pick()
	{
		for (int i = 0; i < s_maxTries; i++)
		{
			Picker<List<T>> current_instance = m_instances.pick();
			try
			{
				List<T> picked = current_instance.pick();
				m_instances.notifyEvent(picked);
				return picked;
			}
			catch (NoMoreElementException e)
			{
				current_instance.reset();
			}
		}
		throw new NoMoreElementException();
	}

	@Override
	public InjectedPatternPicker<T> duplicate(boolean with_state)
	{
		InjectedPatternPicker<T> ipp = new InjectedPatternPicker<T>(m_instances.duplicate(with_state));
		return ipp;
	}

	@Override
	public void reset()
	{
		m_instances.reset();
	}
}
