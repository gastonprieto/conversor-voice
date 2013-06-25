/*******************************************************************************

 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package utn.frba.ps.conversorvoice.dsp.processors;

import utn.frba.ps.conversorvoice.Preferences;
import utn.frba.ps.conversorvoice.dsp.Math;
import android.content.Context;

public class AmplifyProcessor
{
	private final float	ampFactor;

	public AmplifyProcessor(Context context)
	{
		this(new Preferences(context).getSoundAmplification());
	}

	/**
	 * @param ampDecibel
	 *            Amplification level in [dB].
	 * */
	public AmplifyProcessor(int ampDecibel)
	{
		// http://www.sengpielaudio.com/Rechner-pegelaenderung.htm
		ampFactor = Math.pow(10F, ampDecibel / 20F);
	}

	public void processFrame(short[] frame)
	{
		if (ampFactor == 1) return;

		for (int i = 0; i < frame.length; i++)
		{
			float result = frame[i] * ampFactor;

			if (result > 32767F)
			{
				frame[i] = 32767;
			}
			else if (result < -32768F)
			{
				frame[i] = -32768;
			}
			else
			{
				frame[i] = (short) result;
			}
		}
	}
}
