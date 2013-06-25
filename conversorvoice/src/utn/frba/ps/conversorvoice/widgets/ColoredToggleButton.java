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

package utn.frba.ps.conversorvoice.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public final class ColoredToggleButton extends ToggleButton implements
	OnCheckedChangeListener
{
	private static final int	COLOR_BACKGROUND_ON	= Color.RED;
	private static final int	COLOR_ICON			= Color.WHITE;

	public ColoredToggleButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public ColoredToggleButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public ColoredToggleButton(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		this.setOnCheckedChangeListener(this);
		onCheckedChanged(this, isChecked());
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		StateListDrawable selector = (StateListDrawable) this.getBackground();
		LayerDrawable background = (LayerDrawable) selector.getCurrent();
		Drawable icon = background.getDrawable(1);

		if (isChecked)
		{
			background.setColorFilter(COLOR_BACKGROUND_ON,
				PorterDuff.Mode.SRC_ATOP);

			icon.setColorFilter(COLOR_ICON,
				PorterDuff.Mode.SRC_IN);
		}
		else
		{
			icon.setColorFilter(COLOR_ICON,
				PorterDuff.Mode.SRC_IN);
		}
	}
}
