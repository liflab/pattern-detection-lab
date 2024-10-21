/*
  Experimentation of pattern detection by monitors
  Copyright (C) 2022-2023 Sylvain Hallé

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

import static org.junit.Assert.*;

import java.util.Queue;
import java.util.Set;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.ltl.Troolean;
import patternlab.FindOccurrences;
import patternlab.pattern.Tuple;
import patternlab.pattern.incomplete.IncompletePatternMonitor.SliceHandling;

public class IncompletePatternMonitorTest
{
		@Test
		public void testInner1()
		{
			IndexEventTracker tracker = new IndexEventTracker();
			SliceHandling sh = new SliceHandling(tracker);
			QueueSink sink = new QueueSink();
			Queue<Object> q = sink.getQueue();
			Connector.connect(sh, sink);
			Pushable p = sh.getPushableInput();
			p.push(new Tuple(0, "A"));
			assertEquals(1, ((Number) q.remove()).intValue());
			p.push(new Tuple(0, "C"));
			assertEquals(1, ((Number) q.remove()).intValue());
			p.push(new Tuple(0, "B"));
			assertEquals(0, ((Number) q.remove()).intValue());
		}
		
		@Test
		public void testOuter1()
		{
			IndexEventTracker tracker = new IndexEventTracker();
			IncompletePatternMonitor ipm = new IncompletePatternMonitor(tracker, 1);
			QueueSink sink = new QueueSink();
			Queue<Object> q = sink.getQueue();
			Connector.connect(ipm, sink);
			Pushable p = ipm.getPushableInput();
			p.push(new Tuple(0, "A"));
			assertEquals(Troolean.Value.INCONCLUSIVE, q.remove());
			p.push(new Tuple(0, "B"));
			assertEquals(Troolean.Value.INCONCLUSIVE, q.remove());
			p.push(new Tuple(1, "A"));
			assertEquals(Troolean.Value.INCONCLUSIVE, q.remove());
			p.push(new Tuple(2, "A"));
			assertEquals(Troolean.Value.TRUE, q.remove());
		}
		
		@Test
		public void testOuterState1()
		{
			IndexEventTracker tracker = new IndexEventTracker();
			// First instance
			IncompletePatternMonitor ipm1 = new IncompletePatternMonitor(tracker, 1);
			QueueSink sink1 = new QueueSink();
			sink1.getQueue();
			Connector.connect(ipm1, sink1);
			Pushable p1 = ipm1.getPushableInput();
			// Second instance
			IncompletePatternMonitor ipm2 = new IncompletePatternMonitor(tracker, 1);
			QueueSink sink2 = new QueueSink();
			sink2.getQueue();
			Connector.connect(ipm2, sink2);
			Pushable p2 = ipm2.getPushableInput();
			p1.push(new Tuple(0, "A"));
			p2.push(new Tuple(1, "A"));
			Object state1 = ipm1.getState();
			Object state2 = ipm2.getState();
			assertFalse(state1.equals(state2));
		}
		
		@SuppressWarnings("unchecked")
		@Test
		public void testMonitor1()
		{
			IndexEventTracker tracker = new IndexEventTracker();
			IncompletePatternMonitor ipm = new IncompletePatternMonitor(tracker, 1);
			FindOccurrences monitor = new FindOccurrences(ipm);
			QueueSink sink = new QueueSink();
			Queue<Object> q = sink.getQueue();
			Connector.connect(monitor, sink);
			Pushable p = monitor.getPushableInput();
			Set<Integer> out_set;
			p.push(new Tuple(0, "A"));
			out_set = (Set<Integer>) q.remove();
			assertEquals(0, out_set.size());
			p.push(new Tuple(0, "C"));
			out_set = (Set<Integer>) q.remove();
			assertEquals(0, out_set.size());
			p.push(new Tuple(1, "A"));
			out_set = (Set<Integer>) q.remove();
			assertEquals(3, out_set.size());
		}
}
