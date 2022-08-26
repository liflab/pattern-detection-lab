package patternlab.pattern;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.synthia.random.RandomFloat;

public class BFollowsASourceTest
{
	public static void main(String[] args)
	{
		RandomFloat rf = new RandomFloat();
		InjectedPatternPicker<String> ipp = new InjectedPatternPicker<String>(new RandomAlphabet(rf, "a", "c", "d"), new BFollowsAPattern(), 1, 0.75f, rf);
		InjectedPatternSource<String> ips = new InjectedPatternSource<String>(ipp, 25);
		Pullable p = ips.getPullableOutput();
		while (p.hasNext())
		{
			System.out.print(p.pull() + " ");
		}
	}
}
