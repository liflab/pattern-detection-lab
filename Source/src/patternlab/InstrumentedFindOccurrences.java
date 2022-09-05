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
import patternlab.monitor.FindOccurrences;

public class InstrumentedFindOccurrences extends FindOccurrences implements InstanceReportable
{
	/**
	 * A string indicating that the processor only keeps monitor instances
	 * in distinct states.
	 */
	public static final String DISTINCT_STATES = "Distinct states";
	
	/**
	 * A string indicating that the processor only keeps progressing
	 * subsequences.
	 */
	public static final String PROGRESSING = "Progressing";
	
	/**
	 * A string indicating that the processor only keeps monitor instances that
	 * move out of their initial state on the first event consumed.
	 */
	public static final String FIRST_STEP = "First step";
	
	/**
	 * A string indicating that all monitor instances are kept until they produce
	 * a definite verdict.
	 */
	public static final String DIRECT = "Direct";
	
	public InstrumentedFindOccurrences(Processor pattern)
	{
		super(pattern);
	}
	
	@Override
	/*@ pure @*/ public int getInstances()
	{
		return m_instances.size();
	}
	
	@Override
	public void setRemoveNonMatches(boolean b)
	{
		m_removeNonMatches = b;
	}
	
	@Override
	public void setRemoveImmobileOnStart(boolean b)
	{
		m_removeImmobileOnStart = b;
	}
	
	@Override
	public void setRemoveNonProgressing(boolean b)
	{
		m_removeNonProgressing = b;
	}
	
	@Override
	public void setRemoveSameState(boolean b)
	{
		m_removeSameState = b;
	}
	
	@Override
	public InstrumentedFindOccurrences duplicate(boolean with_state)
	{
		InstrumentedFindOccurrences fp = new InstrumentedFindOccurrences(m_pattern.duplicate(with_state));
		if (with_state)
		{
			fp.m_inputCount = m_inputCount;
			fp.m_outputCount = m_outputCount;
			fp.m_removeImmobileOnStart = m_removeImmobileOnStart;
			fp.m_removeNonMatches = m_removeNonMatches;
			fp.m_removeNonProgressing = m_removeNonProgressing;
			fp.m_removeSameState = m_removeSameState;
			for (PatternInstance pi : m_instances)
			{
				fp.m_instances.add(pi.duplicate(with_state));
			}
		}
		return fp;
	}

}
