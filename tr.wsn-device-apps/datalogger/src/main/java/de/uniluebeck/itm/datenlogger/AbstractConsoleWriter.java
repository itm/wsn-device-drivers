package de.uniluebeck.itm.datenlogger;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.or;

import java.io.IOException;
import java.util.Stack;
import java.util.regex.Pattern;

import com.google.common.base.Predicate;

public abstract class AbstractConsoleWriter implements PausableWriter{

	private boolean isPaused = false;
	
	private String regexFilter = ".*";
	private String bracketFilter;
	
	@Override
	public void write(byte[] content) {
		final String output = convert(content);
		Pattern pattern = Pattern.compile(regexFilter);
		if (!isPaused && pattern.matcher(output).matches()) {
			if(bracketFilter != null){
				if(parse_brackets_filter(bracketFilter).apply(output)){
					System.out.println(output);
				}
			}else{
				System.out.println(output);
			}
		}
	}

	@Override
	public void pause() {
		isPaused = true;
	}

	@Override
	public void resume() {
		isPaused = false;
	}

	@Override
	public void setRegexFilter(String regexFilter) {
		this.regexFilter = regexFilter;
	}
	
	@Override
	public void setBracketFilter(String bracketFilter) {
		this.bracketFilter = bracketFilter;
	}
	
	@Override
	public void addBracketFilter(String filter) {
		bracketFilter = bracketFilter + filter;
		System.out.println("Filter added");
	}

	@Override
	public void addRegexFilter(String filter) {
		regexFilter = regexFilter + filter;
		System.out.println("Filter added");
	}

	@Override
	public void close() throws IOException {
		
	}
	
	@Override
	public void setLocation(String location){
		
	}
	
	/**
	 * Parse_klammer_filter. Uses a stack to parse the brackets-filter for
	 * example: ((Datatype, Begin, Value)&(Datatype, Begin, Value))|(Datatype,
	 * Begin, Value)
	 * 
	 * @param klammer_filter
	 *            the klammer_filter
	 * @return the predicate
	 */
	public Predicate<CharSequence> parse_brackets_filter(String bracket_filter) {
		Stack<Predicate<CharSequence>> expressions = new Stack<Predicate<CharSequence>>();
		Stack<String> operators = new Stack<String>();
		String expression = "";
		for (int i = 0; i < bracket_filter.length(); i++) {
			String character = Character.toString(bracket_filter.charAt(i));
			if (character.equals("|")) {
				operators.push("or");
			} else if (character.equals("&")) {
				operators.push("and");
			} else if (character.equals("(")) {
				// do nothing
			} else if (character.equals(")")) {
				if (!expression.equals("")) {
					Predicate<CharSequence> predicate = new Brackets_Predicate(
							expression);
					expressions.push(predicate);
					expression = "";
				} else {
					Predicate<CharSequence> first_expression = expressions
							.pop();
					Predicate<CharSequence> second_expression = expressions
							.pop();
					String operator = operators.pop();
					if (operator.equals("or")) {
						Predicate<CharSequence> result = or(first_expression,
								second_expression);
						expressions.push(result);
					} else if (operator.equals("and")) {
						Predicate<CharSequence> result = and(first_expression,
								second_expression);
						expressions.push(result);
					}
				}
			} else {
				expression = expression + character;
			}
		}
		while (operators.size() != 0) {
			Predicate<CharSequence> first_expression = expressions.pop();
			Predicate<CharSequence> second_expression = expressions.pop();
			String operator = operators.pop();
			if (operator.equals("or")) {
				Predicate<CharSequence> result = or(first_expression,
						second_expression);
				expressions.push(result);
			} else if (operator.equals("and")) {
				Predicate<CharSequence> result = and(first_expression,
						second_expression);
				expressions.push(result);
			}
		}
		return expressions.pop();
	}
	
	public abstract String convert(byte[] content);
}
