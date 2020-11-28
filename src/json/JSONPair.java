package json;

import json.value.JSONString;

public class JSONPair {
	private final JSONString key;
	private JSONValue value;
	private boolean minimal = true;
	
	public <V extends JSONValue> JSONPair(final JSONString key,final V value) {
		if(key == null || key.empty()) throw new NullPointerException("Cannot have empty key.");
		this.key = key; setValue(value);
	}
	
	@SuppressWarnings("unchecked")
	public <V extends JSONValue> V getValue() {return (V) value;}
	public <V extends JSONValue> void setValue(final V value) {
		if(value == null) throw new NullPointerException("Cannot have null value.");
		this.value = value;
		this.value.setMinimal(minimal);
	}
	
	public boolean empty() {return value.empty();}
	
	public void setMinimal(final boolean minimal) {this.value.setMinimal(this.minimal = minimal);}
	public boolean minimal() {return minimal;}
	
	public String getKey() {return key.raw();}
	
	@Override
	public String toString() {
		final String v = value.toString();
		return v.length() > 0? new StringBuilder().append(key).append(':').append(v).toString() : "";
	}
	
	public int prettyPrint(final StringBuilder out,final StringBuilder level) {
		final StringBuilder v = new StringBuilder();
		final int i = value.prettyPrint(v,level,false);
		if(v.length() > 0) out.append(level).append(key).append(':').append(v);
		return i;
	}
	public String prettyPrint() {
		final String v = value.prettyPrint();
		return v.length() > 0? new StringBuilder().append(key).append(':').append(v).toString() : "";
	}
}