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

package utn.frba.ps.conversorvoice.dsp.stft;

import static utn.frba.ps.conversorvoice.dsp.Math.max;
import static utn.frba.ps.conversorvoice.dsp.Math.min;
import utn.frba.ps.conversorvoice.Disposable;
import utn.frba.ps.conversorvoice.Preferences;
import utn.frba.ps.conversorvoice.dsp.KissFFT;
import utn.frba.ps.conversorvoice.dsp.Window;
import utn.frba.ps.conversorvoice.dsp.processors.AmplifyProcessor;
import utn.frba.ps.conversorvoice.dsp.processors.DenoiseProcessor;
import utn.frba.ps.conversorvoice.io.AudioDevice;

/**
 * Reads frames referred to "Constant Overlapp And Add" procedure from the audio
 * source. Frames get shifted by the given hop size, normalized, weighted,
 * optional denoised and transformed into frequency domain.
 * */
public final class StftPreprocessor implements Disposable
{
	private final AudioDevice		input;
	private final int				frameSize;
	private final int				hopSize;
	private final boolean			doForwardFFT;
	private final boolean			doDenoise;

	private final AmplifyProcessor	amplifier;
	//private final SilenceTester		silenceTester;

	private KissFFT					fft	= null;
	private final float[]			window;

	private final short[]			prevFrame, nextFrame;
	private int						frameCursor;

	public StftPreprocessor(AudioDevice input, int frameSize, int hopSize, boolean doForwardFFT, boolean doDenoise)
	{
		this.input = input;
		this.frameSize = frameSize;
		this.hopSize = hopSize;
		this.doForwardFFT = doForwardFFT;
		this.doDenoise = doDenoise;

		amplifier = new AmplifyProcessor(input.getContext());
		//silenceTester = new SilenceTester();

		fft = new KissFFT(frameSize);
		window = new Window(frameSize, true).hann();

		prevFrame = new short[frameSize];
		nextFrame = new short[frameSize];
		frameCursor = -1;
	}

	public StftPreprocessor(AudioDevice audioDevice, int frameSize, int hopSize, boolean doForwardFFT)
	{
		this(audioDevice, frameSize, hopSize, doForwardFFT,
			new Preferences(audioDevice.getContext()).isReduceNoise());
	}

	public void dispose()
	{
		if (fft != null)
		{
			fft.dispose();
			fft = null;
		}
	}

	public void processFrame(float[] frame)
	{
		// Handle the first frame
		if (frameCursor == -1)
		{
			frameCursor = frameSize;
			input.read(nextFrame);

			// Pre-preprocess frame
			// silenceTester.testFrame(nextFrame); // TEST
			amplifier.processFrame(nextFrame);
		}
		// Handle frame cursor
		else if (frameCursor >= frameSize)
		{
			// Reset frame cursor
			frameCursor -= frameSize;

			// Prepare frame buffers
			System.arraycopy(nextFrame, 0, prevFrame, 0, frameSize);

			// Read next frame
			input.read(nextFrame);

			// Pre-preprocess frame
			// silenceTester.testFrame(nextFrame); // TEST
			amplifier.processFrame(nextFrame);
		}

		// Prepare left frame part
		analyzeFrame(
			prevFrame, frameCursor,
			frame, 0,
			frameSize - frameCursor,
			window);

		// Prepare right frame part
		analyzeFrame(
			nextFrame, 0,
			frame, frameSize - frameCursor,
			frameCursor,
			window);

		if (doForwardFFT) fft.fft(frame);

		if (doForwardFFT && doDenoise) DenoiseProcessor.processFrame(frame);

		// Increment frame cursor
		frameCursor += hopSize;
	}

	/**
	 * Performs the analysis step with weighting.
	 * */
	private static void analyzeFrame(short[] src, int offsetSrc, float[] dst, int offsetDst, int count, float[] window)
	{
		if (count == 0) return;

		for (int i = 0; i < count; i++)
		{
			// Get and normalize source value
			float value = (float) src[i + offsetSrc] / 32767F;
			value = min(1F, max(-1F, value));

			// Multiply with window coefficient
			value *= window[i + offsetDst];

			// Copy it to destination array
			dst[i + offsetDst] = value;
		}
	}
}
