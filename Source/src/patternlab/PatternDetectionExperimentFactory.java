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
import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.experiment.ExperimentFactory;
import ca.uqac.lif.labpal.region.Point;
import ca.uqac.lif.synthia.random.RandomBoolean;
import ca.uqac.lif.synthia.random.RandomFloat;
import ca.uqac.lif.synthia.random.RandomInteger;
import patternlab.pattern.BFollowsAMonitor;
import patternlab.pattern.BFollowsAPattern;
import patternlab.pattern.InjectedPatternPicker;
import patternlab.pattern.InjectedPatternSource;
import patternlab.pattern.NormalActionsPattern;
import patternlab.pattern.RandomAlphabet;
import patternlab.pattern.TooManyActionsMonitor;
import patternlab.pattern.TooManyActionsPattern;
import patternlab.pattern.Tuple;
import patternlab.pattern.TooManyActionsMonitorGroup;

import static patternlab.PatternDetectionExperiment.P_ALGORITHM;
import static patternlab.PatternDetectionExperiment.P_ALPHA;
import static patternlab.PatternDetectionExperiment.P_PATTERN;

public class PatternDetectionExperimentFactory extends ExperimentFactory<PatternDetectionExperiment>
{
	protected int m_logLength;
	
	protected int m_seed;
	
	protected static int s_defaultThreshold = 14;
	
	protected static int s_defaultPayloads = 15;
	
	public PatternDetectionExperimentFactory(Laboratory lab, int log_length)
	{
		super(lab);
		m_logLength = log_length;
		m_seed = m_lab.getRandomSeed();
	}
	
	@Override
	protected PatternDetectionExperiment createExperiment(Point p)
	{
		Processor pattern = getPattern(p);
		Processor ifp = getMonitor(p);
		if (pattern == null || ifp == null)
		{
			return null;
		}
		switch (p.getString(P_ALGORITHM))
		{
		case InstrumentedFindPattern.PROGRESSING:
			((InstanceReportable) ifp).setRemoveSameState(false);
			break;
		case InstrumentedFindPattern.FIRST_STEP:
			((InstanceReportable) ifp).setRemoveSameState(false);
			((InstanceReportable) ifp).setRemoveNonProgressing(false);
			break;
		case InstrumentedFindPattern.DIRECT:
			((InstanceReportable) ifp).setRemoveSameState(false);
			((InstanceReportable) ifp).setRemoveNonProgressing(false);
			((InstanceReportable) ifp).setRemoveImmobileOnStart(false);
			break;
		}
		PatternDetectionExperiment pde = new PatternDetectionExperiment(pattern, ifp, m_logLength);
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
	protected Processor getPattern(Point p)
	{
		String pattern_name = p.getString(P_PATTERN);
		float alpha = (Float) p.get(P_ALPHA);
		switch (pattern_name)
		{
			case TooManyActionsPattern.NAME:
			{
				int num_payloads = s_defaultPayloads;
				if (p.get(TooManyActionsPattern.P_PAYLOADS) != null)
				{
					num_payloads = p.getInt(TooManyActionsPattern.P_PAYLOADS);
				}
				List<String> payloads = new ArrayList<String>();
				for (int i = 0; i < num_payloads; i++)
				{
					payloads.add(Integer.toString(i));
				}
				RandomInteger ri = new RandomInteger().setSeed(m_seed);
				RandomInteger id_picker = new RandomInteger().setInterval(0, 1000).setSeed(m_seed);
				RandomFloat rf = new RandomFloat().setSeed(m_seed);
				int threshold = s_defaultThreshold;
				if (p.get(TooManyActionsMonitor.P_THRESHOLD) != null)
				{
					threshold = p.getInt(TooManyActionsMonitor.P_THRESHOLD);
				}
				List<String> safe_payloads = new ArrayList<String>(threshold);
				for (int i = 0; i < threshold - 1; i++)
				{
					safe_payloads.add(payloads.get(i));
				}
				InjectedPatternPicker<Tuple> ipp = new InjectedPatternPicker<Tuple>(
						new NormalActionsPattern(safe_payloads, ri, id_picker), 
						new TooManyActionsPattern(payloads, new RandomBoolean(0.5f).setSeed(m_seed), new RandomInteger().setSeed(m_seed)), 1, alpha, rf);
				InjectedPatternSource<Tuple> ips = new InjectedPatternSource<Tuple>(ipp, m_logLength);
				//QueueSource ips = new QueueSource().setEvents(new Tuple(0, "0"), new Tuple(0, "1"), new Tuple(0, "2")).loop(false);
				return ips;
			}
			case BFollowsAPattern.NAME:
			{
				RandomFloat rf = new RandomFloat().setSeed(m_seed);
				InjectedPatternPicker<String> ipp = new InjectedPatternPicker<String>(new RandomAlphabet(rf, "a", "c", "d"), new BFollowsAPattern(), 1, alpha, rf);
				InjectedPatternSource<String> ips = new InjectedPatternSource<String>(ipp, m_logLength);
				return ips;
			}
		}
		return null;
	}
	
	/**
	 * Gets the monitor instance corresponding to a given pattern name.
	 * @param p The point containing the information about the experiment to
	 * create
	 * @return The picker
	 */
	protected Processor getMonitor(Point p)
	{
		String pattern_name = p.getString(P_PATTERN);
		
		switch (pattern_name)
		{
			case TooManyActionsMonitor.NAME:
			{
				int threshold = s_defaultThreshold;
				if (p.get(TooManyActionsMonitor.P_THRESHOLD) != null)
				{
					threshold = p.getInt(TooManyActionsPattern.P_PAYLOADS);
				}
				return new TooManyActionsMonitorGroup(threshold);
			}
			case BFollowsAMonitor.NAME:
			{
				InstrumentedFindPattern ifp = new InstrumentedFindPattern(new BFollowsAMonitor());
				return ifp;
			}
		}
		return null;
	}
}
