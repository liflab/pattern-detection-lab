package patternlab.pattern.toomanyactions;

import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.tmf.QueueSink;
import patternlab.SomeSlice;
import patternlab.pattern.Tuple;

public class TooManyActionsMonitorGroup extends SomeSlice
{
	public TooManyActionsMonitorGroup(int threshold, EventTracker tracker)
	{
		super(Tuple.getId, new TooManyActionsMonitor(threshold, tracker));
	}

	/**
	 * Main method for testing purposes only.
	 * @param args
	 */
	public static void main(String[] args)
	{
		int threshold = 3;
		IndexEventTracker tracker = new IndexEventTracker();
		TooManyActionsMonitorGroup tmam = new TooManyActionsMonitorGroup(threshold, tracker);
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
