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
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.experiment.ExperimentFactory;
import ca.uqac.lif.labpal.region.Point;
import ca.uqac.lif.synthia.random.RandomBoolean;
import ca.uqac.lif.synthia.random.RandomFloat;
import ca.uqac.lif.synthia.random.RandomInteger;
import ca.uqac.lif.synthia.string.RandomString;
import ca.uqac.lif.synthia.util.Constant;
import patternlab.pattern.InjectedPatternPicker;
import patternlab.pattern.InjectedPatternSource;
import patternlab.pattern.RandomAlphabet;
import patternlab.pattern.Tuple;
import patternlab.pattern.bfollowsa.LinearMonitor;
import patternlab.pattern.bfollowsa.LinearPattern;
import patternlab.pattern.combined.CombinedPatternsMonitor;
import patternlab.pattern.incomplete.AttackPattern;
import patternlab.pattern.incomplete.IncompletePattern;
import patternlab.pattern.incomplete.IncompletePatternMonitor;
import patternlab.pattern.incomplete.NormalPattern;
import patternlab.pattern.combined.CombinedPattern;
import patternlab.pattern.toomanyactions.NormalActionsPattern;
import patternlab.pattern.toomanyactions.TooManyActionsMonitor;
import patternlab.pattern.toomanyactions.TooManyActionsMonitorGroup;
import patternlab.pattern.toomanyactions.TooManyActionsPattern;

import static patternlab.PatternDetectionExperiment.P_ALGORITHM;
import static patternlab.PatternDetectionExperiment.P_ALPHA;
import static patternlab.PatternDetectionExperiment.P_PATTERN;

public class PatternDetectionExperimentFactory extends ExperimentFactory<PatternDetectionExperiment>
{
	protected static final int s_defaultThreshold = 3;

	protected static final int s_defaultLinearPatternLength = 2;

	protected static final int s_defaultCombinedPatterns = 3;

	protected int m_logLength;

	public PatternDetectionExperimentFactory(Laboratory lab, int log_length)
	{
		super(lab);
		m_logLength = log_length;
	}
	
	public PatternDetectionExperimentFactory(PatternDetectionExperimentFactory factory)
	{
		super(factory);
		m_logLength = factory.m_logLength;
	}

	@Override
	protected PatternDetectionExperiment createExperiment(Point p)
	{
		PatternDetectionExperiment pde = new PatternDetectionExperiment();
		if (!setPattern(p, pde, m_logLength))
		{
			return null;
		}
		if (!setMonitor(p, pde))
		{
			return null;
		}
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
	 * Sets the picker instance corresponding to a given pattern name.
	 * @param p The point containing the information about the experiment to
	 * create
	 * @param e The experiment to which this picker is added
	 * @param log_length The number of events the picker instance is set to
	 * generate
	 */
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
			int pattern_length = s_defaultLinearPatternLength;
			if (p.get(LinearPattern.P_PATTERN_LENGTH) != null)
			{
				pattern_length = p.getInt(LinearPattern.P_PATTERN_LENGTH);
			}
			return setLinearPattern(p, e, log_length, alpha, pattern_length	);
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

	protected void setLog(PatternDetectionExperiment e, Processor p, int log_length)
	{
		e.setLog(p, log_length);
	}
	
	protected boolean setTooManyActionsPattern(Point p, PatternDetectionExperiment e, int log_length, float alpha)
	{
		int num_payloads = 10;
		if (p.get(TooManyActionsPattern.P_PAYLOADS) != null)
		{
			num_payloads = p.getInt(TooManyActionsPattern.P_PAYLOADS);
		}
		List<String> payloads = new ArrayList<String>();
		for (int i = 0; i < num_payloads; i++)
		{
			payloads.add(Integer.toString(i));
		}
		RandomInteger ri = new RandomInteger().setSeed(0);
		RandomInteger id_picker = new RandomInteger().setInterval(0, 1000).setSeed(0);
		RandomFloat rf = new RandomFloat().setSeed(0);
		int threshold = s_defaultThreshold;
		if (p.get(TooManyActionsMonitor.P_THRESHOLD) != null)
		{
			threshold = p.getInt(TooManyActionsMonitor.P_THRESHOLD);
		}
		e.writeInput(TooManyActionsMonitor.P_THRESHOLD, threshold);
		List<String> safe_payloads = new ArrayList<String>(threshold);
		for (int i = 0; i < threshold - 1; i++)
		{
			safe_payloads.add(payloads.get(i));
		}
		InjectedPatternPicker<Tuple> ipp = new InjectedPatternPicker<Tuple>(
				new NormalActionsPattern(safe_payloads, ri, id_picker), 
				new TooManyActionsPattern(payloads, new RandomBoolean(0.5f).setSeed(0), new RandomInteger().setSeed(0)), 1, alpha, rf);
		InjectedPatternSource<Tuple> ips = new InjectedPatternSource<Tuple>(ipp, m_logLength);
		//QueueSource ips = new QueueSource().setEvents(new Tuple(0, "0"), new Tuple(0, "1"), new Tuple(0, "2")).loop(false);
		setLog(e, ips, log_length);
		return true;
	}
	
	protected boolean setLinearPattern(Point p, PatternDetectionExperiment e, int log_length, float alpha, int pattern_length)
	{
		e.writeInput(LinearPattern.P_PATTERN_LENGTH, pattern_length);
		RandomFloat rf = new RandomFloat().setSeed(0);
		InjectedPatternPicker<String> ipp = new InjectedPatternPicker<String>(
				new RandomAlphabet(rf, "a", "c", "d"),
				new LinearPattern<String>(RandomAlphabet.getUppercaseSequence(0, pattern_length)),
				1, alpha, rf);
		InjectedPatternSource<String> ips = new InjectedPatternSource<String>(ipp, m_logLength);
		//QueueSource ips = new QueueSource().setEvents("c", "c", "a", "b").loop(false);
		setLog(e, ips, log_length);
		return true;
	}
	
	protected boolean setCombinedPattern(Point p, PatternDetectionExperiment e, int log_length, float alpha)
	{
		int num_patterns = s_defaultCombinedPatterns;
		if (p.get(CombinedPattern.P_NUM_PATTERNS) != null)
		{
			num_patterns = p.getInt(CombinedPattern.P_NUM_PATTERNS);
		}
		e.writeInput(CombinedPattern.P_NUM_PATTERNS, num_patterns);
		RandomFloat rf = new RandomFloat().setSeed(0);
		InjectedPatternPicker<String> ipp = new InjectedPatternPicker<String>(
				new RandomAlphabet(rf, 26), new CombinedPattern(rf, num_patterns), 1, alpha, rf);//.setMaxInstances(1);
		InjectedPatternSource<String> ips = new InjectedPatternSource<String>(ipp, m_logLength);
		setLog(e, ips, log_length);
		return true;
	}
	
	protected boolean setIncompletePattern(Point p, PatternDetectionExperiment e, int log_length, float alpha)
	{
		int threshold = s_defaultThreshold;
		if (p.get(IncompletePatternMonitor.P_THRESHOLD) != null)
		{
			threshold = p.getInt(IncompletePatternMonitor.P_THRESHOLD);
		}
		e.writeInput(IncompletePatternMonitor.P_THRESHOLD, threshold);
		RandomInteger id_picker = new RandomInteger().setInterval(0, 1000).setSeed(0);
		RandomFloat rf = new RandomFloat().setSeed(0);
		char[] payloads = new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
		RandomInteger char_index_picker = new RandomInteger(0, payloads.length).setSeed(0);
		RandomString payload_picker = new RandomString(new Constant<Integer>(1), char_index_picker, payloads);
		InjectedPatternPicker<Tuple> ipp = new InjectedPatternPicker<Tuple>(
				new NormalPattern(id_picker, payload_picker), 
				new AttackPattern(id_picker, payload_picker), threshold + 1, alpha, rf);
		InjectedPatternSource<Tuple> ips = new InjectedPatternSource<Tuple>(ipp, m_logLength);
		//QueueSource ips = new QueueSource().setEvents(new Tuple(0, "0"), new Tuple(0, "1"), new Tuple(0, "2")).loop(false);
		setLog(e, ips, log_length);
		return true;
	}

	/**
	 * Sets the monitor instance corresponding to a given pattern name.
	 * @param p The point containing the information about the experiment to
	 * create
	 * @param e The experiment to which this monitor is associated
	 */
	protected boolean setMonitor(Point p, PatternDetectionExperiment e)
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
			return setTooManyActionsMonitor(p, e, threshold);
		}
		case IncompletePatternMonitor.NAME:
		{
			int threshold = s_defaultThreshold;
			if (p.get(IncompletePatternMonitor.P_THRESHOLD) != null)
			{
				threshold = p.getInt(IncompletePatternMonitor.P_THRESHOLD);
			}
			return setIncompletePatternMonitor(p, e, threshold);
		}
		case LinearMonitor.NAME:
		{
			int pattern_length = s_defaultLinearPatternLength;
			if (p.get(LinearPattern.P_PATTERN_LENGTH) != null)
			{
				pattern_length = p.getInt(LinearPattern.P_PATTERN_LENGTH);
			}
			return setLinearSequenceMonitor(p, e, pattern_length);
		}
		case CombinedPattern.NAME:
		{
			int num_patterns = s_defaultCombinedPatterns;
			if (p.get(CombinedPattern.P_NUM_PATTERNS) != null)
			{
				num_patterns = p.getInt(CombinedPattern.P_NUM_PATTERNS);
			}
			return setCombinedPatternMonitor(p, e, num_patterns);
		}
		}
		return false;
	}
	
	protected boolean setTooManyActionsMonitor(Point p, PatternDetectionExperiment e, int threshold)
	{
		FindOccurrences m = new FindOccurrences(new TooManyActionsMonitorGroup(threshold, new IndexEventTracker()));
		setupMonitor(p, m);
		e.setMonitor(m);
		return true;
	}
	
	protected boolean setLinearSequenceMonitor(Point p, PatternDetectionExperiment e, int pattern_length)
	{
		FindOccurrences m = new FindOccurrences(new LinearMonitor<String>(new IndexEventTracker(), RandomAlphabet.getUppercaseSequence(0, pattern_length)));
		setupMonitor(p, m);
		e.setMonitor(m);
		return true;
	}
	
	protected boolean setCombinedPatternMonitor(Point p, PatternDetectionExperiment e, int num_patterns)
	{
		FindOccurrences m = new FindOccurrences(new CombinedPatternsMonitor(new IndexEventTracker(), num_patterns));
		setupMonitor(p, m);
		e.setMonitor(m);
		return true;
	}
	
	protected boolean setIncompletePatternMonitor(Point p, PatternDetectionExperiment e, int threshold)
	{
		FindOccurrences m = new FindOccurrences(new IncompletePatternMonitor(new IndexEventTracker(), threshold));
		//m.setSpawn(false);
		setupMonitor(p, m);
		e.setMonitor(m);
		return true;
	}

	protected void setupMonitor(Point p, Monitor m)
	{
		switch (p.getString(P_ALGORITHM))
		{
		case Monitor.DISTINCT_STATES:
			m.setRemoveNonProgressing(false);
			break;
		case Monitor.FIRST_STEP:
			m.setRemoveSameState(false);
			m.setRemoveNonProgressing(false);
			break;
		case Monitor.DIRECT:
			m.setRemoveSameState(false);
			m.setRemoveNonProgressing(false);
			m.setRemoveImmobileOnStart(false);
			break;
		}
	}
}
