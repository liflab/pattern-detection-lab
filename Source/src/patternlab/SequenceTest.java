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
