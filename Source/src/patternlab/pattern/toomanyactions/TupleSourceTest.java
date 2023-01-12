package patternlab.pattern.toomanyactions;

import java.util.Arrays;
import java.util.List;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.synthia.random.RandomBoolean;
import ca.uqac.lif.synthia.random.RandomFloat;
import ca.uqac.lif.synthia.random.RandomInteger;
import patternlab.pattern.InjectedPatternPicker;
import patternlab.pattern.InjectedPatternSource;
import patternlab.pattern.Tuple;

public class TupleSourceTest
{
	public static void main(String[] args)
	{
		int seed = 0;
		//List<String> total_payloads = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");
		List<String> total_payloads = Arrays.asList("a", "b", "c", "d");
		NormalActionsPattern nap = new NormalActionsPattern(total_payloads, new RandomInteger(1, 1000), seed);
		TooManyActionsPattern tmap = new TooManyActionsPattern(total_payloads, new RandomBoolean(0.2), new RandomInteger());
		InjectedPatternPicker<Tuple> ipp = new InjectedPatternPicker<Tuple>(nap, tmap, 2, 0.9f, new RandomFloat());
		InjectedPatternSource<Tuple> ips = new InjectedPatternSource<Tuple>(ipp, 100);
		Pullable p = ips.getPullableOutput();
		while (p.hasNext())
		{
			System.out.println(p.pull());
		}
	}
}
