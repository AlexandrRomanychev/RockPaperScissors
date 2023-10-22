package server.player;

import server.helper.WriterHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

/**
 * Сессия пользователя
 *
 * @author Alexandr Romanychev
 * @since 21.10.2023
 */
public class PlayerSession {
	private final PrintWriter writer;
	private final BufferedReader reader;
	private final Socket socket;
	private PlayerSessionState playerState = PlayerSessionState.ENTER_NAME;
	private String name;

	public PlayerSession(Socket socket) throws IOException {
		this.socket = socket;
		this.writer = new PrintWriter(socket.getOutputStream());
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerSessionState getPlayerState() {
		return playerState;
	}

	public void nextPlayerState() {
		this.playerState = this.playerState.nextState();
	}

	public void setWinner() {
		this.playerState = PlayerSessionState.WIN;
	}

	public void setLooser() {
		this.playerState = PlayerSessionState.LOSE;
	}

	public void closeSession() throws IOException {
		WriterHelper.write(this.writer, "Closing session...");
		this.writer.close();
		this.reader.close();
		this.socket.close();
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public String readValue() {
		String response = null;
		try {
			response = this.reader.readLine().trim();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return response;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PlayerSession)) {
			return false;
		}
		return this.name.equals(((PlayerSession)o).getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
