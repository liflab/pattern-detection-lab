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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.util.Choice;
import ca.uqac.lif.synthia.util.Constant;

/**
 * Generates string events by picking them with equal probability.
 */
public class RandomAlphabet extends Choice<List<String>>
{
	public RandomAlphabet(Picker<Float> float_source, int size)
	{
		this(float_source, size, 97); // ASCII 97 = "a"
	}
	
	public RandomAlphabet(Picker<Float> float_source, int size, int start_index)
	{
		super(float_source);
		float p = 1f / (float) size;
		for (int i = 0; i < size; i++)
		{
			List<String> list = new ArrayList<String>(1);
			list.add(Character.toString(start_index + i));
			this.add(new Constant<List<String>>(list), p);
		}
	}
	
	public RandomAlphabet(Picker<Float> float_source, String ... letters)
	{
		super(float_source);
		float p = 1f / (float) letters.length;
		for (int i = 0; i < letters.length; i++)
		{
			List<String> list = new ArrayList<String>(1);
			list.add(letters[i]);
			this.add(new Constant<List<String>>(list), p);
		}
	}
}
