package org.thunlp.thulac;

import org.thunlp.thulac.data.TaggedWord;
import org.thunlp.thulac.util.StringOutputHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * An interface used to handle the output from the segmentation program. The whole
 * handling process is based on lines, though its extending the
 * {@link IProgramStateListener} allows it to listen the starting and termination
 * events of the program, therefore implementations should also concentrate on lines.
 */
public interface IOutputHandler extends IProgramStateListener {
	/**
	 * Creates an instance of {@link IOutputHandler} which writes output to
	 * {@link System#out}, using the default charset as the output encoding.
	 *
	 * @return The {@link IOutputHandler} created.
	 */
	static IOutputHandler createDefault() {
		return new WriterOutputHandler(new BufferedWriter(
				new OutputStreamWriter(System.out)));
	}

	/**
	 * Creates an instance of {@link IOutputHandler} which writes output to the
	 * given file using UTF-8 as file encoding.
	 *
	 * @param filename
	 * 		The name of the file to output to.
	 *
	 * @return The {@link IOutputHandler} created.
	 *
	 * @throws IOException
	 * 		Is the file cannot be created or is not writable.
	 */
	static IOutputHandler createFromFile(String filename) throws IOException {
		return createFromFile(filename, (Charset) null);
	}

	/**
	 * Creates an instance of {@link IOutputHandler} which writes output to the
	 * given file using a given charset as encoding.
	 *
	 * @param filename
	 * 		The name of the file to output to.
	 * @param charsetName
	 * 		The optional name of the charset to use, defaulted to "UTF-8".
	 *
	 * @return The {@link IOutputHandler} created.
	 *
	 * @throws IOException
	 * 		Is the file cannot be created or is not writable.
	 * @throws UnsupportedCharsetException
	 * 		If the charset referred to by the given name is not supported.
	 */
	static IOutputHandler createFromFile(String filename, String charsetName)
			throws IOException, UnsupportedCharsetException {
		Charset charset = null;
		if (charsetName != null) charset = Charset.forName(charsetName);
		return createFromFile(filename, charset);
	}

	/**
	 * Creates an instance of {@link IOutputHandler} which writes output to the
	 * given file using a given charset as encoding.
	 *
	 * @param filename
	 * 		The name of the file to output to.
	 * @param charset
	 * 		The optional file encoding to use, defaulted to UTF-8.
	 *
	 * @return The {@link IOutputHandler} created.
	 *
	 * @throws IOException
	 * 		Is the file cannot be created or is not writable.
	 */
	static IOutputHandler createFromFile(String filename, Charset charset)
			throws IOException {
		if (filename == null) return null;
		if (charset == null) charset = StandardCharsets.UTF_8;
		return new WriterOutputHandler(
				Files.newBufferedWriter(Paths.get(filename), charset));
	}

	/**
	 * Creates an instance of {@link StringOutputHandler} which writes output to an
	 * {@link String} in memory.<br>
	 * It is typical to use this method like this:
	 * <pre><code>
	 * StringOutputHandler output = IOutputHandler.createOutputToString();
	 * Thulac.split(input, output, segOnly); // or anything else
	 * String outputStr = output.getString();
	 * </code></pre>
	 *
	 * @return The {@link StringOutputHandler} created.
	 */
	static StringOutputHandler createOutputToString() {
		return new StringOutputHandler();
	}

	/**
	 * Handles the {@link List} of {@link TaggedWord} generated by the segmentation
	 * program. Since one input line might be split into multiple line segments,
	 * this method might be invoked several times between a pair of
	 * {@link #handleLineStart()} and {@link #handleLineEnd()}. Traditionally, the
	 * param {@code word} of all the invocations of this methods between a pair of
	 * {@link #handleLineEnd()} and {@link #handleLineEnd()} come from the same line of
	 * input, and the output handler should output to the same line as well, however
	 * this is not compulsory.
	 *
	 * @param words
	 * 		The {@link List} of {@link TaggedWord} generated processing one line segment.
	 */
	void handleLineSegment(List<TaggedWord> words, boolean segOnly) throws IOException;

	/**
	 * Called when an input line is obtained from {@link IInputProvider} and the
	 * segmentation program is about to begin breaking the line into segments. This
	 * method is basically for initializations, e.g., creating new line, etc.<br>
	 * This method is invoked before {@link #handleLineSegment(List, boolean)}.
	 */
	void handleLineStart() throws IOException;

	/**
	 * Called when segmentation of an input line is finished and the segmentation
	 * program is about to begin processing the next line. This method is basically for
	 * finalisation, e.g., flushing input of this line, etc.<br>
	 * This method is invoked after {@link #handleLineSegment(List, boolean)}.
	 */
	void handleLineEnd() throws IOException;
}
