package patternlab.pattern;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.ltl.FindPattern;
import ca.uqac.lif.cep.ltl.FindPattern.PatternInstance;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.ltl.Troolean.Value;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Sets;

import java.util.List;

public class TooManyActionsTest
{
	public static void main(String[] args)
	{
		int threshold = 3;
		MyPattern tmam = new MyPattern(threshold);
		FindPattern fp = new FindPattern(tmam);
		QueueSink print = new QueueSink();
		Connector.connect(fp, print);
		Pushable p = fp.getPushableInput();
		p.push(new Tuple(1, "a"));
		p.push(new Tuple(1, "a"));
		p.push(new Tuple(1, "b"));
		p.push(new Tuple(1, "c"));
		//p.push(new Tuple(2, "c"));
		p.push(new Tuple(1, "d"));
		PatternInstance ins = ((List<PatternInstance>) print.getQueue().remove()).get(0);
		System.out.println(ins);
	}
	
	protected static class MyPattern extends GroupProcessor
	{
		protected int m_threshold;
		
		public MyPattern(int threshold)
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
		
		@Override
		public MyPattern duplicate(boolean with_state)
		{
			return new MyPattern(m_threshold);
		}
	}
	
		/*
	public static void main(String[] args)
	{
		TooManyActionsMonitor tmam = new TooManyActionsMonitor(3);
		Print print = new Print();
		Connector.connect(tmam, print);
		Pushable p = tmam.getPushableInput();
		p.push(new Tuple(1, "a"));
		p.push(new Tuple(1, "b"));
		p.push(new Tuple(1, "c"));
		p.push(new Tuple(2, "c"));
		p.push(new Tuple(1, "d"));
	}
	*/
	
	protected static class SoftCast extends UnaryFunction<Boolean,Troolean.Value>
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
}
