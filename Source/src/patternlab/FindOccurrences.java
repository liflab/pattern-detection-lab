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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.Stateful;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.provenance.ProvenanceTree;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Sets.MathSet;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

public class FindOccurrences extends UniformProcessor implements Monitor
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
	
	/*@ non_null @*/ protected final Processor m_monitor;

	/*@ non_null @*/ protected final List<ProcessorSlice> m_slices;
	
	@Override
	/*@ pure @*/ public int getInstances()
	{
		return m_slices.size();
	}
	
	@Override
	public void setRemoveNonMatches(boolean b)
	{
		m_removeNonMatches = b;
	}
	
	@Override
	public void setRemoveImmobileOnStart(boolean b)
	{
		m_removeImmobileOnStart = b;
	}
	
	@Override
	public void setRemoveNonProgressing(boolean b)
	{
		m_removeNonProgressing = b;
	}
	
	@Override
	public void setRemoveSameState(boolean b)
	{
		m_removeSameState = b;
	}

	public FindOccurrences(Processor monitor)
	{
		super(1, 1);
		m_monitor = monitor;
		m_slices = new ArrayList<ProcessorSlice>();
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		ProcessorSlice new_slice = new ProcessorSlice(m_inputCount, m_monitor.duplicate().setEventTracker(new IndexEventTracker()));
		m_slices.add(new_slice);
		Object event = inputs[0];
		ListIterator<ProcessorSlice> it = m_slices.listIterator(m_slices.size());
		List<ProcessorSlice> matching_slices = new ArrayList<ProcessorSlice>();
		Set<Object> seen_states = new HashSet<Object>();
		boolean last = true;
		while (it.hasPrevious())
		{
			ProcessorSlice slice = it.previous();
			Object s_before = null, s_after = null;
			if (m_removeImmobileOnStart && last)
			{
				s_before = slice.getState();
				last = false;
			}
			Troolean.Value verdict = slice.push(event);
			if (m_removeImmobileOnStart || m_removeSameState)
			{
				s_after = slice.getState();
				if (m_removeImmobileOnStart && Equals.isEqualTo(s_before, s_after))
				{
					it.remove();
					continue;
				}
			}
			if (verdict == Troolean.Value.TRUE)
			{
				matching_slices.add(slice);
				it.remove();
			}
			else if (verdict == Troolean.Value.FALSE)
			{
				if (m_removeNonMatches)
				{
					it.remove();
				}
			}
			if (m_removeSameState)
			{
				if (seen_states.contains(s_after))
				{
					it.remove();
				}
				else
				{
					seen_states.add(s_after);
				}
			}
		}
		Collections.sort(matching_slices);
		MathSet<MathSet<Integer>> all_matches = new MathSet<MathSet<Integer>>();
		for (ProcessorSlice slice : matching_slices)
		{
			MathSet<Integer> indices = slice.getIndices();
			if (!m_removeNonProgressing)
			{
				addAllIndices(indices);
			}
			if (!indices.isEmpty())
			{
				all_matches.add(indices);
			}
		}
		m_inputCount++;
		outputs[0] = all_matches;
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		// TODO Auto-generated method stub
		return null;
	}

	protected class ProcessorSlice implements Comparable<ProcessorSlice>
	{
		protected final Processor m_processor;

		protected final SinkLast m_sink;

		protected final Pushable m_pushable;

		protected final int m_offset;
		
		protected int m_numPushes;
		
		protected boolean m_pushed;
		
		protected Object m_lastState;

		public ProcessorSlice(int offset, Processor p)
		{
			super();
			m_processor = p;
			m_sink = new SinkLast();
			Connector.connect(p, m_sink);
			m_pushable = p.getPushableInput();
			m_offset = offset;
			m_numPushes = 0;
			m_pushed = true;
			m_lastState = null;
		}
		
		public Troolean.Value push(Object event)
		{
			m_pushable.push(event);
			m_numPushes++;
			m_pushed = true;
			return (Troolean.Value) m_sink.getLast()[0];
		}
		
		public MathSet<Integer> getIndices()
		{
			MathSet<Integer> indices = new MathSet<Integer>();
			EventTracker tracker = m_processor.getEventTracker();
			if (tracker == null)
			{
				return indices;
			}
			ProvenanceNode root = tracker.getProvenanceTree(m_processor.getId(), 0, m_numPushes - 1);
			List<Integer> stream_indices = ProvenanceTree.getIndices(root);
			for (int index : stream_indices)
			{
				indices.add(index + m_offset);
			}
			return indices;
		}
		
		public Object getState()
		{
			if (m_pushed && m_processor instanceof Stateful)
			{
				m_pushed = false;
				m_lastState = ((Stateful) m_processor).getState();
			}
			return m_lastState;
		}

		@Override
		public int compareTo(ProcessorSlice ps)
		{
			return m_offset - ps.m_offset;
		}

		@Override
		public int hashCode()
		{
			return m_offset;
		}

		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof ProcessorSlice))
			{
				return false;
			}
			return m_offset == ((ProcessorSlice) o).m_offset;
		}
	}
	
	/**
	 * In a given set of integers, adds all the values contained between the
	 * minimum and maximum.
	 * @param set The set
	 */
	protected static void addAllIndices(MathSet<Integer> set)
	{
		int min = -1, max = 0;
		for (int i : set)
		{
			if (i > max)
			{
				max = i;
			}
			if (min < 0 || i < min)
			{
				min = i;
			}
		}
		for (int i = min; i <= max; i++)
		{
			set.add(i);
		}
	}
}
