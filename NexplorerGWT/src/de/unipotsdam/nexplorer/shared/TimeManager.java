package de.unipotsdam.nexplorer.shared;

import java.sql.Date;



public class TimeManager {
	/**
	 * converts a data expression in long to a representable one as a string
	 * @param timeSpanInMillis
	 * @return
	 */
	public static String convertToReadableTimeSpan(Long timeSpanInMillis) {
		timeSpanInMillis = timeSpanInMillis / 1000;
		Long diff[] = new Long[] { 0l, 0l, 0l, 0l };
		/* sec */diff[3] = (timeSpanInMillis >= 60 ? timeSpanInMillis % 60
				: timeSpanInMillis);
		/* min */diff[2] = (timeSpanInMillis = (timeSpanInMillis / 60)) >= 60 ? timeSpanInMillis % 60
				: timeSpanInMillis;
		/* hours */diff[1] = (timeSpanInMillis = (timeSpanInMillis / 60)) >= 24 ? timeSpanInMillis % 24
				: timeSpanInMillis;
		/* days */diff[0] = (timeSpanInMillis = (timeSpanInMillis / 24));

		return (diff[0] + " Tage " + diff[1] + " Stunden " + diff[2] + " Minuten "
				+ diff[3] + " Sekunden ");		

	}
	
	public static String convertToReadableTimeSpan(int seconds) {
		return convertToReadableTimeSpan(seconds*1000);	
	}
}
