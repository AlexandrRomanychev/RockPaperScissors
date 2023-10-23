package server.room;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import server.game.GameResult;
import server.game.GameVariable;
import server.player.PlayerSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Поток для игры
 *
 * @author Alexandr Romanychev
 * @since 22.10.2023
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PlayingRoomThread implements Runnable {

	PlayerSession firstPlayer;
	PlayerSession secondPlayer;

	ExecutorService executorService;

	@SneakyThrows
	@Override
	public void run() {
		firstPlayer.nextPlayerState();
		secondPlayer.nextPlayerState();
		printInfoAboutOpponent();
		while(true) {
			CompletableFuture<Optional<GameVariable>> firstPlayerWaitAnswer = createPlayerWaitAnswerCompletableFuture(firstPlayer);
			CompletableFuture<Optional<GameVariable>> secondPlayerWaitAnswer = createPlayerWaitAnswerCompletableFuture(secondPlayer);
			createListOfJobAndJoin(firstPlayerWaitAnswer, secondPlayerWaitAnswer);

			Optional<GameVariable> firstPlayerAnswer = firstPlayerWaitAnswer.get();
			Optional<GameVariable> secondPlayerAnswer = secondPlayerWaitAnswer.get();
			if (!firstPlayerAnswer.isPresent()) {
				secondPlayer.setWinnerState();
				firstPlayer.setTerminateState();
				break;
			}
			if (!secondPlayerAnswer.isPresent()) {
				firstPlayer.setWinnerState();
				secondPlayer.setTerminateState();
				break;
			}
			GameResult gameResult = firstPlayerAnswer.get().compareVariables(secondPlayerAnswer.get());
			if (gameResult != GameResult.DRAW) {
				if (gameResult == GameResult.WIN) {
					firstPlayer.setWinnerState();
					secondPlayer.setLooserState();
				} else if (gameResult == GameResult.LOSE) {
					firstPlayer.setLooserState();
					secondPlayer.setWinnerState();
				}
				break;
			} else {
				printInfoAboutDraw();
			}
		}
	}

	/**
	 * Создать список задач ввода ответа пользователями и дождаться обоих ответов
	 * @param firstPlayerWaitAnswer ответ первого пользователя
	 * @param secondPlayerWaitAnswer ответ второго пользователя
	 */
	private void createListOfJobAndJoin(
		CompletableFuture<Optional<GameVariable>> firstPlayerWaitAnswer,
		CompletableFuture<Optional<GameVariable>> secondPlayerWaitAnswer
	) {
		List<CompletableFuture<Optional<GameVariable>>> playerWaitAnswerList = new ArrayList<>();
		playerWaitAnswerList.add(firstPlayerWaitAnswer);
		playerWaitAnswerList.add(secondPlayerWaitAnswer);
		playerWaitAnswerList.forEach(CompletableFuture::join);
	}

	/**
	 * Вывести для обоих игроков информацию об оппоненте
	 */
	private void printInfoAboutOpponent() {
		firstPlayer.getWriter().println(String.format("Your opponent %s", secondPlayer.getName()));
		secondPlayer.getWriter().println(String.format("Your opponent %s", firstPlayer.getName()));
	}

	/**
	 * Вывести для обоих игроков информацию о ничьей
	 */
	private void printInfoAboutDraw() {
		firstPlayer.getWriter().println("Draw! Another round!");
		secondPlayer.getWriter().println("Draw! Another round!");
	}

	/**
	 * Создать CompletableFuture для ввода значения (rock, paper, scissors) и валидации введенного значения
	 * @param playerSession сессия пользователя
	 */
	private CompletableFuture<Optional<GameVariable>> createPlayerWaitAnswerCompletableFuture(
		PlayerSession playerSession
	) {
		return CompletableFuture.supplyAsync(() -> {
				playerSession.getWriter().println("Enter value: rock, paper, scissors");
				Optional<GameVariable> validGameVariable = enterValidGameVariable(playerSession);
				playerSession.getWriter().println("Waiting for your opponent...");
				return validGameVariable;
			}, executorService);
	}

	/**
	 * Возвращает валидный ответ от пользователя, проверяя его и запрашивая ввести его заново если ответ невалиден
	 * @param playerSession сессия пользователя, для которого происходит проверка валидации
	 */
	private Optional<GameVariable> enterValidGameVariable(PlayerSession playerSession) {
		Optional<String> readedValue = playerSession.readValue();
		Optional<GameVariable> mayBeGameVariable = readedValue.flatMap(GameVariable::getGameVariableByValue);
		while(readedValue.isPresent() && !mayBeGameVariable.isPresent() && !playerSession.getPlayerState().isFinal()) {
			playerSession.getWriter().println("You entered invalid value, please try again...");
			readedValue = playerSession.readValue();
			mayBeGameVariable = readedValue.flatMap(GameVariable::getGameVariableByValue);
		}
		return mayBeGameVariable;
	}
}
