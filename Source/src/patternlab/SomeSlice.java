/*
  Experimentation of pattern detection by monitors
  Copyright (C) 2022 Sylvain Hallé

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package patternlab;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.provenance.ProvenanceTree;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

public class SomeSlice extends Slice implements Monitor
{
	/**
	 * A flag that determines if processor instances that are known to be
	 * non-matches should be removed.
	 */
	protected boolean m_removeNonMatches = true;

	/**
	 * A flag that determines if newly created processor instances that remain in
	 * their initial state should be removed.
	 */
	protected boolean m_removeImmobileOnStart = true;

	/**
	 * A flag that determines if events that are not part of the progressing
	 * subsequence of an instance should be removed.
	 */
	protected boolean m_removeNonProgressing = true;

	/**
	 * A flag that determines if only a single monitor instance in a given state
	 * should be kept at any moment.
	 */
	protected boolean m_removeSameState = true;

	protected int m_numInputs;

	public SomeSlice(Function f, Processor p)
	{
		super(f, p);
		m_numInputs = 0;
	}

	@Override
	public void setRemoveNonMatches(boolean b)
	{
		m_removeNonMatches = b;
		if (m_processor instanceof Monitor)
		{
			((Monitor) m_processor).setRemoveNonMatches(b);
		}
	}

	@Override
	public void setRemoveImmobileOnStart(boolean b)
	{
		m_removeImmobileOnStart = b;
		if (m_processor instanceof Monitor)
		{
			((Monitor) m_processor).setRemoveImmobileOnStart(b);
		}
	}

	@Override
	public void setRemoveNonProgressing(boolean b)
	{
		m_removeNonProgressing = b;
		if (m_processor instanceof Monitor)
		{
			((Monitor) m_processor).setRemoveNonProgressing(b);
		}
	}

	@Override
	public void setRemoveSameState(boolean b)
	{
		m_removeSameState = b;
		if (m_processor instanceof Monitor)
		{
			((Monitor) m_processor).setRemoveSameState(b);
		}
	}

	@Override
	public void reset()
	{
		super.reset();
		m_numInputs++;
	}

	@Override
	public SomeSlice duplicate(boolean with_state)
	{
		SomeSlice ss = new SomeSlice(m_slicingFunction.duplicate(), m_processor.duplicate());
		copyInto(ss, with_state);
		return ss;
	}

	protected void copyInto(SomeSlice ss, boolean with_state)
	{
		super.copyInto(ss, with_state);
		if (with_state)
		{
			ss.m_numInputs = m_numInputs;
			ss.setRemoveImmobileOnStart(m_removeImmobileOnStart);
			ss.setRemoveNonMatches(m_removeNonMatches);
			ss.setRemoveNonProgressing(m_removeNonProgressing);
			ss.setRemoveSameState(m_removeSameState);
		}
	}

	@Override
	protected boolean produceReturn(Queue<Object[]> outputs)
	{
		Troolean.Value verdict = Troolean.Value.INCONCLUSIVE;
		for (Map.Entry<Object,Object> entry : m_lastValues.entrySet())
		{
			Object v = entry.getValue();
			if (v instanceof Troolean.Value)
			{
				if ((Troolean.Value) v == Troolean.Value.TRUE)
				{
					verdict = Troolean.Value.TRUE;
					if (m_eventTracker != null)
					{
						Object slice_id = entry.getKey();
						List<Integer> indices = m_sliceIndices.get(slice_id);
						if (m_removeNonProgressing)
						{
							Processor p_slice = m_slices.get(slice_id);
							EventTracker in_tracker = p_slice.getEventTracker();
							ProvenanceNode root = in_tracker.getProvenanceTree(p_slice.getId(), 0, indices.size() - 1);
							List<Integer> in_indices = ProvenanceTree.getIndices(root);
							for (int i : in_indices)
							{
								m_eventTracker.associateToInput(getId(), 0, indices.get(i), 0, m_numInputs);
							}
						}
						else
						{
							for (int i : indices)
							{
								m_eventTracker.associateToInput(getId(), 0, i, 0, m_numInputs);
							}
						}
					}
				}
			}
		}
		m_numInputs++;
		outputs.add(new Object[] {verdict});
		return true;
	}

	@Override
	public int getInstances()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
