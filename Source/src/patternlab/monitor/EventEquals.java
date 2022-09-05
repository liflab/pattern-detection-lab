package patternlab.monitor;

import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.util.Equals;

public class EventEquals extends Condition
{
	public static EventEquals eq(Object o)
	{
		return new EventEquals(o);
	}
	
	protected EventEquals(Object o)
	{
		super(new FunctionTree(Equals.instance, StreamVariable.X, new Constant(o)));
	}
}
