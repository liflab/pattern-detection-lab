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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.fsm.FunctionTransition;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.fsm.TransitionOtherwise;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.provenance.EventFunction.InputValue;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

public class SomeSliceTest
{
	@Test
	public void test1()
	{
		IndexEventTracker tracker = new IndexEventTracker();
		MooreMachine mm = new MooreMachine(1, 1);
		mm.addTransition(0, new FunctionTransition(
				new FunctionTree(Equals.instance, new Constant(1), new FunctionTree(new NthElement(1), StreamVariable.X)), 1));
		mm.addTransition(0, new TransitionOtherwise(0));
		mm.addTransition(1, new FunctionTransition(
				new FunctionTree(Equals.instance, new Constant(2), new FunctionTree(new NthElement(1), StreamVariable.X)), 2));
		mm.addTransition(1, new TransitionOtherwise(1));
		mm.addTransition(2, new TransitionOtherwise(2));
		mm.addSymbol(0, new Constant(Troolean.Value.INCONCLUSIVE));
		mm.addSymbol(1, new Constant(Troolean.Value.INCONCLUSIVE));
		mm.addSymbol(2, new Constant(Troolean.Value.TRUE));
		SomeSlice ss = new SomeSlice(new NthElement(0), mm);
		ss.setEventTracker(tracker);
		SinkLast sink = new SinkLast();
		Connector.connect(ss, sink);
		Pushable p = ss.getPushableInput();
		p.push(Arrays.asList(0, 0));
		assertEquals(Troolean.Value.INCONCLUSIVE, sink.getLast()[0]);
		p.push(Arrays.asList(0, 1));
		assertEquals(Troolean.Value.INCONCLUSIVE, sink.getLast()[0]);
		p.push(Arrays.asList(1, 1));
		assertEquals(Troolean.Value.INCONCLUSIVE, sink.getLast()[0]);
		p.push(Arrays.asList(0, 2));
		assertEquals(Troolean.Value.TRUE, sink.getLast()[0]);
		ProvenanceNode root = tracker.getProvenanceTree(ss, 0, 3);
		List<ProvenanceNode> parents = root.getParents();
		assertEquals(2, parents.size());
		isDataPoint((InputValue) parents.get(0).getNodeFunction(), 0, 1);
		isDataPoint((InputValue) parents.get(1).getNodeFunction(), 0, 3);
	}
	
	public static void isDataPoint(InputValue iv, int stream_index, int stream_pos)
	{
		assertEquals(stream_index, iv.getStreamIndex());
		assertEquals(stream_pos, iv.getStreamPosition());
	}
}
