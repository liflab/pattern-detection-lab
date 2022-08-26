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

import java.util.List;

/**
 * Interface implemented by objects that can be told that a list of events
 * has been generated. These objects may then elect to update their internal
 * state accordingly.
 *
 * @param <T> The type of the events
 */
public interface Notifiable<T>
{
	/**
	 * Notifies the object that a list of events has been generated.
	 * @param list The list of generated events
	 */
	public void notifyEvent(List<T> list);
}