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

package utn.frba.ps.conversorvoice.io;

import static utn.frba.ps.conversorvoice.dsp.Math.PI;
import static utn.frba.ps.conversorvoice.dsp.Math.cos;
import android.content.Context;

public final class CosineWaveOscillator extends AudioDevice
{
	/**
	 * Sample cursor.
	 * */
	private int	sample	= 0;

	public CosineWaveOscillator(Context context, int frequency)
	{
		super(context);

		this.setFrequency(frequency);
	}

	public CosineWaveOscillator(Context context, int sampleRate, int frequency)
	{
		super(context, sampleRate);

		this.setFrequency(frequency);
	}

	private int	frequency;

	public int getFrequency()
	{
		return frequency;
	}

	private void setFrequency(int frequency)
	{
		this.frequency = frequency;
	}

	public int read(float[] buffer, int offset, int count)
	{
		final int sampleRate = getSampleRate();
		final int samplesPerPeriod = getSampleRate() / getFrequency();
		final float omega = 2 * PI * getFrequency();

		for (int i = 0; i < count; i++)
		{
			float t = (float) sample / sampleRate;
			float value = cos(omega * t);

			buffer[i + offset] = value;

			sample++;
			if (sample >= samplesPerPeriod) sample = 0;
		}

		return count;
	}

	@Override
	public int read(short[] buffer, int offset, int count)
	{
		final int sampleRate = getSampleRate();
		final int samplesPerPeriod = getSampleRate() / getFrequency();
		final float omega = 2 * PI * getFrequency();
		final float amp = 32767F / 3; // TODO: Cosine amplitude getter/setter.

		for (int i = 0; i < count; i++)
		{
			float t = (float) sample / sampleRate;
			short value = (short) (amp * cos(omega * t));

			buffer[i + offset] = value;

			sample++;
			if (sample >= samplesPerPeriod) sample = 0;
		}

		return count;
	}

	@Override
	public void flush()
	{
		sample = 0;
	}
}
