package json.value;

import json.JSONValue;

public class JSONBool extends JSONValue {
	private Boolean value,defaultValue;
	@Override protected void init() {value = defaultValue = null;}
	
	public JSONBool() {super();}
	public JSONBool(final boolean value) {super();this.value = value;}
	
	public void setDefault(final boolean defaultValue) {this.defaultValue = defaultValue;}
	public Boolean defaultValue() {return defaultValue;}
	public void setValue(final boolean value) {this.value = value;}
	public Boolean value() {return value == null? defaultValue() : value;}
	
	@Override protected String raw() {return value().toString();}
	
	@Override
	public boolean empty() {
		final Boolean v = value();
		return v == null || v == defaultValue();
	}
}