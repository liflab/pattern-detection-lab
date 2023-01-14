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
package patternlab.pattern.toomanyactions;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.random.RandomInteger;
import patternlab.pattern.Notifiable;
import patternlab.pattern.Tuple;

public class NormalActionsPattern implements Picker<List<Tuple>>, Notifiable<Tuple>
{
	protected final List<String> m_totalPayloads;
	
	protected final RandomInteger m_indexPicker;
	
	protected final Picker<Integer> m_idPicker;
	
	public NormalActionsPattern(List<String> total_payloads, Picker<Integer> id_picker, int seed)
	{
		super();
		m_totalPayloads = total_payloads;
		m_indexPicker = new RandomInteger(0, m_totalPayloads.size()).setSeed(seed);
		m_idPicker = id_picker;
	}
	
	public NormalActionsPattern(List<String> total_payloads, RandomInteger rand_int, Picker<Integer> id_picker)
	{
		super();
		m_totalPayloads = total_payloads;
		m_indexPicker = rand_int;
		m_idPicker = id_picker;
	}

	@Override
	public void notifyEvent(List<Tuple> list)
	{
		// Nothing to do
	}

	@Override
	public Picker<List<Tuple>> duplicate(boolean with_state)
	{
		return new NormalActionsPattern(m_totalPayloads, m_indexPicker.duplicate(with_state), m_idPicker.duplicate(with_state));
	}

	@Override
	public List<Tuple> pick()
	{
		List<Tuple> list = new ArrayList<Tuple>(1);
		list.add(new Tuple(m_idPicker.pick(), m_totalPayloads.get(m_indexPicker.pick())));
		return list;
	}

	@Override
	public void reset()
	{
		m_indexPicker.reset();
		m_idPicker.reset();
	}
}
