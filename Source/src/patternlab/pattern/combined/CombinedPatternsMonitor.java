package patternlab.pattern.combined;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.petitpoucet.ProvenanceNode;
import patternlab.monitor.AtomicSequence;
import patternlab.monitor.CombinedMonitor;
import patternlab.pattern.RandomAlphabet;

public class CombinedPatternsMonitor extends CombinedMonitor
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = CombinedPattern.NAME;
	
	public CombinedPatternsMonitor(EventTracker tracker, int num_patterns)
	{
		super(tracker, getPatterns(num_patterns));
	}
	
	protected static Processor[] getPatterns(int num_patterns)
	{
		Processor[] patterns = new Processor[num_patterns];
		for (int i = 0; i < num_patterns; i++)
		{
			patterns[i] = new AtomicSequence<String>(new IndexEventTracker(), RandomAlphabet.getUppercaseSequence(i * 5, 5));
		}
		return patterns;
	}
		
	public static void main(String[] args)
	{
		IndexEventTracker tracker = new IndexEventTracker();
		CombinedPatternsMonitor mon = new CombinedPatternsMonitor(tracker, 3);
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
