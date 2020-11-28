package json;

/**
 * An object that holds a JSON-formatted object.
 * 
 * @author prgmTrouble
 */
public abstract class JSONValue {
	protected abstract void init();
	
	private boolean minimal;
	
	/**Creates a minimal JSON value.*/
	public JSONValue() {init(); setMinimal(false);}
	public JSONValue(final boolean minimal) {init(); setMinimal(minimal);}
	
	public void setMinimal(final boolean minimal) {this.minimal = minimal;}
	public boolean minimal() {return minimal;}
	
	/**@return A character that appears before and after the value.*/
	public caps cap() {return caps.empty;}
	
	/**@return The unwrapped string value.*/
	protected abstract String raw();
	
	/**@return True if empty.*/
	public abstract boolean empty();
	
	/**
	 * Returns the JSON string. If this value is empty, not minimal, and has a wrapper, an empty
	 * value will be wrapped and returned. If it is empty and either minimal or does not have a
	 * wrapper, an empty value will be returned. If it is not empty, the value will be wrapped
	 * and returned.
	 */
	@Override
	public String toString() {
		final caps cap = cap();
		return empty()? cap != caps.empty && !minimal()? cap.wrap("") : "" : cap.wrap(raw());
	}
	
	/**Returns the JSON string with line breaks and indentation.*/
	public String prettyPrint() {
		final StringBuilder out = new StringBuilder();
		prettyPrint(out,new StringBuilder("\n"),false);
		return out.toString();
	}
	public int prettyPrint(final StringBuilder out,final StringBuilder level,final boolean initial) {
		final String s = toString();
		(initial? out.append(level) : out).append(s);
		return s.length();
	}
}