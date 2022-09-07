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

public class SomeSlice extends Slice
{
	protected int m_numInputs;
	
	public SomeSlice(Function f, Processor p)
	{
		super(f, p);
		m_numInputs = 0;
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
						Processor p_slice = m_slices.get(slice_id);
						EventTracker in_tracker = p_slice.getEventTracker();
						ProvenanceNode root = in_tracker.getProvenanceTree(p_slice.getId(), 0, indices.size() - 1);
						List<Integer> in_indices = ProvenanceTree.getIndices(root);
						for (int i : in_indices)
						{
							m_eventTracker.associateToInput(getId(), 0, indices.get(i), 0, m_numInputs);
						}
					}
				}
			}
		}
		m_numInputs++;
		outputs.add(new Object[] {verdict});
		return true;
	}
}
