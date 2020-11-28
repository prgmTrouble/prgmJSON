package json;

/**Special characters which surround JSON values.*/
public enum caps {
	empty,quote('"','"'),brace('{','}'),bracket('[',']');
	
	public final char open,close;
	private caps() {open = close = 0;}
	private caps(final char o,final char c) {open = o; close = c;}
	
	public String wrap(final String in) {return open > 0? open + (in == null? "" : in) + close : in;}
	public String strip(final String in) {
		if(open == 0 || in == null || in.length() <= 0 || in.charAt(0) != open || in.charAt(in.length() - 1) != close) return in;
		return in.substring(1,in.length()-1);
	}
	
	public String regexWrap(final String in) {
		if(open == 0) return in;
		final StringBuilder out = new StringBuilder(in).insert(0,open);
		if(open != '"') out.insert(0,'\\').append('\\');
		return out.append(close).toString();
	}
	
	public static final char[] getOpen() {
		final caps[] cap = values();
		final int l = cap.length - 2;
		final char[] out = new char[l];
		for(int i = 0; i < l; out[i] = cap[++i+1].open);
		return out;
	}
	public static final char[] getClose() {
		final caps[] cap = values();
		final int l = cap.length - 2;
		final char[] out = new char[l];
		for(int i = 0; i < l; out[i] = cap[++i+1].close);
		return out;
	}
}