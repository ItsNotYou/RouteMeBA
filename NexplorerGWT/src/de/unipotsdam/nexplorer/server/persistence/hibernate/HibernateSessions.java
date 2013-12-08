package de.unipotsdam.nexplorer.server.persistence.hibernate;

import java.util.Date;
import java.util.Timer;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import de.unipotsdam.nexplorer.server.Admin;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.GameStatus;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 * 
 * @author hgessner
 */
public class HibernateSessions {

	private static final boolean DEBUG_MODE = false;
	private static final boolean BATTERY_CHEATING = false;
	private static SessionFactory sessionFactory;
	private static boolean firstTime = true;

	private static SessionFactory initSessionFactory() {
		try {
			// Create the SessionFactory from standard (hibernate.cfg.xml) config file.
			Configuration configuration = new Configuration();
			configuration.configure();
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();

			return configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable ex) {
			// Log the exception.
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static synchronized SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory = initSessionFactory();
		}
		return sessionFactory;
	}

	public static synchronized void forceNewSessionFactory() {
		sessionFactory = initSessionFactory();
	}

	public static synchronized void clearDatabase() {
		Session session = getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.doWork(new TruncateGameTables());
			session.doWork(new TruncatePerformanceTables());

			session.flush();
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			throw new RuntimeException(e);
		} finally {
			session.close();
		}
	}

	public synchronized static void insertDebugPlayer() {
		// debug player
		if (HibernateSessions.firstTime && DEBUG_MODE) {
			HibernateSessions.firstTime = false;
			Players runner = null;
			Session session = getSessionFactory().openSession();
			try {
				Transaction tx = session.beginTransaction();

				Players players = new Players();
				players.setBattery(100d);
				players.setHasSignalRangeBooster(0l);
				players.setHasSignalStrengthBooster(null);
				Date now = new Date();
				players.setLastPositionUpdate(now.getTime());
				players.setLatitude(52.3935);
				players.setLongitude(13.130513);
				players.setName("testplayer");
				players.setRole((byte) 2);
				players.setScore(0l);
				players.setSequenceNumber(1l);
				session.save(players);

				Players two = new Players();
				two.setBattery(100.);
				two.setHasSignalRangeBooster(0l);
				two.setHasSignalStrengthBooster(null);
				two.setLastPositionUpdate(new Date().getTime());
				two.setLatitude(52.39345);
				two.setLongitude(13.130513);
				two.setName("player two");
				two.setRole((byte) 2);
				two.setScore(0l);
				two.setSequenceNumber(1l);
				session.save(two);

				Players third = new Players();
				third.setBattery(100.);
				third.setHasSignalRangeBooster(0l);
				third.setHasSignalStrengthBooster(0l);
				third.setLastPositionUpdate(new Date().getTime());
				third.setLatitude(52.3934);
				third.setLongitude(13.130513);
				third.setName("player three");
				third.setRole((byte) 2);
				third.setScore(0l);
				third.setSequenceNumber(15l);
				session.save(third);

				runner = two;

				session.flush();
				tx.commit();
			} catch (HibernateException e) {
				e.printStackTrace();
			} finally {
				session.close();
			}

			Timer timer = new Timer();
			timer.schedule(new RunningPlayer(runner.getId()), 0, 1000);
			timer = new Timer();
			timer.schedule(new ItemCheater(), 0, 5000);

			Admin admin = new Admin();
			Settings stats = admin.getDefaultGameStats();
			admin.startGame(new GameStats(stats));

			GameStats current;
			do {
				admin.resumeGame();
				current = admin.getGameStats();
			} while (current.getGameStatus() != GameStatus.ISRUNNING);
		}

		if (BATTERY_CHEATING) {
			Timer timer = new Timer();
			timer.schedule(new BatteryCheater(), 0, 2000);
		}
	}
}
