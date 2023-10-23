import server.game.GameServer;

/**
 * Точка входа в приложение
 *
 * @author Alexandr Romanychev
 * @since 21.10.2023
 */
public class Main {

	public static void main(String[] args) {
		new GameServer().run();
	}
}
