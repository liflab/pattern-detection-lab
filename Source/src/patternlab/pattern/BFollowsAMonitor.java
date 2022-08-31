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
package patternlab.pattern;

import java.util.Arrays;

/**
 * A simple monitor used for testing the lab. It checks that an event "a" is
 * eventually followed by a "b".
 */
public class BFollowsAMonitor extends SequenceMonitor<String>
{
	/**
	 * The name of this pattern.
	 */
	public static final String NAME = "b follows a";
		
	/**
	 * Creates a new instance of the monitor.
	 */
	public BFollowsAMonitor()
	{
		super(Arrays.asList("a", "b"));
	}
	
	@Override
	public BFollowsAMonitor duplicate(boolean with_state)
	{
		BFollowsAMonitor bfa = new BFollowsAMonitor();
		copyInto(bfa, with_state);
		return bfa;
	}
}
