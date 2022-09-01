package patternlab.pattern.toomanyactions;

import ca.uqac.lif.cep.functions.UnaryFunction;

public class Tuple
{
	/**
	 * A public instance of the function {@link GetId}.
	 */
	public static final GetId getId = new GetId();
	
	/**
	 * A public instance of the function {@link GetPayload}.
	 */
	public static final GetPayload getPayload = new GetPayload();
	
	/**
	 * An ID identifying the slice this tuple belongs to.
	 */
	/*@ non_null @*/ protected final int m_id;
	
	/**
	 * The payload for this tuple.
	 */
	/*@ non_null @*/ protected final String m_payload;
	
	public Tuple(int id, String payload)
	{
		super();
		m_id = id;
		m_payload = payload;
	}
	
	@Override
	public int hashCode()
	{
		return m_id + m_payload.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Tuple))
		{
			return false;
		}
		Tuple t = (Tuple) o;
		return t.m_id == m_id && t.m_payload.compareTo(m_payload) == 0;
	}
	
	@Override
	public String toString()
	{
		return "(" + m_id + "," + m_payload + ")";
	}
	
	/**
	 * Function that extracts the ID of a tuple.
	 */
	protected static class GetId extends UnaryFunction<Tuple,Integer>
	{
		/**
		 * Creates a new instance of the function.
		 */
		public GetId()
		{
			super(Tuple.class, Integer.class);
		}

		@Override
		public Integer getValue(Tuple x)
		{
			return x.m_id;
		}
	}
	
	/**
	 * Function that extracts the payload of a tuple.
	 */
	protected static class GetPayload extends UnaryFunction<Tuple,String>
	{
		/**
		 * Creates a new instance of the function.
		 */
		public GetPayload()
		{
			super(Tuple.class, String.class);
		}

		@Override
		public String getValue(Tuple x)
		{
			return x.m_payload;
		}
	}
}
