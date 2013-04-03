package de.unipotsdam.nexplorer.tools.dummyplayer;

public class StatsPrinter extends Thread {

	private static Thread printer;

	private StatsPrinter() {
	}

	public static synchronized void startOnce() {
		if (printer == null) {
			printer = new StatsPrinter();
			printer.setDaemon(true);
			printer.start();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}

			Stopwatch.printStatus();
		}
	}
}
