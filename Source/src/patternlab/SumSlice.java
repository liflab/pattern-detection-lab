/*
  Experimentation of pattern detection by monitors
  Copyright (C) 2022-2023 Sylvain Hallé

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
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Stateful;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.provenance.ProvenanceTree;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

public class SumSlice extends MonitorSlice implements Stateful
{
	public SumSlice(Function f, Processor p)
	{
		super(f, p);
		m_cleaningFunction = new RemoveNullValues();
	}
	
	@Override
	public SumSlice duplicate(boolean with_state)
	{
		SumSlice ss = new SumSlice(m_slicingFunction.duplicate(), m_processor.duplicate());
		copyInto(ss, with_state);
		return ss;
	}
	
	@Override
	protected boolean produceReturn(Queue<Object[]> outputs)
	{
		int value = 0;
		for (Map.Entry<Object,Object> entry : m_lastValues.entrySet())
		{
			Object v = entry.getValue();
			if (v instanceof Number)
			{
				int v_n = ((Number) v).intValue();
				if (v_n > 0 || !m_removeNonMatches)
				{
					value += v_n;
					if (m_eventTracker != null)
					{
						Object slice_id = entry.getKey();
						List<Integer> indices = m_sliceIndices.get(slice_id);
						List<Integer> in_indices;
						if (m_removeNonProgressing)
						{
							Processor p_slice = m_slices.get(slice_id);
							if (p_slice instanceof GroupProcessor)
							{
								GroupProcessor gp_slice = (GroupProcessor) p_slice;
								EventTracker in_tracker = gp_slice.getInnerTracker();
								int p_id = gp_slice.getAssociatedOutput(0).getId();
								ProvenanceNode root = in_tracker.getProvenanceTree(p_id, 0, indices.size() - 1);
								in_indices = ProvenanceTree.getIndices(root);
							}
							else
							{
								EventTracker in_tracker = p_slice.getEventTracker();
								ProvenanceNode root = in_tracker.getProvenanceTree(p_slice.getId(), 0, indices.size() - 1);
								in_indices = ProvenanceTree.getIndices(root);
							}
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
		outputs.add(new Object[] {value});
		return true;
	}
	
	protected class RemoveNullValues extends UnaryFunction<Object,Boolean>
	{
		public RemoveNullValues()
		{
			super(Object.class, Boolean.class);
		}

		@Override
		public Boolean getValue(Object x)
		{
			if (x instanceof Number && ((Number) x).intValue() == 0 && m_removeNonMatches)
			{
				return true;
			}
			return false;
		}
	}

}
