package json.value;

import json.JSONValue;
import json.caps;

public class JSONString extends JSONValue {
	private static final caps CAP = caps.quote;
	
	private String value;
	@Override protected void init() {value = null;}
	
	public JSONString() {super();}
	public JSONString(final boolean minimal) {super(minimal);}
	public JSONString(final char[] value, final int start, final int length) {super();this.value = new String(value,start,length);}
	
	public caps cap() {return CAP;}

	@Override public String raw() {return value;}

	@Override public boolean empty() {return value == null || value.length() <= 0;}
}