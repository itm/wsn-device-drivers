package de.uniluebeck.itm.rsc.apps.datalogger;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.or;

import java.io.IOException;
import java.util.Stack;
import java.util.regex.Pattern;

import com.google.common.base.Predicate;

/**
 * The Class AbstractConsoleWriter.
 */
public abstract class AbstractConsoleWriter implements PausableWriter {

	private boolean isPaused = false;

	private String regexFilter = ".*";

	private String bracketFilter = "";

	/*
	 * @see de.uniluebeck.itm.rsc.apps.datalogger.PausableWriter#write(byte[], int)
	 */
	@Override
	public void write(final byte[] content, final int messageType) {
		// Convert the received bytes into a String
		final String output = convert(content); 
		Pattern pattern = Pattern.compile(regexFilter);
		// Matching regex-filter
		if (!isPaused && pattern.matcher(output).matches()) { 											
			if (!bracketFilter.equals("")) {
				if (parseBracketsFilter(bracketFilter, messageType).apply(
						output)) { // Matching brackets-filter
					System.out.println(output); // output on console
				}
			} else {
				System.out.println(output);
			}
		}
	}

	/*
	 * @see de.uniluebeck.itm.rsc.apps.datalogger.PausableWriter#pause()
	 */
	@Override
	public void pause() {
		isPaused = true;
	}

	/*
	 * @see de.uniluebeck.itm.rsc.apps.datalogger.PausableWriter#resume()
	 */
	@Override
	public void resume() {
		isPaused = false;
	}

	/*
	 * @see de.uniluebeck.itm.rsc.apps.datalogger.PausableWriter#getRegexFilter()
	 */
	@Override
	public String getRegexFilter() {
		return this.regexFilter;
	}

	/*
	 * @see
	 * de.uniluebeck.itm.rsc.apps.datalogger.PausableWriter#setRegexFilter(java.lang
	 * .String)
	 */
	@Override
	public void setRegexFilter(final String regexFilter) {
		this.regexFilter = regexFilter;
	}

	/*
	 * @see de.uniluebeck.itm.rsc.apps.datalogger.PausableWriter#getBracketFilter()
	 */
	@Override
	public String getBracketFilter() {
		return this.bracketFilter;
	}

	/*
	 * @see
	 * de.uniluebeck.itm.rsc.apps.datalogger.PausableWriter#setBracketFilter(java.lang
	 * .String)
	 */
	@Override
	public void setBracketFilter(final String bracketFilter) {
		this.bracketFilter = bracketFilter;
	}

	/*
	 * @see
	 * de.uniluebeck.itm.rsc.apps.datalogger.PausableWriter#addBracketFilter(java.lang
	 * .String)
	 */
	@Override
	public void addBracketFilter(final String filter) {
		bracketFilter = bracketFilter + filter;
		System.out.println("Filter added");
	}

	/*
	 * @see
	 * de.uniluebeck.itm.rsc.apps.datalogger.PausableWriter#addRegexFilter(java.lang
	 * .String)
	 */
	@Override
	public void addRegexFilter(final String filter) {
		if (regexFilter.equals(".*")) {
			regexFilter = ""; // to add the filter, the regexFilter must be
								// empty
		}
		regexFilter = regexFilter + filter;
		System.out.println("Filter added");
	}

	/*
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {

	}

	/*
	 * @see
	 * de.uniluebeck.itm.rsc.apps.datalogger.PausableWriter#setLocation(java.lang.String
	 * )
	 */
	@Override
	public void setLocation(final String location) {

	}

	/**
	 * Parse_klammer_filter. Uses a stack to parse the brackets-filter for
	 * example: ((Datatype, Begin, Value)&(Datatype, Begin, Value))|(Datatype,
	 * Begin, Value)
	 * @param bracketFilter
	 *            the bracket filter
	 * @param messageType
	 *            the message type
	 * @return the predicate
	 */
	public Predicate<CharSequence> parseBracketsFilter(final String bracketFilter,
			final int messageType) {
		// first step parsing the filter and push the elements on the stack:
		Stack<Predicate<CharSequence>> expressions = new Stack<Predicate<CharSequence>>();
		Stack<String> operators = new Stack<String>();
		String expression = "";
		for (int i = 0; i < bracketFilter.length(); i++) {
			String character = Character.toString(bracketFilter.charAt(i));
			if (character.equals("|")) {
				operators.push("or");
			} else if (character.equals("&")) {
				operators.push("and");
			} else if (character.equals("(")) {
				// do nothing
			} else if (character.equals(")")) {
				if (!expression.equals("")) {
					Predicate<CharSequence> predicate = new BracketsPredicate(
							expression, messageType);
					expressions.push(predicate);
					expression = "";
				} else {
					// second step, if an element for example
					// (104,23,4)&(104,24,5)is found,
					Predicate<CharSequence> firstExpression = expressions.pop();
					Predicate<CharSequence> secondExpression = expressions
							.pop();
					String operator = operators.pop();
					// evaluate these two last expression and push the result on
					// the stack
					if (operator.equals("or")) {
						Predicate<CharSequence> result = or(firstExpression,
								secondExpression);
						expressions.push(result);
					} else if (operator.equals("and")) {
						Predicate<CharSequence> result = and(firstExpression,
								secondExpression);
						expressions.push(result);
					}
				}
			} else {
				expression = expression + character;
			}
		}
		// last step, evaluate the rest of elements on the stack
		while (operators.size() != 0) {
			Predicate<CharSequence> firstExpression = expressions.pop();
			Predicate<CharSequence> secondExpression = expressions.pop();
			String operator = operators.pop();
			if (operator.equals("or")) {
				Predicate<CharSequence> result = or(firstExpression,
						secondExpression);
				expressions.push(result);
			} else if (operator.equals("and")) {
				Predicate<CharSequence> result = and(firstExpression,
						secondExpression);
				expressions.push(result);
			}
		}
		// result is one predicate, that represents the whole filter
		return expressions.pop();
	}

	/**
	 * Converts the content to a string.
	 * @param content
	 *            the content
	 * @return the string
	 */
	public abstract String convert(byte[] content);
}
