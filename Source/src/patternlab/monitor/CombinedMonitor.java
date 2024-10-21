package patternlab.monitor;

import java.util.Arrays;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.tmf.Fork;
import patternlab.AllEventually;

public class CombinedMonitor extends GroupProcessor
{
	protected final Processor[] m_patterns;
	
	public CombinedMonitor(EventTracker tracker, Processor ... patterns)
	{
		super(patterns.length, 1);
		m_patterns = patterns;
		setEventTracker(tracker);
		Fork f = new Fork(patterns.length);
		AllEventually seq = new AllEventually(patterns.length);
		addProcessors(f, seq);
		for (int i = 0; i < patterns.length; i++)
		{
			Connector.connect(m_innerTracker, f, i, patterns[i], 0);
			Connector.connect(m_innerTracker, patterns[i], 0, seq, i);
			addProcessor(patterns[i]);
		}
		associateInput(0, f, 0);
		associateOutput(0, seq, 0);
	}
	
	@Override
	public CombinedMonitor duplicate(boolean with_state)
	{
		Processor[] patterns = new Processor[m_patterns.length];
		for (int i = 0; i < patterns.length; i++)
		{
			patterns[i] = m_patterns[i].duplicate();
		}
		return new CombinedMonitor(getEventTracker().getCopy(false), patterns);
	}
	
	public static void main(String[] args)
	{
		CombinedMonitor cm = new CombinedMonitor(new IndexEventTracker(), new AtomicSequence<String>(new IndexEventTracker(), Arrays.asList("a", "b")), new AtomicSequence<String>(new IndexEventTracker(), Arrays.asList("c", "d")));
		cm.duplicate();
	}
}
