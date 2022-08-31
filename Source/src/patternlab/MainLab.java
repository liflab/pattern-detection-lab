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

import ca.uqac.lif.dag.NodeConnector;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.plot.Plot;
import ca.uqac.lif.labpal.region.Point;
import ca.uqac.lif.labpal.region.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.labpal.table.TransformedTable;
import ca.uqac.lif.petitpoucet.function.Circuit;
import ca.uqac.lif.spreadsheet.chart.Chart.Axis;
import ca.uqac.lif.spreadsheet.chart.gnuplot.GnuplotScatterplot;
import ca.uqac.lif.spreadsheet.functions.ExpandAsColumns;
import ca.uqac.lif.spreadsheet.functions.Sort;
import ca.uqac.lif.synthia.random.RandomFloat;
import patternlab.pattern.BFollowsAMonitor;
import patternlab.pattern.BFollowsAPattern;
import patternlab.pattern.InjectedPatternPicker;
import patternlab.pattern.InjectedPatternSource;
import patternlab.pattern.RandomAlphabet;
import patternlab.pattern.TooManyActionsPattern;

import static ca.uqac.lif.labpal.region.ExtensionDomain.extension;
import static ca.uqac.lif.labpal.region.ProductRegion.product;
import static patternlab.PatternDetectionExperiment.P_ALGORITHM;
import static patternlab.PatternDetectionExperiment.P_ALPHA;
import static patternlab.PatternDetectionExperiment.P_DETECTED;
import static patternlab.PatternDetectionExperiment.P_MAX_INSTANCES;
import static patternlab.PatternDetectionExperiment.P_PATTERN;
import static patternlab.PatternDetectionExperiment.P_TIME;
import static patternlab.PatternDetectionExperiment.P_WITNESS_EVENTS;
import static patternlab.InstrumentedFindPattern.DIRECT;
import static patternlab.InstrumentedFindPattern.FIRST_STEP;
import static patternlab.InstrumentedFindPattern.PROGRESSING;
import static patternlab.InstrumentedFindPattern.DISTINCT_STATES;

public class MainLab extends Laboratory
{
	@Override
	public void setup()
	{
		PatternDetectionExperimentFactory factory = new PatternDetectionExperimentFactory(this, 10000);

		Region big_r = product(
				extension(P_ALGORITHM, DIRECT, FIRST_STEP, PROGRESSING, DISTINCT_STATES),
				extension(P_PATTERN, BFollowsAPattern.NAME, TooManyActionsPattern.NAME),
				extension(P_ALPHA, 0.999f, 0.99f, 0.9f, 0.75f, 0.5f));

		// For fixed alpha
		for (Region r : big_r.all(P_ALPHA))
		{
			float alpha = (Float) r.asPoint().get(P_ALPHA);
			{
				ExperimentTable et_witnesses = new ExperimentTable(P_PATTERN, P_ALGORITHM, P_WITNESS_EVENTS);
				et_witnesses.add(factory.get(r));
				TransformedTable tt_witnesses = new TransformedTable(new ExpandAsColumns(P_ALGORITHM, P_WITNESS_EVENTS), et_witnesses);
				tt_witnesses.setTitle("Number of witness events for each algorithm, \u03b1 = " + alpha);
				add(tt_witnesses);
			}
			{
				ExperimentTable et_witnesses = new ExperimentTable(P_PATTERN, P_ALGORITHM, P_TIME);
				et_witnesses.add(factory.get(r));
				TransformedTable tt_witnesses = new TransformedTable(new ExpandAsColumns(P_ALGORITHM, P_TIME), et_witnesses);
				tt_witnesses.setTitle("Running time for each algorithm, \u03b1 = " + alpha);
				add(tt_witnesses);
			}
			{
				ExperimentTable et_witnesses = new ExperimentTable(P_PATTERN, P_ALGORITHM, P_DETECTED);
				et_witnesses.add(factory.get(r));
				TransformedTable tt_witnesses = new TransformedTable(new ExpandAsColumns(P_ALGORITHM, P_DETECTED), et_witnesses);
				tt_witnesses.setTitle("Number of matches for each algorithm, \u03b1 = " + alpha);
				add(tt_witnesses);
			}
			{
				ExperimentTable et_witnesses = new ExperimentTable(P_PATTERN, P_ALGORITHM, P_MAX_INSTANCES);
				et_witnesses.add(factory.get(r));
				TransformedTable tt_witnesses = new TransformedTable(new ExpandAsColumns(P_ALGORITHM, P_MAX_INSTANCES), et_witnesses);
				tt_witnesses.setTitle("Maximum number of monitor instances for each algorithm, \u03b1 = " + alpha);
				add(tt_witnesses);
			}
		}

		// For fixed pattern
		for (Region r : big_r.all(P_PATTERN))
		{
			String pattern = r.asPoint().getString(P_PATTERN);
			{
				ExperimentTable et_witnesses = new ExperimentTable(P_ALPHA, P_ALGORITHM, P_WITNESS_EVENTS);
				et_witnesses.add(factory.get(r));
				Circuit g = new Circuit(1, 1);
				{
					ExpandAsColumns e = new ExpandAsColumns(P_ALGORITHM, P_WITNESS_EVENTS);
					Sort s = new Sort().by(0).excludeFirst();
					NodeConnector.connect(e, 0, s, 0);
					g.addNodes(e, s);
					g.associateInput(0, e.getInputPin(0));
					g.associateOutput(0, s.getOutputPin(0));
				}
				TransformedTable tt_witnesses = new TransformedTable(g, et_witnesses);
				tt_witnesses.setTitle("Impact of pattern density on witnesss, pattern " + pattern);
				add(tt_witnesses);
				Plot p = new Plot(tt_witnesses, new GnuplotScatterplot().setCaption(Axis.X, "\u03b1").setCaption(Axis.Y, "Witness events/instance").setLogscale(Axis.Y));
				add(p);
			}
			{
				ExperimentTable et_witnesses = new ExperimentTable(P_ALPHA, P_ALGORITHM, P_TIME);
				et_witnesses.add(factory.get(r));
				Circuit g = new Circuit(1, 1);
				{
					ExpandAsColumns e = new ExpandAsColumns(P_ALGORITHM, P_TIME);
					Sort s = new Sort().by(0).excludeFirst();
					NodeConnector.connect(e, 0, s, 0);
					g.addNodes(e, s);
					g.associateInput(0, e.getInputPin(0));
					g.associateOutput(0, s.getOutputPin(0));
				}
				TransformedTable tt_witnesses = new TransformedTable(g, et_witnesses);
				tt_witnesses.setTitle("Impact of pattern density on running time, pattern " + pattern);
				add(tt_witnesses);
				Plot p = new Plot(tt_witnesses, new GnuplotScatterplot().setCaption(Axis.X, "\u03b1").setCaption(Axis.Y, "Running time (ms)").setLogscale(Axis.Y));
				add(p);
			}
			{
				ExperimentTable et_witnesses = new ExperimentTable(P_ALPHA, P_ALGORITHM, P_MAX_INSTANCES);
				et_witnesses.add(factory.get(r));
				Circuit g = new Circuit(1, 1);
				{
					ExpandAsColumns e = new ExpandAsColumns(P_ALGORITHM, P_MAX_INSTANCES);
					Sort s = new Sort().by(0).excludeFirst();
					NodeConnector.connect(e, 0, s, 0);
					g.addNodes(e, s);
					g.associateInput(0, e.getInputPin(0));
					g.associateOutput(0, s.getOutputPin(0));
				}
				TransformedTable tt_witnesses = new TransformedTable(g, et_witnesses);
				tt_witnesses.setTitle("Impact of pattern density on maximum number of monitor instances, pattern " + pattern);
				add(tt_witnesses);
				Plot p = new Plot(tt_witnesses, new GnuplotScatterplot().setCaption(Axis.X, "\u03b1").setCaption(Axis.Y, "Max instances").setLogscale(Axis.Y));
				add(p);
			}
		}
	}

	public static void main(String[] args)
	{
		initialize(args, MainLab.class);
	}
}
