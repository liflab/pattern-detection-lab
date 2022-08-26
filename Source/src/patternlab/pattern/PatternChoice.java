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

import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.util.Choice;

/**
 * A descendant of {@link Choice} that allows its pickers to be notified.
 *
 * @param <T> The type of events produced by the underlying pickers
 */
public class PatternChoice<T> extends Choice<Picker<List<T>>> implements Notifiable<T>
{

	/**
	 * Creates a new instance of the picker.
	 * @param picker A float picker acting as the source of choices
	 */
	public PatternChoice(Picker<Float> picker)
	{
		super(picker);
	}
	
	@SuppressWarnings("unchecked")
	public void notifyEvent(List<T> list)
	{
		for (ProbabilityChoice<?> choice : m_choices)
		{
			Picker<?> picker = choice.getPicker();
			if (picker instanceof Notifiable)
			{
				((Notifiable<T>) picker).notifyEvent(list);
			}
		}
	}
	
	@Override
	public PatternChoice<T> duplicate(boolean with_state)
	{
		PatternChoice<T> ppc = new PatternChoice<T>(m_floatPicker);
		for (ProbabilityChoice<Picker<List<T>>> pc : m_choices)
		{
			ppc.add(pc.duplicate(with_state));
		}
		return ppc;
	}
}
