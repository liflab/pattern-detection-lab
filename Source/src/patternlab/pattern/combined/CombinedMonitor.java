package patternlab.pattern.combined;

import java.util.Arrays;
import java.util.List;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.ltl.FindMonitorPattern;
import ca.uqac.lif.cep.ltl.FindMonitorPattern.PatternInstance;
import ca.uqac.lif.cep.tmf.SinkLast;
import patternlab.InstrumentedFindPattern;
import patternlab.pattern.CartesianProductMonitor;
import patternlab.pattern.SequenceMonitor;

public class CombinedMonitor extends CartesianProductMonitor
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = CombinedPattern.NAME;
	
	public CombinedMonitor()
	{
		super(new SequenceMonitor<String>(Arrays.asList("A", "B")),
				new SequenceMonitor<String>(Arrays.asList("C", "D")),
				new SequenceMonitor<String>(Arrays.asList("E", "F")));
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
		InstrumentedFindPattern pat = new InstrumentedFindPattern(mon);
		pat.setRemoveImmobileOnStart(false);
		pat.setRemoveSameState(false);
		pat.setRemoveNonProgressing(false);
		Pushable p = pat.getPushableInput();
		SinkLast last = new SinkLast();
		Connector.connect(pat, last);
		Object state;
		//state = mon.getState();
		p.push("A");
		//state = mon.getState();
		p.push("B");
		//state = mon.getState();
		p.push("x");
		//state = mon.getState();
		p.push("C");
		//state = mon.getState();
		p.push("D");
		//state = mon.getState();
		p.push("E");
		//state = mon.getState();
		p.push("F");
		state = mon.getState();
		List<?> list = (List<?>) last.getLast()[0];
		PatternInstance pi = (PatternInstance) list.get(0);
		
	}
}
