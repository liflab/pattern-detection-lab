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

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.ltl.TrooleanCast;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Sets.MathSet;

public class FindOccurrencesTest
{
	@SuppressWarnings("unchecked")
	@Test
	public void test1()
	{
		
		GroupProcessor ba = new GroupProcessor(1, 1);
		{
			IndexEventTracker et = new IndexEventTracker();
			Fork f = new Fork();
			ApplyFunction is_a = new ApplyFunction(new FunctionTree(TrooleanCast.instance, new FunctionTree(Equals.instance, StreamVariable.X, new Constant("a"))));
			ApplyFunction is_b = new ApplyFunction(new FunctionTree(TrooleanCast.instance, new FunctionTree(Equals.instance, StreamVariable.X, new Constant("b"))));
			Connector.connect(et, f, 0, is_a, 0);
			Connector.connect(et, f, 1, is_b, 0);
			Sequence seq = new Sequence(2);
			Connector.connect(et, is_a, 0, seq, 0);
			Connector.connect(et, is_b, 0, seq, 1);
			ba.addProcessors(f, is_a, is_b, seq);
			ba.associateInput(0, f, 0);
			ba.associateOutput(0, seq, 0);
			ba.setEventTracker(et);
		}
		/*{
			Pushable p = ba.getPushableInput();
			SinkLast sink = new SinkLast();
			Connector.connect(ba, sink);
			p.push("a");
			p.push("b");
			Object o = sink.getLast()[0];
		}*/
		FindOccurrences fo = new FindOccurrences(ba);
		Pushable p = fo.getPushableInput();
		SinkLast sink = new SinkLast();
		MathSet<MathSet<Integer>> matches;
		Connector.connect(fo, sink);
		p.push("a");
		matches = (MathSet<MathSet<Integer>>) sink.getLast()[0];
		assertEquals(0, matches.size());
		p.push("b");
		matches = (MathSet<MathSet<Integer>>) sink.getLast()[0];
		assertEquals(1, matches.size());
	}
}
