package utn.frba.ps.conversorvoice.dsp.vad;

import utn.frba.ps.conversorvoice.Utils;
import android.content.Context;

public final class SilenceTester
{
	private final Context context;
	private final RMS				rms;
	private final SchmittTrigger	hysteresis;

	private boolean					isSignalSilent	= false;

	public SilenceTester(Context context)
	{
		this.context = context;

		rms = new RMS(10);
		hysteresis = new SchmittTrigger(1000, 1000000);
	}

	public void testFrame(short[] frame)
	{
		for (int i = 0; i < frame.length; i++)
		{
			long value = frame[i];
			long meanValue = rms.getMeanValue(value);
			boolean isSignalSilent = hysteresis.isLow(meanValue);

			if (this.isSignalSilent != isSignalSilent)
			{
				this.isSignalSilent = isSignalSilent;
				new Utils(context).log("SilenceTester: %s, %s, %s",
					isSignalSilent, meanValue, value);
			}
		}
	}
}
