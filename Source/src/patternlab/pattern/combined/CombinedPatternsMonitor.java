package patternlab.pattern.combined;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.petitpoucet.ProvenanceNode;
import patternlab.monitor.AtomicSequence;
import patternlab.monitor.CombinedMonitor;

public class CombinedPatternsMonitor extends CombinedMonitor
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = CombinedPattern.NAME;
	
	public CombinedPatternsMonitor()
	{
		super(new AtomicSequence("A", "B"),
				new AtomicSequence("C", "D"),
				new AtomicSequence("E", "F"));
	}
		
	public static void main(String[] args)
	{
		CombinedPatternsMonitor mon = new CombinedPatternsMonitor();
		IndexEventTracker tracker = new IndexEventTracker();
		mon.setEventTracker(tracker);
		//InstrumentedFindOccurrences pat = new InstrumentedFindOccurrences(mon);
		//pat.setRemoveImmobileOnStart(false);
		//pat.setRemoveSameState(false);
		//pat.setRemoveNonProgressing(false);
		Pushable p = mon.getPushableInput();
		SinkLast last = new SinkLast();
		Connector.connect(mon, last);
		Object state;
		state = mon.getState();
		p.push("A");
		state = mon.getState();
		p.push("B");
		state = mon.getState();
		p.push("x");
		state = mon.getState();
		p.push("C");
		state = mon.getState();
		p.push("D");
		state = mon.getState();
		p.push("E");
		state = mon.getState();
		p.push("F");
		state = mon.getState();
		ProvenanceNode root = tracker.getProvenanceTree(mon, 0, 6);
		
	}
}
