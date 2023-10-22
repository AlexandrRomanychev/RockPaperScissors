package server.helper;

import java.io.PrintWriter;

/**
 * Помощник для вывода информации в output stream
 *
 * @author Alexandr Romanychev
 * @since 21.10.2023
 */
public class WriterHelper {

	public static void write(PrintWriter writer, String message) {
		writer.println(message);
		writer.flush();
	}
}
