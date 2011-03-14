package de.uniluebeck.itm.datenlogger;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.or;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Stack;
import java.util.regex.Pattern;

import com.google.common.base.Predicate;

/**
 * The Class AbstractFileWriter.
 */
public abstract class AbstractFileWriter implements PausableWriter{

	private boolean isPaused = false;
	private Writer writer;
	private String regexFilter = ".*";
	private String bracketFilter = "";
	
	/* 
	 * @see de.uniluebeck.itm.datenlogger.PausableWriter#write(byte[], int)
	 */
	@Override
	public void write(byte[] content, int messageType) {
		final String output = convert(content);		//Convert the received bytes into a String
		Pattern pattern = Pattern.compile(regexFilter);
		if (!isPaused && pattern.matcher(output).matches()) {	//Matching regex-filter
			if(!bracketFilter.equals("")){
				if(parseBracketsFilter(bracketFilter, messageType).apply(output)){ 	//Matching brackets-filter
					try {
						writer.write(output);		//output in file
						writer.write("\n");
					} catch (IOException e) {
						System.out.println("Cannot write to file.");
					}
				}	
			}else{
				try {
					writer.write(output);
					writer.write("\n");
				} catch (IOException e) {
					System.out.println("Cannot write to file.");
				}
			}
		}
	}
	
	/* 
	 * @see de.uniluebeck.itm.datenlogger.PausableWriter#pause()
	 */
	@Override
	public void pause() {
		isPaused = true;
	}

	/* 
	 * @see de.uniluebeck.itm.datenlogger.PausableWriter#resume()
	 */
	@Override
	public void resume() {
		isPaused = false;
	}

	/* 
	 * @see de.uniluebeck.itm.datenlogger.PausableWriter#getRegexFilter()
	 */
	@Override
	public String getRegexFilter() {
		return this.regexFilter;
	}
	
	/* 
	 * @see de.uniluebeck.itm.datenlogger.PausableWriter#setRegexFilter(java.lang.String)
	 */
	@Override
	public void setRegexFilter(String regexFilter) {
		this.regexFilter = regexFilter;
	}
	
	/* 
	 * @see de.uniluebeck.itm.datenlogger.PausableWriter#getBracketFilter()
	 */
	@Override
	public String getBracketFilter() {
		return this.bracketFilter;
	}
	
	/* 
	 * @see de.uniluebeck.itm.datenlogger.PausableWriter#setBracketFilter(java.lang.String)
	 */
	@Override
	public void setBracketFilter(String bracketFilter) {
		this.bracketFilter = bracketFilter;
	}
	
	/* 
	 * @see de.uniluebeck.itm.datenlogger.PausableWriter#addBracketFilter(java.lang.String)
	 */
	@Override
	public void addBracketFilter(String filter) {
		bracketFilter = bracketFilter + filter;
		System.out.println("Filter added");
	}

	/* 
	 * @see de.uniluebeck.itm.datenlogger.PausableWriter#addRegexFilter(java.lang.String)
	 */
	@Override
	public void addRegexFilter(String filter) {
		if(regexFilter.equals(".*")){
			regexFilter = "";		//to add the filter, the regexFilter must be empty
		}
		regexFilter = regexFilter + filter;
		System.out.println("Filter added");
	}

	/* 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		writer.close();
	}
	
	/* 
	 * @see de.uniluebeck.itm.datenlogger.PausableWriter#setLocation(java.lang.String)
	 */
	@Override
	public void setLocation(String location){
		try {
			this.writer = new FileWriter(location);
		} catch (IOException e) {
			System.out.println("FileWriter could not be created.");
		}
	}
	
	/**
	 * Parse_klammer_filter. Uses a stack to parse the brackets-filter for
	 * example: ((Datatype, Begin, Value)&(Datatype, Begin, Value))|(Datatype,
	 * Begin, Value)
	 *
	 * @param bracketFilter the bracket filter
	 * @param messageType the message type
	 * @return the predicate
	 */
	public Predicate<CharSequence> parseBracketsFilter(String bracketFilter, int messageType) {
		//first step parsing the filter and push the elements on the stack:
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
					//second step, if an element for example (104,23,4)&(104,24,5)is found,
					Predicate<CharSequence> firstExpression = expressions
							.pop();
					Predicate<CharSequence> secondExpression = expressions
							.pop();
					String operator = operators.pop();
					//evaluate these two last expression and push the result on the stack
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
		//last step, evaluate the rest of elements on the stack
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
		//result is one predicate, that represents the whole filter
		return expressions.pop();
	}
	
	/**
	 * Convert.
	 *
	 * @param content the content
	 * @return the string
	 */
	public abstract String convert(byte[] content);
}
