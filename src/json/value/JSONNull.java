package json.value;

import json.JSONValue;

public class JSONNull extends JSONValue {
	@Override protected void init() {}
	public JSONNull() {super();}
	
	@Override protected String raw() {return "null";}
	@Override public boolean empty() {return false;}
}