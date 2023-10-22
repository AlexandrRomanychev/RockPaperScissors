package server.game;

import server.player.PlayerThread;
import server.service.GameService;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Сервер для игры в камень-ножницы-бумага
 *
 * @author Alexandr Romanychev
 * @since 21.10.2023
 */

public class GameServer {
	private static final Integer SERVER_PORT = 6969;
	private ServerSocket serverSocket;

	private final GameService gameService;

	public GameServer() {
		this.gameService = new GameService();
		try {
			this.serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
	}

	/**
	 * Запуск сервера
	 */
	public void run () {
		while(true) {
			try {
				new Thread(new PlayerThread(serverSocket.accept(), gameService)).start();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
