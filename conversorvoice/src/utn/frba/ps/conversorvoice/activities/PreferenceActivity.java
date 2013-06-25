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

import utn.frba.ps.conversorvoice.Preferences;
import utn.frba.ps.conversorvoice.Utils;
import greendroid.app.GDPreferenceActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import de.jurihock.conversorvoice.R;

public final class PreferenceActivity extends GDPreferenceActivity
{
	private Class<?>	callerActivityClass	= null;

	private Preferences	preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.preferences);

		preferences = new Preferences(this);

		if (getIntent().getExtras() != null)
		{
			String caller = getIntent().getExtras().getString("caller");
			if (caller != null)
			{
				// Add return button to the action bar
				getActionBarGreen().setType(ActionBar.Type.Empty);
				addActionBarItem(getActionBarGreen()
					.newActionBarItem(NormalActionBarItem.class)
					.setDrawable(R.drawable.action_bar_return));

				try
				{
					callerActivityClass = (Class<?>) Class.forName(caller);
				}
				catch (ClassNotFoundException exception)
				{
					new Utils(this).log(exception);
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.preferences, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.ResetPreferences:

			preferences.reset();

			// Refresh this activity
			// http://stackoverflow.com/questions/6380658/refreshing-views-of-preferences-when-using-preferenceactivity
			Intent intent = getIntent();
			{
				overridePendingTransition(0, 0);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finish();
				overridePendingTransition(0, 0);
			}
			startActivity(intent);

			break;
		}

		return true;
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position)
	{
		// Go back to the caller activity
		if (callerActivityClass != null && position == 0)
		{
			Intent intent = new Intent(this, callerActivityClass);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		return super.onHandleActionBarItemClick(item, position);
	}
}
