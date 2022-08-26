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

import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.synthia.random.RandomFloat;
import patternlab.monitor.BFollowsAMonitor;
import patternlab.pattern.BFollowsAPattern;
import patternlab.pattern.InjectedPatternPicker;
import patternlab.pattern.InjectedPatternSource;
import patternlab.pattern.RandomAlphabet;

public class MainLab extends Laboratory
{
	@Override
	public void setup()
	{
		RandomFloat rf = new RandomFloat().setSeed(0);
		InjectedPatternPicker<String> ipp = new InjectedPatternPicker<String>(new RandomAlphabet(rf, "a", "c", "d"), new BFollowsAPattern(), 1, 0.75f, rf);
		InjectedPatternSource<String> ips = new InjectedPatternSource<String>(ipp, 100);
		//QueueSource ips = new QueueSource().setEvents("a", "b").loop(false);
		
		InstrumentedFindPattern ifp = new InstrumentedFindPattern(new BFollowsAMonitor());
		ifp.setRemoveNonProgressing(false);
		ifp.setRemoveImmobileOnStart(false);
		PatternDetectionExperiment e = new PatternDetectionExperiment(ips, ifp);
		add(e);
	}
	
	public static void main(String[] args)
	{
		initialize(args, MainLab.class);
	}
}
