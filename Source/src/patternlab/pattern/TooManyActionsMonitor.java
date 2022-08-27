package patternlab.pattern;

import java.util.Collection;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.ltl.Troolean.Value;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Maps;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Sets;

public class TooManyActionsMonitor extends GroupProcessor
{
	protected int m_threshold;
	
	public TooManyActionsMonitor(int threshold)
	{
		super(1, 1);
		m_threshold = threshold;
		GroupProcessor inside = new GroupProcessor(1, 1);
		{
			ApplyFunction payload = new ApplyFunction(Tuple.getPayload);
			Sets.PutInto put = new Sets.PutInto();
			ApplyFunction size = new ApplyFunction(Bags.getSize);
			ApplyFunction gt = new ApplyFunction(new FunctionTree(Numbers.isGreaterThan, StreamVariable.X, new Constant(m_threshold)));
			Connector.connect(payload, put, size, gt);
			inside.addProcessors(payload, put, size, gt);
			inside.associateInput(0, payload, 0);
			inside.associateOutput(0, gt, 0);
		}
		Slice s = new Slice(Tuple.getId, inside);
		ApplyFunction values = new ApplyFunction(Maps.values);
		Connector.connect(s, values);
		ApplyFunction cast = new ApplyFunction(new ContainsTrue());
		Connector.connect(values, cast);
		addProcessors(s, values, cast);
		associateInput(0, s, 0);
		associateOutput(0, cast, 0);
	}
	
	@Override
	public TooManyActionsMonitor duplicate(boolean with_state)
	{
		return new TooManyActionsMonitor(m_threshold);
	}
	
	@SuppressWarnings("rawtypes")
	protected static class ContainsTrue extends UnaryFunction<Collection,Troolean.Value>
	{
		public ContainsTrue()
		{
			super(Collection.class, Troolean.Value.class);
		}

		@Override
		public Value getValue(Collection c)
		{
			for (Object o : c)
			{
				if (Boolean.TRUE.equals(o))
				{
					return Troolean.Value.TRUE;
				}
			}
			return Troolean.Value.INCONCLUSIVE;
		}
		
		@Override
		public ContainsTrue duplicate(boolean with_state)
		{
			return this;
		}
	}
}
