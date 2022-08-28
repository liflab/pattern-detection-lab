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

import java.lang.reflect.Constructor;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.experiment.ExperimentFactory;
import ca.uqac.lif.labpal.region.Point;
import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.random.RandomFloat;
import patternlab.pattern.BFollowsAMonitor;
import patternlab.pattern.BFollowsAPattern;
import patternlab.pattern.InjectedPatternPicker;
import patternlab.pattern.InjectedPatternSource;
import patternlab.pattern.RandomAlphabet;

import static patternlab.PatternDetectionExperiment.P_ALGORITHM;
import static patternlab.PatternDetectionExperiment.P_ALPHA;
import static patternlab.PatternDetectionExperiment.P_PATTERN;

public class PatternDetectionExperimentFactory extends ExperimentFactory<PatternDetectionExperiment>
{
	public PatternDetectionExperimentFactory(Laboratory lab)
	{
		super(lab);
	}
	
	@Override
	protected PatternDetectionExperiment createExperiment(Point p)
	{
		InjectedPatternSource<?> pattern = getPattern(p);
		Processor monitor = getMonitor(p);
		if (pattern == null || monitor == null)
		{
			return null;
		}
		InstrumentedFindPattern ifp = new InstrumentedFindPattern(monitor);
		switch (p.getString(P_ALGORITHM))
		{
		case InstrumentedFindPattern.PROGRESSING:
			ifp.setRemoveSameState(false);
			break;
		case InstrumentedFindPattern.FIRST_STEP:
			ifp.setRemoveSameState(false);
			ifp.setRemoveNonProgressing(false);
			break;
		case InstrumentedFindPattern.DIRECT:
			ifp.setRemoveSameState(false);
			ifp.setRemoveNonProgressing(false);
			ifp.setRemoveImmobileOnStart(false);
			break;
		}
		PatternDetectionExperiment pde = new PatternDetectionExperiment(pattern, ifp);
		pde.writeInput(P_PATTERN, p.getString(P_PATTERN));
		pde.writeInput(P_ALGORITHM, p.getString(P_ALGORITHM));
		pde.writeInput(P_ALPHA, p.get(P_ALPHA));
		return pde;
	}

	@Override
	protected Constructor<? extends PatternDetectionExperiment> getPointConstructor(Point p)
	{
		return null;
	}

	@Override
	protected Constructor<? extends PatternDetectionExperiment> getEmptyConstructor(Point p)
	{
		return null;
	}

	@Override
	protected Class<? extends PatternDetectionExperiment> getClass(Point p)
	{
		return PatternDetectionExperiment.class;
	}
	
	/**
	 * Gets the picker instance corresponding to a given pattern name.
	 * @param p The point containing the information about the experiment to
	 * create
	 * @return The picker
	 */
	protected static InjectedPatternSource<?> getPattern(Point p)
	{
		String pattern_name = p.getString(P_PATTERN);
		float alpha = (Float) p.get(P_ALPHA);
		switch (pattern_name)
		{
			case BFollowsAPattern.NAME:
				RandomFloat rf = new RandomFloat().setSeed(0);
				InjectedPatternPicker<String> ipp = new InjectedPatternPicker<String>(new RandomAlphabet(rf, "a", "c", "d"), new BFollowsAPattern(), 1, alpha, rf);
				InjectedPatternSource<String> ips = new InjectedPatternSource<String>(ipp, 100000);
				return ips;
		}
		return null;
	}
	
	/**
	 * Gets the monitor instance corresponding to a given pattern name.
	 * @param p The point containing the information about the experiment to
	 * create
	 * @return The picker
	 */
	protected static Processor getMonitor(Point p)
	{
		String pattern_name = p.getString(P_PATTERN);
		switch (pattern_name)
		{
			case BFollowsAMonitor.NAME:
				return new BFollowsAMonitor();
		}
		return null;
	}
}
