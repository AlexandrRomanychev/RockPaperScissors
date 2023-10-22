package server.room;

import server.game.GameResult;
import server.game.GameVariable;
import server.helper.WriterHelper;
import server.player.PlayerSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Поток для игры
 *
 * @author Alexandr Romanychev
 * @since 22.10.2023
 */
public class PlayingRoomThread implements Runnable {

	private final PlayerSession firstPlayer;
	private final PlayerSession secondPlayer;

	public PlayingRoomThread(PlayerSession firstPlayer, PlayerSession secondPlayer) {
		this.firstPlayer = firstPlayer;
		firstPlayer.nextPlayerState();
		this.secondPlayer = secondPlayer;
		secondPlayer.nextPlayerState();
		printInfoAboutOpponent();
	}

	@Override
	public void run() {
		while(true) {
			List<CompletableFuture<GameVariable>> playerWaitAnswerList = new ArrayList<>();
			CompletableFuture<GameVariable> firstPlayerWaitAnswer = createPlayerWaitAnswerCompletableFuture(firstPlayer);
			CompletableFuture<GameVariable> secondPlayerWaitAnswer = createPlayerWaitAnswerCompletableFuture(secondPlayer);
			playerWaitAnswerList.add(firstPlayerWaitAnswer);
			playerWaitAnswerList.add(secondPlayerWaitAnswer);
			playerWaitAnswerList.forEach(CompletableFuture::join);
			try {
				GameVariable firstPlayerAnswer = firstPlayerWaitAnswer.get();
				GameVariable secondPlayerAnswer = secondPlayerWaitAnswer.get();
				GameResult gameResult = firstPlayerAnswer.compareVariables(secondPlayerAnswer);
				if (gameResult != GameResult.DRAW) {
					if (gameResult == GameResult.WIN) {
						firstPlayer.setWinner();
						secondPlayer.setLooser();
					} else if (gameResult == GameResult.LOSE) {
						firstPlayer.setLooser();
						secondPlayer.setWinner();
					}
					break;
				} else {
					printInfoAboutDraw();
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Вывести для обоих игроков информацию об оппоненте
	 */
	private void printInfoAboutOpponent() {
		WriterHelper.write(firstPlayer.getWriter(), String.format("Your opponent %s", secondPlayer.getName()));
		WriterHelper.write(secondPlayer.getWriter(), String.format("Your opponent %s", firstPlayer.getName()));
	}

	/**
	 * Вывести для обоих игроков информацию о ничьей
	 */
	private void printInfoAboutDraw() {
		WriterHelper.write(firstPlayer.getWriter(), "Draw! Another round!");
		WriterHelper.write(secondPlayer.getWriter(), "Draw! Another round!");
	}

	/**
	 * Создать CompletableFuture для ввода значения (rock, paper, scissors) и валидации введенного значения
	 * @param playerSession сессия пользователя
	 */
	private CompletableFuture<GameVariable> createPlayerWaitAnswerCompletableFuture(PlayerSession playerSession) {
		return CompletableFuture.supplyAsync(() -> {
				WriterHelper.write(playerSession.getWriter(), "Enter value: rock, paper, scissors");
				GameVariable validGameVariable = enterValidGameVariable(playerSession);
				WriterHelper.write(playerSession.getWriter(), "Waiting for your opponent...");
				return validGameVariable;
			});
	}

	/**
	 * Возвращает валидный ответ от пользователя, проверяя его и запрашивая ввести его заново если ответ невалиден
	 * @param playerSession сессия пользователя, для которого происходит проверка валидации
	 */
	private GameVariable enterValidGameVariable(PlayerSession playerSession) {
		String readedValue = playerSession.readValue();
		Optional<GameVariable> mayBeGameVariable = GameVariable.getGameVariableByValue(readedValue);
		while(!mayBeGameVariable.isPresent()) {
			WriterHelper.write(playerSession.getWriter(), "You entered invalid value, please try again...");
			readedValue = playerSession.readValue();
			mayBeGameVariable = GameVariable.getGameVariableByValue(readedValue);
		}
		return mayBeGameVariable.get();
	}
}
