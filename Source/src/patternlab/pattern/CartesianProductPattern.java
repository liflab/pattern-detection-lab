package patternlab.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.uqac.lif.synthia.NoMoreElementException;
import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.random.RandomFloat;

public class CartesianProductPattern<T> implements Picker<List<T>>, Notifiable<T>
{
	protected final List<Picker<List<T>>> m_patterns;
	
	protected final List<Picker<List<T>>> m_availablePatterns;
	
	protected final Picker<Float> m_floatSource;
	
	protected static final int s_maxTries = 100;
	
	@SuppressWarnings("unchecked")
	public CartesianProductPattern(Picker<Float> float_source, Picker<?> ... patterns)
	{
		super();
		m_floatSource = float_source;
		m_patterns = new ArrayList<Picker<List<T>>>(patterns.length);
		m_availablePatterns = new ArrayList<Picker<List<T>>>(patterns.length);
		for (Picker<?> p : patterns)
		{
			m_patterns.add((Picker<List<T>>) p);
			m_availablePatterns.add((Picker<List<T>>) p);
		}
	}
	
	protected CartesianProductPattern(Picker<Float> float_source, List<Picker<List<T>>> patterns, List<Picker<List<T>>> available_patterns)
	{
		super();
		m_floatSource = float_source;
		m_patterns = patterns;
		m_availablePatterns = available_patterns;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyEvent(List<T> list)
	{
		for (Picker<List<T>> p : m_patterns)
		{
			if (p instanceof Notifiable)
			{
				((Notifiable<T>) p).notifyEvent(list);
			}
		}
	}

	@Override
	public Picker<List<T>> duplicate(boolean with_state)
	{
		List<Picker<List<T>>> patterns_dup = new ArrayList<Picker<List<T>>>(m_patterns.size());
		List<Picker<List<T>>> patterns_available = new ArrayList<Picker<List<T>>>(m_patterns.size());
		for (Picker<List<T>> p : m_patterns)
		{
			Picker<List<T>> dup = p.duplicate(with_state);
			patterns_dup.add(dup);
			if (m_availablePatterns.contains(p))
			{
				patterns_available.add(dup);
			}
		}
		CartesianProductPattern<T> cpp = new CartesianProductPattern<T>(m_floatSource.duplicate(with_state), patterns_dup, patterns_available);
		return cpp;
	}

	@Override
	public List<T> pick()
	{
		for (int tries = 0; tries < s_maxTries; tries++)
		{
			if (m_availablePatterns.isEmpty())
			{
				throw new NoMoreElementException();
			}
			int index = (int) (m_floatSource.pick() * (float) m_availablePatterns.size());
			try
			{
				Picker<List<T>> choice = m_availablePatterns.get(index);
				List<T> list = choice.pick();
				return list;
			}
			catch (NoMoreElementException e)
			{
				m_availablePatterns.remove(index);
				
			}
			catch (IndexOutOfBoundsException e)
			{
				System.out.println("FOO");
			}
		}
		throw new NoMoreElementException();
	}

	@Override
	public void reset()
	{
		for (Picker<List<T>> p : m_patterns)
		{
			p.reset();
		}
		m_availablePatterns.clear();
		m_availablePatterns.addAll(m_patterns);
		m_floatSource.reset();
	}
	
	public static void main(String[] args)
	{
		RandomFloat rf = new RandomFloat().setSeed(10);
		SequencePattern<String> seq1 = new SequencePattern<String>(Arrays.asList("a", "b"));
		SequencePattern<String> seq2 = new SequencePattern<String>(Arrays.asList("c", "d"));
		SequencePattern<String> seq3 = new SequencePattern<String>(Arrays.asList("e", "f"));
		CartesianProductPattern<String> cpp = new CartesianProductPattern<String>(rf, seq1, seq2, seq3);
		for (int i = 0; i < 7; i++)
		{
			System.out.println(cpp.pick());
		}
	}
}
