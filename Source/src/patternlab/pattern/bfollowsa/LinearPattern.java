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
package patternlab.pattern.bfollowsa;

import java.util.List;

import patternlab.pattern.SequencePattern;

public class LinearPattern<T> extends SequencePattern<T>
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = "Linear sequence";
	
	/**
	 * The name of the parameter "Number of elements".
	 */
	public static final String P_PATTERN_LENGTH = "Number of elements";

	protected final List<T> m_elements;

	public LinearPattern(List<T> elements)
	{
		super(elements);
		m_elements = elements;
	}

	@Override
	public LinearPattern<T> duplicate(boolean with_state)
	{
		LinearPattern<T> bfa = new LinearPattern<T>(m_elements);
		copyInto(bfa, with_state);
		return bfa;
	}

	
}
