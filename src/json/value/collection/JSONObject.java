package json.value.collection;

import java.util.TreeMap;

import json.JSONPair;
import json.JSONValue;
import json.caps;

public class JSONObject extends JSONValue implements Container {
	private static final caps CAP = caps.brace;
	
	private TreeMap<String,JSONPair> values;
	@Override protected void init() {values = new TreeMap<>();}
	
	public JSONObject() {super();}
	public JSONObject(final boolean minimal) {super(minimal);}
	
	@Override public boolean empty() {return values.isEmpty();}
	
	public void put(final JSONPair p) {if(p != null) values.put(p.getKey(),p);}
	@SuppressWarnings("unchecked")
	public <V extends JSONValue> V get(final String k) {
		final JSONPair p = values.get(k);
		if(p == null) return null;
		return (V) p.getValue();
	}
	
	@Override public caps cap() {return CAP;}
	
	@Override
	protected String raw() {
		final StringBuilder out = new StringBuilder();
		boolean notFirst = false;
		for(final JSONPair p : values.values()) {
			final String v = p.toString();
			if(v.length() > 0) {
				if(notFirst) out.append(',');
				else notFirst = true;
				out.append(v);
			}
		}
		return out.toString();
	}
	
	@Override
	public int prettyPrint(final StringBuilder out,final StringBuilder level,final boolean initial) {
		final caps c = cap();
		int l = 0;
		(initial? out.append(level) : out).append(c.open);
		if(!empty()) {
			final StringBuilder local = new StringBuilder(level).append('\t');
			if(values.size() == 1) {
				final JSONPair p = values.firstEntry().getValue();
				final String v = p.toString();
				if((l = v.length()) > NEWLINE_THRESHOLD) {l = p.prettyPrint(out,local);out.append(level);}
				else out.append(v);
			} else {
				boolean notFirst = false;
				for(final JSONPair p : values.values()) {
					if(notFirst) out.append(',');
					else notFirst = true;
					l += p.prettyPrint(out,local);
				}
				out.append(level);
			}
		}
		out.append(c.close);
		return l + 2;
	}
	
	public String[] getKeys() {return values.keySet().toArray(String[]::new);}
	public JSONPair[] getValues() {return values.values().toArray(JSONPair[]::new);}
}