package server.player;

/**
 * Состояние сессии игрока
 *
 * @author Alexandr Romanychev
 * @since 22.10.2023
 */
public enum PlayerSessionState {
	ENTER_NAME(false) {
		@Override
		public PlayerSessionState nextState() {
			return PRINT_SEARCHING_FOR_OPPONENT;
		}
	},
	PRINT_SEARCHING_FOR_OPPONENT(false) {
		@Override
		public PlayerSessionState nextState() {
			return SEARCHING_FOR_OPPONENT;
		}
	},
	SEARCHING_FOR_OPPONENT(false) {
		@Override
		public PlayerSessionState nextState() {
			return PLAYING;
		}
	},
	PLAYING(false) {
		@Override
		public PlayerSessionState nextState() {
			return PLAYING;
		}
	},
	WIN(true) {
		@Override
		public PlayerSessionState nextState() {
			return this;
		}
	},
	LOSE(true) {
		@Override
		public PlayerSessionState nextState() {
			return this;
		}
	},
	TERMINATED(true) {
		@Override
		public PlayerSessionState nextState() {
			return this;
		}
	}
	;

	private final boolean isFinal;

	PlayerSessionState(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public abstract PlayerSessionState nextState();
}
