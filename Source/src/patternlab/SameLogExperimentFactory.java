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

import static patternlab.PatternDetectionExperiment.P_ALPHA;
import static patternlab.PatternDetectionExperiment.P_PATTERN;

import ca.uqac.lif.labpal.region.Point;
import patternlab.pattern.bfollowsa.LinearPattern;
import patternlab.pattern.combined.CombinedPattern;
import patternlab.pattern.incomplete.IncompletePattern;
import patternlab.pattern.toomanyactions.TooManyActionsPattern;

public class SameLogExperimentFactory extends PatternDetectionExperimentFactory
{
	public SameLogExperimentFactory(PatternDetectionExperimentFactory factory)
	{
		super(factory);
	}
	
	@Override
	protected boolean setPattern(Point p, PatternDetectionExperiment e, int log_length)
	{
		String pattern_name = p.getString(P_PATTERN);
		float alpha = (Float) p.get(P_ALPHA);
		switch (pattern_name)
		{
		case TooManyActionsPattern.NAME:
		{
			return setTooManyActionsPattern(p, e, log_length, alpha);
		}
		case LinearPattern.NAME:
		{
			return setLinearPattern(p, e, log_length, alpha, 10);
		}
		case CombinedPattern.NAME:
		{
			return setCombinedPattern(p, e, log_length, alpha);
		}
		case IncompletePattern.NAME:
		{
			return setIncompletePattern(p, e, log_length, alpha);
		}
		}
		return false;
	}
	
	@Override
	protected boolean setLinearSequenceMonitor(Point p, PatternDetectionExperiment e, int pattern_length)
	{
		boolean b = super.setLinearSequenceMonitor(p, e, pattern_length);
		e.writeInput(LinearPattern.P_PATTERN_LENGTH, pattern_length);
		return b;
	}
	
}
