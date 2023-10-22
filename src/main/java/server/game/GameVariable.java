package server.game;

import java.util.Arrays;
import java.util.Optional;

import static server.game.GameResult.*;

/**
 * Значения для выбора в игре
 *
 * @author Alexandr Romanychev
 * @since 22.10.2023
 */
public enum GameVariable {
	ROCK("rock") {
		@Override
		public GameResult compareVariables(GameVariable gameVariable) {
			if (gameVariable == PAPER) {
				return LOSE;
			}
			if (gameVariable == ROCK) {
				return DRAW;
			}
			return WIN;
		}
	},
	PAPER("paper") {
		@Override
		public GameResult compareVariables(GameVariable gameVariable) {
			if (gameVariable == PAPER) {
				return DRAW;
			}
			if (gameVariable == ROCK) {
				return WIN;
			}
			return LOSE;
		}
	},
	SCISSORS("scissors") {
		@Override
		public GameResult compareVariables(GameVariable gameVariable) {
			if (gameVariable == PAPER) {
				return WIN;
			}
			if (gameVariable == ROCK) {
				return LOSE;
			}
			return DRAW;
		}
	}
	;

	private final String lowerCaseValue;

	GameVariable(String lowerCaseValue) {
		this.lowerCaseValue = lowerCaseValue;
	}

	public abstract GameResult compareVariables(GameVariable gameVariable);

	public static Optional<GameVariable> getGameVariableByValue(String value) {
		return Arrays.stream(values())
			.filter(gameVariable -> gameVariable.lowerCaseValue.equals(value.trim().toLowerCase()))
			.findFirst();
	}
}
