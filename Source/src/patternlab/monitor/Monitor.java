package patternlab.monitor;

import java.util.List;

import ca.uqac.lif.cep.Stateful;
import ca.uqac.lif.cep.SynchronousProcessor;

public abstract class Monitor extends SynchronousProcessor implements Stateful
{
	public Monitor(int in_arity, int out_arity)
	{
		super(in_arity, out_arity);
	}
	
	public abstract List<Integer> getSequence();
	
	@Override
	public abstract Monitor duplicate(boolean with_state);
}
