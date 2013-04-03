package de.unipotsdam.nexplorer.tools.dummyplayer;

public class DummyBatchStarter {

	private static String defaultHost = "127.0.0.1:8080";
	private static int defaultAiCount = 5;

	public static void main(String[] args) {
		String tmpHost = defaultHost;
		int tmpAi = defaultAiCount;
		if (args.length >= 1) {
			tmpHost = args[0];
		}
		if (args.length >= 2) {
			tmpAi = Integer.parseInt(args[1]);
		}

		final String host = tmpHost;
		final int aiCount = tmpAi;
		System.out.println("Host: " + host);
		System.out.println("AI count: " + aiCount);

		for (int count = 0; count < aiCount; count++)
			new Thread(new Runnable() {

				@Override
				public void run() {
					PlayerDummy.main(new String[] { host });
				}
			}).start();
	}
}
