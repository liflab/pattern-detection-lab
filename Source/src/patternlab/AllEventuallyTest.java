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

import static ca.uqac.lif.cep.ltl.Troolean.Value.FALSE;
import static ca.uqac.lif.cep.ltl.Troolean.Value.INCONCLUSIVE;
import static ca.uqac.lif.cep.ltl.Troolean.Value.TRUE;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.provenance.EventFunction.InputValue;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

public class AllEventuallyTest
{
	@Test
	public void test1()
	{
		IndexEventTracker tracker = new IndexEventTracker();
		AllEventually ae = new AllEventually(3);
		ae.setEventTracker(tracker);
		SinkLast sink = new SinkLast();
		Object[] last;
		Connector.connect(ae, sink);
		Pushable p0 = ae.getPushableInput(0);
		Pushable p1 = ae.getPushableInput(1);
		Pushable p2 = ae.getPushableInput(2);
		p0.push(INCONCLUSIVE); p1.push(INCONCLUSIVE); p2.push(INCONCLUSIVE);
		last = sink.getLast();
		assertEquals(INCONCLUSIVE, last[0]);
		p0.push(TRUE); p1.push(INCONCLUSIVE); p2.push(INCONCLUSIVE);
		last = sink.getLast();
		assertEquals(INCONCLUSIVE, last[0]);
		p0.push(FALSE); p1.push(INCONCLUSIVE); p2.push(INCONCLUSIVE);
		last = sink.getLast();
		assertEquals(INCONCLUSIVE, last[0]);
		p0.push(TRUE); p1.push(TRUE); p2.push(INCONCLUSIVE);
		last = sink.getLast();
		assertEquals(INCONCLUSIVE, last[0]);
		p0.push(TRUE); p1.push(INCONCLUSIVE); p2.push(TRUE);
		last = sink.getLast();
		assertEquals(TRUE, last[0]);
		ProvenanceNode node = tracker.getProvenanceTree(ae, 0, 4);
		List<ProvenanceNode> parents = node.getParents();
		assertEquals(3, parents.size());
		isDataPoint((InputValue) parents.get(0).getNodeFunction(), 0, 4);
		isDataPoint((InputValue) parents.get(1).getNodeFunction(), 1, 3);
		isDataPoint((InputValue) parents.get(2).getNodeFunction(), 2, 4);
	}
	
	@Test
	public void test2()
	{
		IndexEventTracker tracker = new IndexEventTracker();
		AllEventually ae = new AllEventually(3);
		ae.setEventTracker(tracker);
		SinkLast sink = new SinkLast();
		Object[] last;
		Connector.connect(ae, sink);
		Pushable p0 = ae.getPushableInput(0);
		Pushable p1 = ae.getPushableInput(1);
		Pushable p2 = ae.getPushableInput(2);
		p0.push(INCONCLUSIVE); p1.push(INCONCLUSIVE); p2.push(INCONCLUSIVE);
		last = sink.getLast();
		assertEquals(INCONCLUSIVE, last[0]);
		p0.push(INCONCLUSIVE); p1.push(TRUE); p2.push(INCONCLUSIVE);
		last = sink.getLast();
		assertEquals(INCONCLUSIVE, last[0]);
		p0.push(FALSE); p1.push(TRUE); p2.push(INCONCLUSIVE);
		last = sink.getLast();
		assertEquals(INCONCLUSIVE, last[0]);
		p0.push(TRUE); p1.push(TRUE); p2.push(INCONCLUSIVE);
		last = sink.getLast();
		assertEquals(INCONCLUSIVE, last[0]);
		p0.push(TRUE); p1.push(INCONCLUSIVE); p2.push(TRUE);
		last = sink.getLast();
		assertEquals(TRUE, last[0]);
		ProvenanceNode node = tracker.getProvenanceTree(ae, 0, 4);
		List<ProvenanceNode> parents = node.getParents();
		assertEquals(3, parents.size());
		isDataPoint((InputValue) parents.get(0).getNodeFunction(), 0, 4);
		isDataPoint((InputValue) parents.get(1).getNodeFunction(), 1, 3);
		isDataPoint((InputValue) parents.get(2).getNodeFunction(), 2, 4);
	}
	
	public static void isDataPoint(InputValue iv, int stream_index, int stream_pos)
	{
		assertEquals(stream_index, iv.getStreamIndex());
		assertEquals(stream_pos, iv.getStreamPosition());
	}
}
