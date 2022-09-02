package patternlab.pattern.combined;

import java.util.Arrays;

import ca.uqac.lif.synthia.Picker;
import patternlab.pattern.CartesianProductPattern;
import patternlab.pattern.SequencePattern;

public class CombinedPattern extends CartesianProductPattern<String>
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = "Combined patterns";
	
	public CombinedPattern(Picker<Float> float_source)
	{
		super(float_source, getPatterns());
	}

	protected static Picker<?>[] getPatterns()
	{
		return new Picker<?>[] {new SequencePattern<String>(Arrays.asList("A", "B")),
			new SequencePattern<String>(Arrays.asList("C", "D")),
			new SequencePattern<String>(Arrays.asList("E", "F"))};
	}
}
