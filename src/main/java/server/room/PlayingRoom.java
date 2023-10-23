package server.room;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import server.player.PlayerSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Игровая комната
 *
 * @author Alexandr Romanychev
 * @since 21.10.2023
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PlayingRoom {
	PlayerSession firstPlayer;
	PlayerSession secondPlayer;

	public void run() {
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		executorService.submit(new PlayingRoomThread(firstPlayer, secondPlayer, executorService));
	}

}
