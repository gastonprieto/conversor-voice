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

package utn.frba.ps.conversorvoice.services;

import utn.frba.ps.conversorvoice.DAFX;
import utn.frba.ps.conversorvoice.io.AudioDevice;
import utn.frba.ps.conversorvoice.threads.AudioThread;
import android.content.Intent;
import android.os.IBinder;

/**
 * Implementation of the vocoder audio service.
 * */
public final class DafxService extends AudioService
{
	// The DAFX type
	private DAFX	dafx	= DAFX.valueOf(0);

	public DAFX getDafx()
	{
		return dafx;
	}

	public void setDafx(DAFX dafx)
	{
		if (this.dafx == dafx) return;

		preferences.setDafx(dafx);

		if (isThreadRunning())
		{
			stopThread(true);
			
			this.dafx = dafx;
			setThreadParams((Object[]) null);
			
			startThread();
		}
		else
		{
			this.dafx = dafx;
			setThreadParams((Object[]) null);
		}
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();

		this.setDafx(preferences.getDafx());
	}

	@Override
	protected AudioThread createAudioThread(AudioDevice input, AudioDevice output)
	{
		return AudioThread.create(this, input, output, dafx);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new ServiceBinder<DafxService>(this);
	}
}
