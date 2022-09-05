package patternlab.pattern.toomanyactions;

import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Maps;
import patternlab.InstanceReportable;
import patternlab.InstrumentedFindOccurrences;
import patternlab.monitor.AnySlice;
import patternlab.monitor.FindOccurrences;
import patternlab.pattern.toomanyactions.TooManyActionsMonitor.Flatten;

public class TooManyActionsMonitorGroup extends GroupProcessor implements InstanceReportable
{
	protected final AnySlice m_slice;
	
	protected final InstrumentedFindOccurrences m_pattern;
	
	protected final int m_threshold;
	
	public TooManyActionsMonitorGroup(int threshold)
	{
		super(1, 1);
		m_threshold = threshold;
		TooManyActionsMonitor tmam = new TooManyActionsMonitor(threshold);
		m_pattern = new InstrumentedFindOccurrences(tmam);
		m_slice = new AnySlice(Tuple.getId, m_pattern);
		ApplyFunction values = new ApplyFunction(new FunctionTree(Flatten.instance, Maps.values));
		Connector.connect(m_slice, values);
		addProcessors(m_slice, values);
		associateInput(0, m_slice, 0);
		associateOutput(0, values, 0);
	}

	@Override
	public int getInstances()
	{
		return m_slice.getInstances();
	}

	@Override
	public void setRemoveNonMatches(boolean b)
	{
		m_slice.setRemoveNonMatches(b);
	}

	@Override
	public void setRemoveImmobileOnStart(boolean b)
	{
		m_slice.setRemoveImmobileOnStart(b);
	}

	@Override
	public void setRemoveNonProgressing(boolean b)
	{
		m_slice.setRemoveNonProgressing(b);
	}

	@Override
	public void setRemoveSameState(boolean b)
	{
		m_slice.setRemoveSameState(b);
	}	
	
	/**
	 * Main method for testing purposes only.
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		int threshold = 3;
		TooManyActionsMonitorGroup tmam = new TooManyActionsMonitorGroup(threshold);
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
