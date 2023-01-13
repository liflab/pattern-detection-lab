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
package patternlab.pattern.incomplete;

import java.util.Arrays;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IfThenElse;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.ltl.SoftCast;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Numbers;
import patternlab.Monitor;
import patternlab.SumSlice;
import patternlab.monitor.AtomicSequence;
import patternlab.pattern.Tuple;

public class IncompletePatternMonitor extends GroupProcessor implements Monitor
{
	public static final String NAME = IncompletePattern.NAME;
	
	/**
	 * The name of parameter "Threshold"
	 */
	public static final String P_THRESHOLD = "Threshold";
	
	protected final SumSlice m_slice;
	
	public IncompletePatternMonitor(EventTracker tracker, int k)
	{
		super(1, 1);
		setEventTracker(tracker);
		m_slice = new SumSlice(Tuple.getId, new SliceHandling(tracker.getCopy(false)));
		m_slice.setEventTracker(tracker.getCopy(false));
		ApplyFunction threshold = new ApplyFunction(new FunctionTree(SoftCast.instance,
				new FunctionTree(Numbers.isGreaterThan,
						StreamVariable.X,
						new Constant(k))));
		Connector.connect(m_innerTracker, m_slice, 0, threshold, 0);
		Cumulate or = new Cumulate(new CumulativeFunction<Troolean.Value>(Troolean.OR_FUNCTION));
		Connector.connect(m_innerTracker, threshold, 0, or, 0);
		addProcessors(m_slice, threshold, or);
		associateInput(0, m_slice, 0);
		associateOutput(0, or, 0);
	}
	
	@Override
	public int getInstances()
	{
		return m_slice.getInstances();
	}

	@Override
	public void setRemoveNonMatches(boolean b)
	{
		m_slice.setRemoveNonMatches(b);
	}

	@Override
	public void setRemoveImmobileOnStart(boolean b)
	{
		m_slice.setRemoveImmobileOnStart(b);
	}

	@Override
	public void setRemoveNonProgressing(boolean b)
	{
		m_slice.setRemoveNonProgressing(b);
	}

	@Override
	public void setRemoveSameState(boolean b)
	{
		m_slice.setRemoveSameState(b);
	}
	
	@Override
	public void setSpawn(boolean b)
	{
		m_slice.setSpawn(b);
	}
	
	public static class SliceHandling extends GroupProcessor
	{
		public SliceHandling(EventTracker tracker)
		{
			super(1, 1);
			setEventTracker(tracker);
			ApplyFunction get_p = new ApplyFunction(Tuple.getPayload);
			AtomicSequence<String> seq = new AtomicSequence<String>(tracker.getCopy(false), Arrays.asList(new String[]{"A", "B"}));
			Connector.connect(m_innerTracker, get_p, 0, seq, 0);
			Fork f2 = new Fork(3);
			Connector.connect(m_innerTracker, seq, 0, f2, 0);
			ApplyFunction eq = new ApplyFunction(new FunctionTree(Equals.instance, StreamVariable.X, new Constant(Troolean.Value.INCONCLUSIVE)));
			Connector.connect(m_innerTracker, f2, 0, eq, 0);
			TurnInto ti_1 = new TurnInto(1);
			Connector.connect(m_innerTracker, f2, 1, ti_1, 0);
			TurnInto ti_0 = new TurnInto(0);
			Connector.connect(m_innerTracker, f2, 2, ti_0, 0);
			ApplyFunction ite = new ApplyFunction(IfThenElse.instance);
			Connector.connect(m_innerTracker, eq, 0, ite, 0);
			Connector.connect(m_innerTracker, ti_1, 0, ite, 1);
			Connector.connect(m_innerTracker, ti_0, 0, ite, 2);
			addProcessors(get_p, seq, f2, eq, ti_1, ti_0, ite);
			associateInput(0, get_p, 0);
			associateOutput(0, ite, 0);
		}
		
		@Override
		public SliceHandling duplicate(boolean with_state)
		{
			if (with_state)
			{
				throw new UnsupportedOperationException("Stateful duplication not supported for this processor");
			}
			return new SliceHandling(m_innerTracker.getCopy(false));
		}
	}
}
