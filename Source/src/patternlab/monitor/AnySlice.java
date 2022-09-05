package patternlab.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.tmf.Slice;
import patternlab.InstanceReportable;
import patternlab.monitor.FindOccurrences.PatternInstance;

public class AnySlice extends Slice implements InstanceReportable
{
	public AnySlice(Function f, Processor p)
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
	
	@SuppressWarnings("unchecked")
	@Override
  protected boolean produceReturn(Queue<Object[]> outputs)
  {
		Map<Object,Object> out_map = new HashMap<Object,Object>(m_lastValues.size());
		for (Map.Entry<Object,Object> e : m_lastValues.entrySet())
		{
			List<PatternInstance> list = (List<PatternInstance>) e.getValue();
			List<PatternInstance> out_list = new ArrayList<PatternInstance>(list.size());
			for (PatternInstance pi : list)
			{
				PatternInstance pi_shifted = shiftIndices(e.getKey(), pi);
				out_list.add(pi_shifted);
			}
			out_map.put(e.getKey(), out_list);
		}
    outputs.add(new Object[] {out_map});
    m_outputCount++;
    return true;
  }
	
	protected PatternInstance shiftIndices(Object key, PatternInstance pi)
	{
		return pi;
	}
}
