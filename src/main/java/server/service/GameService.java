package server.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameService {
	Deque<PlayerSession> playerSessions;

	public GameService() {
		this.playerSessions = new ConcurrentLinkedDeque<>();
		startScheduledCreatingRooms();
	}

	/**
	 * Запустить запуск создания комнат по расписания каждые 10 секунд
	 */
	private void startScheduledCreatingRooms() {
		Executors.newSingleThreadScheduledExecutor()
			.scheduleWithFixedDelay(this::createPlayingRooms, 10, 10, TimeUnit.SECONDS);
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
	public boolean addNewPlayer(PlayerSession playerSession) {
		if (this.playerSessions.contains(playerSession)) {
			return false;
		}
		this.playerSessions.add(playerSession);
		return true;
	}

	/**
	 * Удалить игрока из списка
	 * @param playerSession сессия пользователя
	 */
	public void removePlayer(PlayerSession playerSession) {
		playerSessions.remove(playerSession);
	}
}
