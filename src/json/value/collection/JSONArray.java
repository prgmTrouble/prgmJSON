package json.value.collection;

import java.util.ArrayList;

import json.JSONValue;
import json.caps;

public class JSONArray extends JSONValue implements Container {
	private static final caps CAP = caps.bracket;
	
	private ArrayList<JSONValue> values;
	@Override protected void init() {values = new ArrayList<>();}
	
	public JSONArray() {super();}
	public JSONArray(final boolean minimal) {super(minimal);}
	
	@Override public boolean empty() {return values.isEmpty();}
	
	public <V extends JSONValue> void put(final V v) {if(v != null) values.add(v);}
	@SuppressWarnings("unchecked")
	public <V extends JSONValue> V get(final int i) {
		if(i < 0 || i >= values.size()) throw new IndexOutOfBoundsException(i+" not in [0,"+values.size()+')');
		return (V) values.get(i);
	}
	
	@Override public caps cap() {return CAP;}
	
	@Override
	protected String raw() {
		final StringBuilder out = new StringBuilder();
		boolean notFirst = false;
		for(final JSONValue v : values) {
			if(!v.empty()) {
				if(notFirst) out.append(',');
				else notFirst = true;
				out.append(v);
			}
		}
		return out.toString();
	}
	
	@Override
	public String prettyPrint() {
		final caps c = cap();
		final StringBuilder out = new StringBuilder(c.open);
		boolean notFirst = false;
		for(final JSONValue p : values) {
			final String v = p.prettyPrint();
			if(v.length() > 0) {
				if(notFirst) out.append(',');
				else notFirst = true;
				out.append('\n').append(v);
			}
		}
		return out.append(c.close).toString();
	}
	
	@Override
	public int prettyPrint(final StringBuilder out,final StringBuilder level,final boolean initial) {
		final caps c = cap();
		int l = 0;
		(initial? out.append(level) : out).append(c.open);
		if(!empty()) {
			final StringBuilder local = new StringBuilder(level).append('\t');
			if(values.size() == 1) {
				final String v = values.get(0).toString();
				if((l = v.length()) > NEWLINE_THRESHOLD) {l = values.get(0).prettyPrint(out,local,true); out.append(level);}
				else out.append(v);
			} else {
				boolean notFirst = false;
				for(final JSONValue p : values) {
					if(notFirst) out.append(',');
					else notFirst = true;
					l += p.prettyPrint(out,local,true);
				}
				out.append(level);
			}
		}
		out.append(c.close);
		return l + 2;
	}
}