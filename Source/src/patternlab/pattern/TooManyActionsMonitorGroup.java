package patternlab.pattern;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.util.Maps;
import patternlab.InstanceReportable;
import patternlab.InstrumentedFindPattern;
import patternlab.InstrumentedSlice;
import patternlab.pattern.TooManyActionsMonitor.Flatten;

public class TooManyActionsMonitorGroup extends GroupProcessor implements InstanceReportable
{
	protected InstrumentedSlice m_slice;
	
	protected int m_threshold;
	
	public TooManyActionsMonitorGroup(int threshold)
	{
		super(1, 1);
		m_threshold = threshold;
		TooManyActionsMonitor tmam = new TooManyActionsMonitor(threshold);
		InstrumentedFindPattern fp = new InstrumentedFindPattern(tmam);
		m_slice = new InstrumentedSlice(Tuple.getId, fp);
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
}
