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

public class AttackPattern implements Picker<List<Tuple>>, Notifiable<Tuple>
{
	protected final Picker<String> m_payloadPicker;
	
	protected final Picker<Integer> m_idPicker;
	
	protected final int m_id;
	
	protected boolean m_start;
	
	public AttackPattern(Picker<Integer> id_picker, Picker<String> payload_picker)
	{
		this(true, id_picker.pick(), id_picker, payload_picker);
	}
	
	protected AttackPattern(boolean start, int id, Picker<Integer> id_picker, Picker<String> payload_picker)
	{
		super();
		m_start = start;
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
	public AttackPattern duplicate(boolean with_state)
	{
		/*if (with_state)
		{
			return new AttackPattern(m_start, m_id, m_idPicker.duplicate(with_state), m_payloadPicker.duplicate(with_state));
		}*/
		return new AttackPattern(true, m_idPicker.pick(), m_idPicker.duplicate(with_state), m_payloadPicker.duplicate(with_state));
	}

	@Override
	public List<Tuple> pick()
	{
		List<Tuple> out = new ArrayList<Tuple>();
		if (m_start)
		{
			m_start = false;
			out.add(new Tuple(m_id, "A"));
		}
		else
		{
			String payload = "";
			do
			{
				// Since this is the attack pattern, we never issue a B;
				// if we get one we re-pick
				payload = m_payloadPicker.pick();
			} while (payload.compareTo("B") == 0);
			out.add(new Tuple(m_id, payload));	
		}
		return out;
	}

	@Override
	public void reset()
	{
		m_start = true;
		m_payloadPicker.reset();
	}
	
	@Override
	public String toString()
	{
		return "Attack pattern";
	}
}
