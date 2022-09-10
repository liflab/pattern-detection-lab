package patternlab.monitor;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.tmf.Fork;
import patternlab.AllEventually;

public class CombinedMonitor extends GroupProcessor
{
	public CombinedMonitor(Processor ... patterns)
	{
		super(patterns.length, 1);
		IndexEventTracker et = new IndexEventTracker();
		Fork f = new Fork(patterns.length);
		AllEventually seq = new AllEventually(patterns.length);
		addProcessors(f, seq);
		for (int i = 0; i < patterns.length; i++)
		{
			Connector.connect(et, f, i, patterns[i], 0);
			Connector.connect(et, patterns[i], 0, seq, i);
			addProcessor(patterns[i]);
		}
		associateInput(0, f, 0);
		associateOutput(0, seq, 0);
		setEventTracker(et);
	}
	
	public static void main(String[] args)
	{
		CombinedMonitor cm = new CombinedMonitor(new AtomicSequence("a", "b"), new AtomicSequence("c", "d"));
		Processor gp = cm.duplicate();
	}
}
