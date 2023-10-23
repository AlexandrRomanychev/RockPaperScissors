package server.player;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import server.service.GameService;

import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Поток для игрока
 *
 * @author Alexandr Romanychev
 * @since 21.10.2023
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerThread implements Runnable {
	final GameService gameService;
	volatile PlayerSession playerSession;

	public PlayerThread(Socket socket, GameService gameService) {
		this.playerSession = new PlayerSession(socket);
		this.gameService = gameService;
		scheduleAutoClosingSession();
	}

	/**
	 * Сессия пользователя автоматически закроется через 5 минут
	 */
	private void scheduleAutoClosingSession() {
		ScheduledExecutorService autoClosingExecutor = Executors.newSingleThreadScheduledExecutor();
		autoClosingExecutor.schedule(() -> {
			gameService.removePlayer(playerSession);
			playerSession.closeSession();
		}, 5, TimeUnit.MINUTES);
	}

	@Override
	public void run() {
		while(!playerSession.getPlayerState().isFinal()) {
			if (playerSession.getPlayerState() == PlayerSessionState.ENTER_NAME){
				playerSession.getWriter().println("Enter your nickname...");
				enterNickName();
				this.playerSession.nextPlayerState();
			}
			if (playerSession.getPlayerState() == PlayerSessionState.PRINT_SEARCHING_FOR_OPPONENT) {
				playerSession.getWriter().println("Searching for opponent...");
				this.playerSession.nextPlayerState();
			}
		}
		if (this.playerSession.getPlayerState() == PlayerSessionState.WIN) {
			playerSession.getWriter().println("You win! Congratulations!");
		}
		if (this.playerSession.getPlayerState() == PlayerSessionState.LOSE) {
			playerSession.getWriter().println("You lose! Don't worry and try again!");
		}
		this.playerSession.closeSession();
	}

	/**
	 * Ввод ника игрока
	 */
	private void enterNickName() {
		playerSession.readValue().ifPresent(readedValue -> playerSession.setName(readedValue));
		while(!gameService.addNewPlayer(playerSession)) {
			playerSession.getWriter().println("This nickname is already in use. Please choose another!");
			playerSession.readValue().ifPresent(readedValue -> playerSession.setName(readedValue));
		}
	}
}
