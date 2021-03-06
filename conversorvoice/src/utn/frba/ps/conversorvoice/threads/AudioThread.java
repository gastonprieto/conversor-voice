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

import utn.frba.ps.conversorvoice.AAF;
import utn.frba.ps.conversorvoice.Disposable;
import utn.frba.ps.conversorvoice.Utils;
import utn.frba.ps.conversorvoice.io.AudioDevice;
import android.content.Context;

public abstract class AudioThread implements Runnable, Disposable
{
	protected final Context	context;
	protected final AudioDevice	input, output;

	private Thread				thread	= null;

	public AudioThread(Context context, AudioDevice input, AudioDevice output)
	{
		this.context = context;
		this.input = input;
		this.output = output;
	}

	public void configure(Object... params)
	{
		// ReentrantLock lock = new ReentrantLock(true);
		// lock.lock();
		// try { ... } finally { lock.unlock(); }
	}

	public void dispose()
	{
		stop();

		new Utils(context).log("Disposing an audio thread.");
	}

	public boolean isRunning()
	{
		return (thread != null)
			&& (thread.getState() != Thread.State.TERMINATED);
	}

	public void start()
	{
		if (isRunning()) return;

		thread = new Thread(this);
		thread.start();
	}

	public void stop()
	{
		if (!isRunning()) return;

		thread.interrupt();

		try
		{
			thread.join();
		}
		catch (InterruptedException exception)
		{
			new Utils(context).log(exception);
		}
		finally
		{
			thread = null;
		}
	}

	public void run()
	{
		android.os.Process.setThreadPriority(
			android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		output.start();
		input.start();

		doProcessing();

		input.stop();
		output.stop();
	}

	protected abstract void doProcessing();

	public static AudioThread create(Context context, AudioDevice input, AudioDevice output, AAF aaf)
	{
		switch (aaf)
		{
		case FAF:
			return new TransposeThread(context, input, output);
		case DAF:
			return new DelayThread(context, input, output);
		case FastDAF:
			return new LowDelayThread(context, input, output);
		default:
			throw new IllegalArgumentException("Illegal AAF argument!");
		}
	}
}
