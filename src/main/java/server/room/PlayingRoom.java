package server.room;

import server.player.PlayerSession;

/**
 * Игровая комната
 *
 * @author Alexandr Romanychev
 * @since 21.10.2023
 */
public class PlayingRoom {
	private final PlayerSession firstPlayer;
	private final PlayerSession secondPlayer;

	public PlayingRoom(PlayerSession firstPlayer, PlayerSession secondPlayer) {
		this.firstPlayer = firstPlayer;
		this.secondPlayer = secondPlayer;
	}

	public void run() {
		new Thread(new PlayingRoomThread(firstPlayer, secondPlayer)).start();
	}

}
