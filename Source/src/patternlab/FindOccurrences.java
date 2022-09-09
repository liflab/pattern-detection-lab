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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.provenance.ProvenanceTree;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.cep.util.Sets.MathSet;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

public class FindOccurrences extends UniformProcessor
{
	/**
	 * A string indicating that the processor only keeps monitor instances
	 * in distinct states.
	 */
	public static final String DISTINCT_STATES = "Distinct states";
	
	/**
	 * A string indicating that the processor only keeps progressing
	 * subsequences.
	 */
	public static final String PROGRESSING = "Progressing";
	
	/**
	 * A string indicating that the processor only keeps monitor instances that
	 * move out of their initial state on the first event consumed.
	 */
	public static final String FIRST_STEP = "First step";
	
	/**
	 * A string indicating that all monitor instances are kept until they produce
	 * a definite verdict.
	 */
	public static final String DIRECT = "Direct";
	
	/*@ non_null @*/ protected final Processor m_monitor;

	/*@ non_null @*/ protected final Set<ProcessorSlice> m_slices;

	public FindOccurrences(Processor monitor)
	{
		super(1, 1);
		m_monitor = monitor;
		m_slices = new HashSet<ProcessorSlice>();
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		ProcessorSlice new_slice = new ProcessorSlice(m_inputCount, m_monitor.duplicate());
		m_slices.add(new_slice);
		Object event = inputs[0];
		Iterator<ProcessorSlice> it = m_slices.iterator();
		List<ProcessorSlice> matching_slices = new ArrayList<ProcessorSlice>();
		while (it.hasNext())
		{
			ProcessorSlice slice = it.next();
			Troolean.Value verdict = slice.push(event);
			if (verdict == Troolean.Value.TRUE)
			{
				matching_slices.add(slice);
				it.remove();
			}
			else if (verdict == Troolean.Value.FALSE)
			{
				it.remove();
			}
		}
		Collections.sort(matching_slices);
		MathSet<MathSet<Integer>> all_matches = new MathSet<MathSet<Integer>>();
		for (ProcessorSlice slice : matching_slices)
		{
			MathSet<Integer> indices = slice.getIndices();
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

		public ProcessorSlice(int offset, Processor p)
		{
			super();
			m_processor = p;
			m_sink = new SinkLast();
			Connector.connect(p, m_sink);
			m_pushable = p.getPushableInput();
			m_offset = offset;
			m_numPushes = 0;
		}
		
		public Troolean.Value push(Object event)
		{
			m_pushable.push(event);
			m_numPushes++;
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
}
