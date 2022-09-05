package patternlab.monitor;

import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;

public class GroupMonitor extends Monitor
{
	protected final GroupProcessor m_group;
	
	public GroupMonitor(GroupProcessor g)
	{
		super(1, 1);
		m_group = g;
	}

	@Override
	public Object getState() throws UnsupportedOperationException
	{
		return m_group.getState();
	}

	@Override
	public List<Integer> getSequence()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GroupMonitor duplicate(boolean with_state)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
