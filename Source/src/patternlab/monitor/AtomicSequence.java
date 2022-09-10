package patternlab.monitor;

import ca.uqac.lif.cep.Connector;
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

public class AtomicSequence extends GroupProcessor
{
	public AtomicSequence(Object ... events)
	{
		super(events.length, 1);
		IndexEventTracker et = new IndexEventTracker();
		Fork f = new Fork(events.length);
		Sequence seq = new Sequence(events.length, true);
		addProcessors(f, seq);
		ApplyFunction[] are = new ApplyFunction[events.length];
		for (int i = 0; i < events.length; i++)
		{
			are[i] = new ApplyFunction(new FunctionTree(TrooleanCast.instance, new FunctionTree(Equals.instance, StreamVariable.X, new Constant(events[i]))));
			Connector.connect(et, f, i, are[i], 0);
			Connector.connect(et, are[i], 0, seq, i);
			addProcessor(are[i]);
		}
		associateInput(0, f, 0);
		associateOutput(0, seq, 0);
		setEventTracker(et);
	}
	
	public static void main(String[] args)
	{
		AtomicSequence as = new AtomicSequence("a", "b");
		as.duplicate();
	}
}
