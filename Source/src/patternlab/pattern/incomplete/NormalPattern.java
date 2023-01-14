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
package patternlab.pattern.incomplete;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.synthia.Picker;
import patternlab.pattern.Notifiable;
import patternlab.pattern.Tuple;

public class NormalPattern implements Picker<List<Tuple>>, Notifiable<Tuple>
{
	protected final Picker<String> m_payloadPicker;
	
	protected final Picker<Integer> m_idPicker;
	
	protected final int m_id;
	
	public NormalPattern(Picker<Integer> id_picker, Picker<String> payload_picker)
	{
		this(id_picker.pick(), id_picker, payload_picker);
	}
	
	protected NormalPattern(int id, Picker<Integer> id_picker, Picker<String> payload_picker)
	{
		super();
		m_idPicker = id_picker;
		m_payloadPicker = payload_picker;
		m_id = id;
	}
	
	@Override
	public void notifyEvent(List<Tuple> list)
	{
		// Nothing to do
	}

	@Override
	public NormalPattern duplicate(boolean with_state)
	{
		/*if (with_state)
		{
			return new NormalPattern(m_id, m_idPicker.duplicate(with_state), m_payloadPicker.duplicate(with_state));
		}*/
		return new NormalPattern(m_idPicker.duplicate(with_state), m_payloadPicker.duplicate(with_state));
	}

	@Override
	public List<Tuple> pick()
	{
		List<Tuple> out = new ArrayList<Tuple>();
		String payload = m_payloadPicker.pick();
		out.add(new Tuple(m_id, payload));
		if (payload.compareTo("A") == 0)
		{
			// Since this is the normal pattern, if we pick an A, we add a B right after
			out.add(new Tuple(m_id, "B"));
		}
		return out;
	}

	@Override
	public void reset()
	{
		m_payloadPicker.reset();
	}
	
	@Override
	public String toString()
	{
		return "Normal pattern";
	}
}
