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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

import static ca.uqac.lif.cep.ltl.Troolean.Value.FALSE;
import static ca.uqac.lif.cep.ltl.Troolean.Value.INCONCLUSIVE;
import static ca.uqac.lif.cep.ltl.Troolean.Value.TRUE;


public class SequenceTest
{
	@Test
	public void test1()
	{
		IndexEventTracker tracker = new IndexEventTracker();
		Sequence seq = new Sequence(3);
		seq.setEventTracker(tracker);
		SinkLast sink = new SinkLast();
		Object[] last;
		Connector.connect(seq, sink);
		Pushable p0 = seq.getPushableInput(0);
		Pushable p1 = seq.getPushableInput(1);
		Pushable p2 = seq.getPushableInput(2);
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
		ProvenanceNode node = tracker.getProvenanceTree(seq, 0, 4);
		List<ProvenanceNode> parents = node.getParents();
		assertEquals(3, parents.size());
		ProvenanceNode parent1 = parents.get(0);
		parent1.getNodeFunction();
		System.out.println(node);
	}
}
