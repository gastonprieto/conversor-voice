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

import utn.frba.ps.conversorvoice.DAFX;
import utn.frba.ps.conversorvoice.Utils;
import utn.frba.ps.conversorvoice.audio.HeadsetMode;
import utn.frba.ps.conversorvoice.services.DafxService;
import utn.frba.ps.conversorvoice.services.ServiceListener;
import utn.frba.ps.conversorvoice.widgets.ColoredToggleButton;
import utn.frba.ps.conversorvoice.widgets.DafxPicker;
import utn.frba.ps.conversorvoice.widgets.IntervalPicker;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import de.jurihock.conversorvoice.R;

public final class DafxActivity extends AudioServiceActivity<DafxService>
	implements
	PropertyChangeListener, OnClickListener,
	OnCheckedChangeListener, ServiceListener
{
	// Relevant activity widgets:
	private DafxPicker			viewDafxPicker			= null;
	private IntervalPicker		viewIntervalPicker		= null;
	private ColoredToggleButton	viewStartStopButton		= null;

	public DafxActivity()
	{
		super(DafxService.class);
	}

	/**
	 * Initializes the activity, its layout and widgets.
	 * */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setActionBarContentView(R.layout.dafx);

		viewDafxPicker = (DafxPicker) this.findViewById(R.id.viewDafxPicker);
		viewDafxPicker.setPropertyChangeListener(this);

		viewIntervalPicker = (IntervalPicker) this.findViewById(
			R.id.viewIntervalPicker);
		viewIntervalPicker.setPropertyChangeListener(this);

		viewStartStopButton = (ColoredToggleButton) this.findViewById(
			R.id.viewStartStopButton);
		// set red if checked otherwise white
		viewStartStopButton.setOnClickListener(this);
	}

	@Override
	protected void onServiceConnected()
	{
		new Utils(this).log("DafxActivity founds the service.");

		getService().setActivityVisible(true, this.getClass());
		getService().setListener(this);

		// Update widgets
		viewDafxPicker.setDafx(getService().getDafx());
		viewStartStopButton.setChecked(getService().isThreadRunning());

		if (getService().getDafx() == DAFX.Transpose)
		{
			viewIntervalPicker.setVisibility(View.VISIBLE);

			int interval = Integer.parseInt(
				getService().getThreadParams()[0].toString());
			viewIntervalPicker.setInterval(interval);
		}
		else
		{
			viewIntervalPicker.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onServiceDisconnected()
	{
		new Utils(this).log("DafxActivity losts the service.");

		if (!this.isFinishing())
		{
			getService().setActivityVisible(false, this.getClass());
		}

		getService().setListener(null);
	}

	public void onClick(View view)
	{
		if (getService().isThreadRunning())
		{
			if (viewStartStopButton.isChecked())
				viewStartStopButton.setChecked(false);

			getService().stopThread(false);
		}
		else
		{
			if (!viewStartStopButton.isChecked())
				viewStartStopButton.setChecked(true);

			getService().startThread();
		}

		// BZZZTT!!1!
		viewStartStopButton.performHapticFeedback(0);
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
		if (event.getSource().equals(viewDafxPicker))
		{
			DAFX dafx = viewDafxPicker.getDafx();

			getService().setDafx(dafx);

			if (dafx == DAFX.Transpose)
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
		if (viewStartStopButton.isChecked())
			viewStartStopButton.setChecked(false);

		new Utils(this).toast(getString(R.string.ServiceFailureMessage));

		// BZZZTT!!1!
		viewStartStopButton.performHapticFeedback(0);
	}
}
