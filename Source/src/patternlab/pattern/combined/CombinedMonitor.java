package patternlab.pattern.combined;

import java.util.List;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.SinkLast;
import patternlab.InstrumentedFindOccurrences;
import patternlab.monitor.AllMonitors;
import patternlab.monitor.Eventually;
import patternlab.monitor.Sequence;
import patternlab.monitor.FindOccurrences.PatternInstance;

import static patternlab.monitor.EventEquals.eq;

public class CombinedMonitor extends AllMonitors
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = CombinedPattern.NAME;
	
	public CombinedMonitor()
	{
		super(new Sequence(new Eventually(eq("A")), new Eventually(eq("B"))),
				new Sequence(new Eventually(eq("C")), new Eventually(eq("D"))),
				new Sequence(new Eventually(eq("E")), new Eventually(eq("F"))));
	}
	
	@Override
	public CombinedMonitor duplicate(boolean with_state)
	{
		CombinedMonitor cm = new CombinedMonitor();
		copyInto(cm, with_state);
		return cm;
	}
	
	public static void main(String[] args)
	{
		CombinedMonitor mon = new CombinedMonitor();
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
		List<Integer> seq = mon.getSequence();
		List<?> list = (List<?>) last.getLast()[0];
		PatternInstance pi = (PatternInstance) list.get(0);
		
	}
}
