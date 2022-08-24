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
package patternlab;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ltl.FindPattern;

public class InstrumentedFindPattern extends FindPattern
{
	public InstrumentedFindPattern(Processor pattern)
	{
		super(pattern);
	}
	
	/**
	 * Gets the number of monitor instances currently active inside the
	 * processor.
	 * @return The number of monitor instances
	 */
	/*@ pure @*/ public int getInstances()
	{
		return m_instances.size();
	}
	
	public void setRemoveNonMatches(boolean b)
	{
		m_removeNonMatches = b;
	}
	
	public void setRemoveImmobileOnStart(boolean b)
	{
		m_removeImmobileOnStart = b;
	}
	
	public void setRemoveNonProgressing(boolean b)
	{
		m_removeNonProgressing = b;
	}

}
