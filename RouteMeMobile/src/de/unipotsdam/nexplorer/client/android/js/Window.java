package de.unipotsdam.nexplorer.client.android.js;

public class Window {

	public static Object undefined = null;

	public static Location location = new Location();

	public static Button loginButton = new Button();

	public static Dialog beginDialog = new Dialog();

	public static Text waitingText = new Text();

	public static Text hint = new Text();

	public static Text nextItemDistance = new Text();

	public static Text activeItems = new Text();

	public static Button collectItemButton = new Button();

	public static void clearInterval(Interval interval) {
		// TODO Port
	}

	public static Interval setInterval(Object interval, long timeMillis) {
		// TODO Port
		return new Interval();
	}

	public static void ajax(Options options) {
		// TODO Port
	}

	public static <T> void each(java.util.Map<?, T> objects, Call<T> callback) {
		// TODO Port
	}

	public static boolean isNaN(double result) {
		// TODO Port
		return true;
	}

	public static double parseFloat(String value) {
		return Double.parseDouble(value);
	}
}
