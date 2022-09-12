package patternlab.monitor;

import java.util.Arrays;
import java.util.List;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.ltl.TrooleanCast;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Equals;
import patternlab.Sequence;

public class AtomicSequence<T> extends GroupProcessor
{
	public AtomicSequence(EventTracker tracker, List<T> events)
	{
		super(events.size(), 1);
		setEventTracker(tracker);
		Fork f = new Fork(events.size());
		Sequence seq = new Sequence(events.size(), true);
		addProcessors(f, seq);
		ApplyFunction[] are = new ApplyFunction[events.size()];
		for (int i = 0; i < events.size(); i++)
		{
			are[i] = new ApplyFunction(new FunctionTree(TrooleanCast.instance, new FunctionTree(Equals.instance, StreamVariable.X, new Constant(events.get(i)))));
			Connector.connect(m_innerTracker, f, i, are[i], 0);
			Connector.connect(m_innerTracker, are[i], 0, seq, i);
			addProcessor(are[i]);
		}
		associateInput(0, f, 0);
		associateOutput(0, seq, 0);
	}
	
	public static void main(String[] args)
	{
		IndexEventTracker tracker = new IndexEventTracker();
		AtomicSequence<String> as = new AtomicSequence<String>(tracker, Arrays.asList("a", "b"));
		as.duplicate();
	}
}
