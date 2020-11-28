package json.value.number;

import json.JSONValue;

public abstract class JSONNumber<N extends Number> extends JSONValue {
	private N value = null;
	private N defaultValue = null;
	@Override protected void init() {value = defaultValue = null;}
	
	public JSONNumber(final N defaultValue) {setDefault(defaultValue);setValue(defaultValue);}
	public JSONNumber(final boolean minimal) {super(minimal);}
	
	/**
	 * Sets the default value. Can be <code>null</code>
	 * to specify that there is no such default value.
	 */
	public void setDefault(final N defaultValue) {this.defaultValue = defaultValue;}
	/**@return The default value.*/
	public N defaultValue() {return defaultValue;}
	
	/**
	 * Sets the value. Can be <code>null</code> to specify
	 * the default value. If no default value exists, an empty
	 * value will throw an error when converted to string. 
	 */
	public void setValue(final N value) {this.value = value;}
	/**@return The value if present, or the default value. May be <code>null</code>.*/
	public N value() {return value == null? defaultValue() : value;}
	
	/**
	 * Gets the raw value. Will throw an error if the value
	 * and default value are empty.
	 */
	@Override protected String raw() {return value().toString();}

	@Override
	public boolean empty() {
		final N v = value();
		return v == null || v == defaultValue();
	}
}