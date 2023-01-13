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
package patternlab.pattern.combined;

import ca.uqac.lif.synthia.Picker;
import patternlab.pattern.CartesianProductPattern;
import patternlab.pattern.RandomAlphabet;
import patternlab.pattern.SequencePattern;

public class CombinedPattern extends CartesianProductPattern<String>
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = "Combined patterns";
	
	/**
	 * The name of the parameter "Number of elements".
	 */
	public static final String P_NUM_PATTERNS = "Number of patterns";

	protected final int m_numPatterns;
	
	public CombinedPattern(Picker<Float> float_source, int num_patterns)
	{
		super(float_source, getPatterns(num_patterns));
		m_numPatterns = num_patterns;
	}

	protected static Picker<?>[] getPatterns(int num_patterns)
	{
		Picker<?>[] patterns = new Picker<?>[num_patterns];
		for (int i = 0; i < num_patterns; i++)
		{
			patterns[i] = new SequencePattern<String>(RandomAlphabet.getUppercaseSequence(i * 5, 5));
		}
		return patterns;
	}
}
