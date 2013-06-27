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

package utn.frba.ps.conversorvoice.activities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import utn.frba.ps.conversorvoice.AAF;
import utn.frba.ps.conversorvoice.Utils;
import utn.frba.ps.conversorvoice.audio.HeadsetMode;
import utn.frba.ps.conversorvoice.services.AafService;
import utn.frba.ps.conversorvoice.services.ServiceListener;
import utn.frba.ps.conversorvoice.widgets.AafPicker;
import utn.frba.ps.conversorvoice.widgets.IntervalPicker;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import de.jurihock.conversorvoice.R;

public final class AafActivity extends AudioServiceActivity<AafService>
	implements
	PropertyChangeListener, 
	OnCheckedChangeListener, ServiceListener
{
	// Relevant activity widgets:
	private AafPicker			viewAafPicker			= null;
	private IntervalPicker		viewIntervalPicker		= null;
	private Button	viewStartButton		= null;
	private Button	viewStopButton		= null;

	public AafActivity()
	{
		super(AafService.class);
	}

	/**
	 * Initializes the activity, its layout and widgets.
	 * */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setActionBarContentView(R.layout.aaf);

		viewAafPicker = (AafPicker) this.findViewById(R.id.viewAafPicker);
		viewAafPicker.setPropertyChangeListener(this);

		viewIntervalPicker = (IntervalPicker) this.findViewById(
			R.id.viewIntervalPicker);
		viewIntervalPicker.setPropertyChangeListener(this);

		viewStartButton = (Button) this.findViewById(R.id.viewStartButton);
		viewStopButton = (Button) this.findViewById(R.id.viewStopButton);
		
		// set red if checked otherwise white
		viewStartButton.setOnClickListener(new ListenerStart());
		viewStopButton.setOnClickListener(new ListenerStop());
	}
	
	private class ListenerStart implements OnClickListener {
		public void onClick(View v) {
			getService().startThread();
			updateButtons(true);
		}
	}
	
	private class ListenerStop implements OnClickListener {
		public void onClick(View v) {
			getService().stopThread(false);
			updateButtons(false);
		}

	}

	private void updateButtons(boolean activateStart) {
		viewStartButton.setEnabled(activateStart);
		viewStopButton.setEnabled(!activateStart);
	}
	
	@Override
	protected void onServiceConnected()
	{
		new Utils(this).log("AafActivity founds the service.");

		getService().setActivityVisible(true, this.getClass());
		getService().setListener(this);

		// Update widgets
		viewAafPicker.setAaf(getService().getAaf());
		updateButtons(getService().isThreadRunning());

		if (getService().getAaf() == AAF.FAF)
		{
			viewIntervalPicker.setVisibility(View.VISIBLE);

			if(getService().getThreadParams() != null)
			{
				int interval = Integer.parseInt(
					getService().getThreadParams()[0].toString());
				viewIntervalPicker.setInterval(interval);
			}
		}
		else
		{
			viewIntervalPicker.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onServiceDisconnected()
	{
		new Utils(this).log("AafActivity losts the service.");

		if (!this.isFinishing())
		{
			getService().setActivityVisible(false, this.getClass());
		}

		getService().setListener(null);
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if (isChecked && getService().getHeadsetMode()
				== HeadsetMode.WIRED_HEADSET)
		{
			getService().setHeadsetMode(HeadsetMode.BLUETOOTH_HEADSET);
		}
		else if (!isChecked && getService().getHeadsetMode()
				== HeadsetMode.BLUETOOTH_HEADSET)
		{
			getService().setHeadsetMode(HeadsetMode.WIRED_HEADSET);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getSource().equals(viewAafPicker))
		{
			AAF aaf = viewAafPicker.getAaf();

			getService().setAaf(aaf);

			if (aaf == AAF.FAF)
			{
				viewIntervalPicker.setVisibility(View.VISIBLE);

				getService().setThreadParams(
					viewIntervalPicker.getInterval());
			}
			else
			{
				viewIntervalPicker.setVisibility(View.GONE);
			}
		}
		else if (event.getSource().equals(viewIntervalPicker))
		{
			int interval = viewIntervalPicker.getInterval();

			getService().setThreadParams(interval);
		}
	}
	
	public void onServiceFailed()
	{
		viewStartButton.setEnabled(false);
		viewStopButton.setEnabled(false);

		new Utils(this).toast(getString(R.string.ServiceFailureMessage));
	}
}
