package patternlab.pattern.combined;

import ca.uqac.lif.synthia.Picker;
import patternlab.pattern.CartesianProductPattern;
import patternlab.pattern.RandomAlphabet;
import patternlab.pattern.SequencePattern;

public class CombinedPattern extends CartesianProductPattern<String>
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = "Combined patterns";
	
	/**
	 * The name of the parameter "Number of elements".
	 */
	public static final String P_NUM_PATTERNS = "Number of patterns";

	protected final int m_numPatterns;
	
	public CombinedPattern(Picker<Float> float_source, int num_patterns)
	{
		super(float_source, getPatterns(num_patterns));
		m_numPatterns = num_patterns;
	}

	protected static Picker<?>[] getPatterns(int num_patterns)
	{
		Picker<?>[] patterns = new Picker<?>[num_patterns];
		for (int i = 0; i < num_patterns; i++)
		{
			patterns[i] = new SequencePattern<String>(RandomAlphabet.getUppercaseSequence(i * 5, 5));
		}
		return patterns;
	}
}
