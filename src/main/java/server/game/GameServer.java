package server.game;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import server.player.PlayerThread;
import server.service.GameService;

import java.net.ServerSocket;

/**
 * Сервер для игры в камень-ножницы-бумага
 *
 * @author Alexandr Romanychev
 * @since 21.10.2023
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameServer {
	static final Integer SERVER_PORT = 8000;
	GameService gameService;
	ServerSocket serverSocket;

	@SneakyThrows
	public GameServer() {
		this.gameService = new GameService();
		this.serverSocket = new ServerSocket(SERVER_PORT);
	}

	/**
	 * Запуск сервера
	 */
	@SneakyThrows
	public void run () {
		while(true) {
			new Thread(new PlayerThread(serverSocket.accept(), gameService)).start();
		}
	}
}
