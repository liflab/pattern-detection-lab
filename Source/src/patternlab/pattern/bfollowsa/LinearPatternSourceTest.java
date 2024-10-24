package patternlab.pattern.bfollowsa;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.synthia.random.RandomFloat;
import patternlab.pattern.InjectedPatternPicker;
import patternlab.pattern.InjectedPatternSource;
import patternlab.pattern.RandomAlphabet;

public class LinearPatternSourceTest
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args)
	{
		RandomFloat rf = new RandomFloat();
		InjectedPatternPicker<String> ipp = new InjectedPatternPicker<String>(new RandomAlphabet(rf, "a", "c", "d"), new LinearPattern(RandomAlphabet.getUppercaseSequence(0, 2)), 1, 0.75f, rf);
		InjectedPatternSource<String> ips = new InjectedPatternSource<String>(ipp, 25);
		Pullable p = ips.getPullableOutput();
		while (p.hasNext())
		{
			System.out.print(p.pull() + " ");
		}
	}
}
