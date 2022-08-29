package patternlab.pattern;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.ltl.FindMonitorPattern;
import ca.uqac.lif.cep.ltl.FindMonitorPattern.PatternInstance;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.ltl.Troolean.Value;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Maps;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

public class TooManyActionsMonitor extends GroupProcessor
{
	public static final String NAME = TooManyActionsPattern.NAME;
	
	public static final String P_THRESHOLD = "Threshold payloads";
	
	protected int m_threshold;
	
	@Override
	public TooManyActionsMonitor duplicate(boolean with_state)
	{
		return new TooManyActionsMonitor(m_threshold);
	}

	public TooManyActionsMonitor(int threshold)
	{
		super(1, 1);
		m_threshold = threshold;
		ApplyFunction payload = new ApplyFunction(Tuple.getPayload);
		Sets.PutInto put = new Sets.PutInto();
		ApplyFunction size = new ApplyFunction(Bags.getSize);
		ApplyFunction gt = new ApplyFunction(new FunctionTree(new SoftCast(), new FunctionTree(Numbers.isGreaterThan, StreamVariable.X, new Constant(threshold))));
		Connector.connect(payload, put, size, gt);
		addProcessors(payload, put, size, gt);
		associateInput(0, payload, 0);
		associateOutput(0, gt, 0);
	}
	
	@SuppressWarnings("rawtypes")
	public static class Flatten extends UnaryFunction<Collection,Collection>
	{
		public static final Flatten instance = new Flatten();
		
		protected Flatten()
		{
			super(Collection.class, Collection.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Collection getValue(Collection c)
		{
			List<Object> set = new ArrayList<Object>();
			for (Object o : c)
			{
				if (o instanceof Collection)
				{
					if (!((Collection) o).isEmpty())
					{
						set.addAll((Collection) o);
					}
				}
				else
				{
					set.add(o);
				}
			}
			return set;
		}
		
		@Override
		public Flatten duplicate(boolean with_state)
		{
			return this;
		}
	}
	
	public static class SoftCast extends UnaryFunction<Boolean,Troolean.Value>
	{
		public SoftCast()
		{
			super(Boolean.class, Troolean.Value.class);
		}

		@Override
		public Value getValue(Boolean x)
		{
			if (Boolean.FALSE.equals(x))
			{
				return Value.INCONCLUSIVE;
			}
			return Value.TRUE;
		}
		
		@Override
		public SoftCast duplicate(boolean with_state)
		{
			return this;
		}
	}
	
	/**
	 * Main method for testing purposes only.
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		int threshold = 3;
		TooManyActionsMonitor tmam = new TooManyActionsMonitor(threshold);
		FindMonitorPattern fp = new FindMonitorPattern(tmam);
		Slice slice = new Slice(Tuple.getId, fp);
		ApplyFunction values = new ApplyFunction(new FunctionTree(Flatten.instance, Maps.values));
		Connector.connect(slice, values);
		QueueSink print = new QueueSink();
		Connector.connect(values, print);
		Pushable p = slice.getPushableInput();
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
