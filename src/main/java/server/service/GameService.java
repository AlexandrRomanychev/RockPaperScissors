package server.service;

import server.helper.WriterHelper;
import server.player.PlayerSession;
import server.room.PlayingRoom;

import java.util.*;
import java.util.concurrent.*;

/**
 * Сервис для формирования игровых комнат
 *
 * @author Alexandr Romanychev
 * @since 21.10.2023
 */
public class GameService {
	private final Deque<PlayerSession> playerSessions;

	public GameService() {
		this.playerSessions = new ConcurrentLinkedDeque<>();
		startScheduledCreatingRooms();
	}

	/**
	 * Запустить запуск создания комнат по расписания каждые 10 секунд
	 */
	private void startScheduledCreatingRooms() {
		Executors.newScheduledThreadPool(1)
			.scheduleWithFixedDelay(this::createPlayingRooms, 30, 30, TimeUnit.SECONDS);
	}

	/**
	 * Создание отдельных комнат для проведения игр между двумя участниками
	 */
	private void createPlayingRooms() {
		while(playerSessions.size() >= 2) {
			PlayerSession firstPlayer = playerSessions.pollFirst();
			PlayerSession secondPlayer = playerSessions.pollFirst();
			new PlayingRoom(firstPlayer, secondPlayer).run();
		}
	}

	/**
	 * Добавить нового игрока
	 * @param playerSession сессия нового игрока
	 */
	public synchronized boolean addNewPlayer(PlayerSession playerSession) {
		if (this.playerSessions.contains(playerSession)) {
			return false;
		}
		this.playerSessions.add(playerSession);
		playerSession.nextPlayerState();
		return true;
	}
}
