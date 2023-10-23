package server.player;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

/**
 * Сессия пользователя
 *
 * @author Alexandr Romanychev
 * @since 21.10.2023
 */
@Slf4j
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerSession {
	final PrintWriter writer;
	final BufferedReader reader;
	final Socket socket;
	final Thread currentThread;
	PlayerSessionState playerState = PlayerSessionState.ENTER_NAME;
	@EqualsAndHashCode.Include
	String name;

	@SneakyThrows
	public PlayerSession(Socket socket) {
		this.socket = socket;
		this.currentThread = Thread.currentThread();
		this.writer = new PrintWriter(socket.getOutputStream(), true);
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void setName(String name) {
		this.name = name;
	}

	public void nextPlayerState() {
		this.playerState = this.playerState.nextState();
	}

	public void setWinnerState() {
		this.playerState = PlayerSessionState.WIN;
	}

	public void setLooserState() {
		this.playerState = PlayerSessionState.LOSE;
	}

	public void setTerminateState() {
		this.playerState = PlayerSessionState.TERMINATED;
	}

	@SneakyThrows
	public void closeSession() {
		if (writer != null) {
			writer.println("Closing session...");
			this.writer.close();
		}
		if (reader != null) {
			this.reader.close();
		}
		this.socket.close();
	}

	public Optional<String> readValue() {
		String readedValue = null;
		try {
			readedValue = reader.readLine();
		} catch (IOException e) {
			log.error("Can't read from OutputStream", e);
			setTerminateState();
		}
		return Optional.ofNullable(readedValue);
	}
}
