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

package utn.frba.ps.conversorvoice.threads;

import utn.frba.ps.conversorvoice.FrameType;
import utn.frba.ps.conversorvoice.Preferences;
import utn.frba.ps.conversorvoice.Utils;
import utn.frba.ps.conversorvoice.dsp.processors.RobotizeProcessor;
import utn.frba.ps.conversorvoice.dsp.stft.StftPostprocessor;
import utn.frba.ps.conversorvoice.dsp.stft.StftPreprocessor;
import utn.frba.ps.conversorvoice.io.AudioDevice;
import android.content.Context;

public class RobotizeThread extends AudioThread
{
	private final float[]		buffer;

	private StftPreprocessor	preprocessor	= null;
	private StftPostprocessor	postprocessor	= null;

	public RobotizeThread(Context context, AudioDevice input, AudioDevice output)
	{
		super(context, input, output);

		Preferences preferences = new Preferences(context);

		FrameType frameType = FrameType.Medium;
		int frameSize = preferences.getFrameSize(
			frameType, input.getSampleRate());
		int hopSize = preferences.getHopSize(
			frameType, input.getSampleRate());

		buffer = new float[frameSize];
		new Utils(context).log("Robotize frame size is %s.", buffer.length);

		preprocessor = new StftPreprocessor(input, frameSize, hopSize, true);
		postprocessor = new StftPostprocessor(output, frameSize, hopSize, true);
	}

	@Override
	public void dispose()
	{
		super.dispose();
		disposeProcessors();
	}

	private void disposeProcessors()
	{
		if (preprocessor != null)
		{
			preprocessor.dispose();
			preprocessor = null;
		}

		if (postprocessor != null)
		{
			postprocessor.dispose();
			postprocessor = null;
		}
	}

	@Override
	protected void doProcessing()
	{
		while (!Thread.interrupted())
		{
			preprocessor.processFrame(buffer);
			RobotizeProcessor.processFrame(buffer);
			postprocessor.processFrame(buffer);
		}
	}
}
