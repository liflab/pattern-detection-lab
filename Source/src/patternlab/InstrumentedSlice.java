package patternlab;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.tmf.Slice;

public class InstrumentedSlice extends Slice implements InstanceReportable
{
	public InstrumentedSlice(Function f, Processor p)
	{
		super(f, p);
	}
	
	@Override
	public void setRemoveNonMatches(boolean b)
	{
		((InstanceReportable) m_processor).setRemoveNonMatches(b);
	}
	
	@Override
	public void setRemoveImmobileOnStart(boolean b)
	{
		((InstanceReportable) m_processor).setRemoveImmobileOnStart(b);
	}
	
	@Override
	public void setRemoveNonProgressing(boolean b)
	{
		((InstanceReportable) m_processor).setRemoveNonProgressing(b);
	}
	
	@Override
	public void setRemoveSameState(boolean b)
	{
		((InstanceReportable) m_processor).setRemoveSameState(b);
	}
	
	@Override
	public int getInstances()
	{
		int total = 0;
		for (Processor p : m_slices.values())
		{
			if (p instanceof InstanceReportable)
			{
				total += ((InstanceReportable) p).getInstances();
			}
		}
		return total;
	}
}
