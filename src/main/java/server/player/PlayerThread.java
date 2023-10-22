package server.player;

import server.helper.WriterHelper;
import server.service.GameService;

import java.io.*;
import java.net.Socket;

/**
 * Поток для игрока
 *
 * @author Alexandr Romanychev
 * @since 21.10.2023
 */
public class PlayerThread implements Runnable {
	GameService gameService;
	private volatile PlayerSession playerSession;

	public PlayerThread(Socket socket, GameService gameService) throws IOException {
		this.playerSession = new PlayerSession(socket);
		this.gameService = gameService;
	}

	@Override
	public void run() {
		while(!playerSession.getPlayerState().isFinal()) {
			if (playerSession.getPlayerState() == PlayerSessionState.ENTER_NAME){
				WriterHelper.write(playerSession.getWriter(), "Enter your nickname...");
				enterNickName();
			}
			if (playerSession.getPlayerState() == PlayerSessionState.PRINT_SEARCHING_FOR_OPPONENT) {
				WriterHelper.write(playerSession.getWriter(), "Searching for opponent...");
				this.playerSession.nextPlayerState();
			}
		}
		if (this.playerSession.getPlayerState() == PlayerSessionState.WIN) {
			WriterHelper.write(this.playerSession.getWriter(), "You win! Congratulations!");
		}
		if (this.playerSession.getPlayerState() == PlayerSessionState.LOSE) {
			WriterHelper.write(this.playerSession.getWriter(), "You lose! Don't worry and try again!");
		}
		if (this.playerSession.getPlayerState() == PlayerSessionState.TERMINATED) {
			//TODO
			System.out.println("Uknown");
		}
		try {
			this.playerSession.closeSession();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Ввод ника игрока
	 */
	private void enterNickName() {
		String userName = playerSession.readValue();
		playerSession.setName(userName);
		while(!gameService.addNewPlayer(playerSession)) {
			WriterHelper.write(playerSession.getWriter(), "This nickname is already in use. Please choose another!");
			userName = playerSession.readValue();
			playerSession.setName(userName);
		}
	}
}
