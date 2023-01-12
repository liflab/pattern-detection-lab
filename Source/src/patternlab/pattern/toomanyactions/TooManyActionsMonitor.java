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
package patternlab.pattern.toomanyactions;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.ltl.SoftCast;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Sets;
import patternlab.FindOccurrences;
import patternlab.pattern.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

public class TooManyActionsMonitor extends GroupProcessor
{
	public static final String NAME = TooManyActionsPattern.NAME;
	
	public static final String P_THRESHOLD = "Threshold payloads";
	
	protected int m_threshold;
	
	public TooManyActionsMonitor(int threshold, EventTracker tracker)
	{
		super(1, 1);
		m_threshold = threshold;
		setEventTracker(tracker);
		ApplyFunction payload = new ApplyFunction(Tuple.getPayload);
		Sets.PutInto put = new Sets.PutInto();
		ApplyFunction size = new ApplyFunction(Bags.getSize);
		ApplyFunction gt = new ApplyFunction(new FunctionTree(SoftCast.instance, new FunctionTree(Numbers.isGreaterThan, StreamVariable.X, new Constant(threshold))));
		Connector.connect(m_innerTracker, payload, put, size, gt);
		addProcessors(payload, put, size, gt);
		associateInput(0, payload, 0);
		associateOutput(0, gt, 0);
	}
	
	@Override
	public TooManyActionsMonitor duplicate(boolean with_state)
	{
		return new TooManyActionsMonitor(m_threshold, getEventTracker().getCopy(false));
	}
	
	@SuppressWarnings("rawtypes")
	public static class Flatten extends UnaryFunction<Collection,Collection>
	{
		public static final Flatten instance = new Flatten();
		
		protected Flatten()
		{
			super(Collection.class, Collection.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Collection getValue(Collection c)
		{
			List<Object> set = new ArrayList<Object>();
			for (Object o : c)
			{
				if (o instanceof Collection)
				{
					if (!((Collection) o).isEmpty())
					{
						set.addAll((Collection) o);
					}
				}
				else
				{
					set.add(o);
				}
			}
			return set;
		}
		
		@Override
		public Flatten duplicate(boolean with_state)
		{
			return this;
		}
	}
	
	
	/**
	 * Main method for testing purposes only.
	 * @param args
	 */
	public static void main(String[] args)
	{
		int threshold = 3;
		IndexEventTracker tracker = new IndexEventTracker();
		TooManyActionsMonitor tmam = new TooManyActionsMonitor(threshold, tracker);
		FindOccurrences fp = new FindOccurrences(tmam);
		QueueSink print = new QueueSink();
		Connector.connect(tmam, print);
		Pushable p = tmam.getPushableInput();
		p.push(new Tuple(1, "a"));
		p.push(new Tuple(1, "b"));
		p.push(new Tuple(1, "c"));
		p.push(new Tuple(1, "a"));
		p.push(new Tuple(1, "c"));
		p.push(new Tuple(1, "d"));
		//p.push(new Tuple(2, "c"));
		//p.push(new Tuple(1, "d"));
		Queue<?> q = print.getQueue();
		//PatternInstance ins = ((List<PatternInstance>) print.getLast()[0]).get(0);
		System.out.println(q);
	}

}
