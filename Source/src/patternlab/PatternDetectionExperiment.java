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

import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.ltl.FindPattern.PatternInstance;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.labpal.experiment.Experiment;
import ca.uqac.lif.labpal.experiment.ExperimentException;
import ca.uqac.lif.labpal.util.Stopwatch;
import ca.uqac.lif.synthia.Bounded;
import ca.uqac.lif.units.Scalar;
import ca.uqac.lif.units.Time;
import ca.uqac.lif.units.si.Millisecond;

public class PatternDetectionExperiment extends Experiment
{
	/**
	 * Name of the parameter "Time".
	 */
	public static final String P_TIME = "Time";
	
	/**
	 * Name of the parameter "Detected patterns".
	 */
	public static final String P_DETECTED = "Detected patterns";
	
	/**
	 * Name of the parameter "Witness events".
	 */
	public static final String P_WITNESS_EVENTS = "Witness events";
	
	/**
	 * Name of the parameter "Witness events per instance".
	 */
	public static final String P_AVG_WITNESS_EVENTS = "Witness events per instance";
	
	/**
	 * Name of the parameter "Log length".
	 */
	public static final String P_LOG_LENGTH = "Log length";
	
	/**
	 * Name of the parameter "Max instances".
	 */
	public static final String P_MAX_INSTANCES = "Max instances";
	
	/**
	 * Name of the parameter "Algorithm".
	 */
	public static final String P_ALGORITHM = "Algorithm";
	
	/**
	 * Name of the parameter "Pattern".
	 */
	public static final String P_PATTERN = "Pattern";
	
	/**
	 * Name of the parameter "alpha".
	 */
	public static final String P_ALPHA = "\u03b1";
	
	/**
	 * A processor producing the events to be analyzed.
	 */
	/*@ non_null @*/ protected final Processor m_log;
	
	/**
	 * A processor detecting instances of the pattern to look for.
	 */
	/*@ non_null @*/ protected final InstrumentedFindPattern m_pattern;
	
	public PatternDetectionExperiment(Processor log, InstrumentedFindPattern pat)
	{
		super();
		m_log = log;
		m_pattern = pat;
		describe(P_ALGORITHM, "The technique used to detect patterns", Scalar.DIMENSION);
		describe(P_PATTERN, "The pattern to detect", Scalar.DIMENSION);
		describe(P_ALPHA, "The density of non-pattern events in the log", Scalar.DIMENSION);
		describe(P_TIME, "The total time taken to process the log", Time.DIMENSION);
		describe(P_DETECTED, "The number of distinct pattern instances detected in the log", Scalar.DIMENSION);
		describe(P_WITNESS_EVENTS, "The total number of events from the log included as witness of a pattern instance", Scalar.DIMENSION);
		describe(P_AVG_WITNESS_EVENTS, "The average number of witness events from the log included as witness in a pattern instance", Scalar.DIMENSION);
		describe(P_LOG_LENGTH, "The total number of events in the log", Scalar.DIMENSION);
		describe(P_MAX_INSTANCES, "The maximum number of monitor instances used by the analysis", Scalar.DIMENSION);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws ExperimentException
	{
		QueueSink sink = new QueueSink();
		Connector.connect(m_pattern, sink);
		Queue<?> q = sink.getQueue();
		Pushable ph = m_pattern.getPushableInput();
		Pullable pl = m_log.getPullableOutput();
		int len = 0;
		int witnesses = 0;
		int detected = 0;
		int max_instances = 0;
		Stopwatch.start(this);
		while (pl.hasNext())
		{
			len++;
			Object e = pl.pull();
			ph.push(e);
			//System.out.print(e + " ");
			max_instances = Math.max(max_instances, m_pattern.getInstances());
			if (!q.isEmpty())
			{
				List<PatternInstance> instances = (List<PatternInstance>) q.remove();
				detected += instances.size();
				for (PatternInstance pi : instances)
				{
					witnesses += pi.getSubSequence().size();
				}
			}
		}
		writeOutput(P_TIME, new Millisecond(Stopwatch.stop(this)));
		writeOutput(P_DETECTED, detected);
		writeOutput(P_WITNESS_EVENTS, witnesses);
		writeOutput(P_AVG_WITNESS_EVENTS, (float) witnesses / (float) detected);
		writeOutput(P_LOG_LENGTH, len);
		writeOutput(P_MAX_INSTANCES, max_instances);
	}
	
	@Override
	public String getDescription()
	{
		return "<p>Detects the presence of a specific pattern in a log, and computes statistics about the process.</p>";
	}
}
