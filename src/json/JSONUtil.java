package json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import json.value.JSONBool;
import json.value.JSONString;
import json.value.collection.JSONArray;
import json.value.collection.JSONObject;
import json.value.number.JSONDouble;
import json.value.number.JSONInteger;

public final class JSONUtil {
	private static final boolean isOpen(final char in) {return in == '{' || in == '[';}
	private static final boolean isClose(final char in) {return in == '}' || in == ']';}
	private static final char close(final char in) {return in == '{'? '}' : ']';}
	
	/**
	 * @param in
	 * @param start The first character. This should be a quotation mark ('"').
	 * @return The index of the matching quotation mark.
	 */
	private static final int endquote(final char[] in,int start) {
		boolean escaped = false;
		while(++start < in.length) {
			if(escaped) escaped = false;
			else if(!(escaped = in[start] == '\\') && in[start] == '"') return start;
		}
		throw new IllegalArgumentException("Could not find closing quote.");
	}
	
	private static final class charnode {
		protected final char v;
		protected final charnode n;
		protected charnode(final char v,final charnode n) {this.v = v;this.n = n;}
	}
	
	/**
	 * @param in
	 * @param start The first character. This should be an opening character
	 *              ('{' or '[').
	 * @return The index of the structure's closing character ('}' or ']').
	 */
	private static final int endstruct(final char[] in,int start) {
		charnode n = new charnode(close(in[start]),null);
		boolean escaped = false;
		while(++start < in.length) {
			if(escaped) escaped = false;
			else {
				final char c = in[start];
				if(!(escaped = c == '\\')) {
					if(c == '"') start = endquote(in,start);
					else if(isOpen(c)) n = new charnode(close(c),n);
					else if(isClose(c)) {
						if(c != n.v) throw new IllegalArgumentException("Mismatched: '"+c+"'!='"+n.v+'\'');
						if((n = n.n) == null) return start;
					}
				}
			}
		}
		throw new IllegalArgumentException("Could not find: '"+n.v+'\'');
	}
	
	private static final class intnode {
		protected final int v;
		protected final intnode n;
		protected intnode(final int v,final intnode n) {this.v = v;this.n = n;}
	}
	
	/**
	 * @param in
	 * @param start The first character of the structure. This must be
	 *              an opening character ('{' or '[') so that the end
	 *              can be found.
	 * @return An array of indices which represent the first character
	 *         of the structure, any commas between structures, and
	 *         the last character of the structure.
	 */
	private static final int[] commas(final char[] in,int start) {
		if(start >= in.length) throw new IllegalArgumentException("Invalid starting position.");
		intnode n = new intnode(start,null);
		int ct = 1;
		final char close = close(in[start]);
		boolean escaped = false;
		while(++start < in.length) {
			if(escaped) escaped = false;
			else {
				final char c = in[start];
				if(!(escaped = c == '\\')) {
					if(c == close) {
						final int[] out = new int[1+ct];
						out[ct] = start;
						do out[--ct] = n.v;
						while((n = n.n) != null);
						return out;
					}
					if(c == ',') {++ct; n = new intnode(start,n);}
					else if(isOpen(c)) start = endstruct(in,start);
					else if(c == '"') start = endquote(in,start);
				}
			}
		}
		throw new IllegalArgumentException("Could not find: '"+close+'\'');
	}
	
	/**
	 * @param in
	 * @param start The first character. This character will not be counted as the first
	 *              non-whitespace character.
	 * @return The index of the first non-whitespace character after <code>start</code>.
	 */
	private static final int stripLeading(final char[] in,int start) {
		if(++start >= in.length) return in.length;
		do if(!Character.isWhitespace(in[start])) return start;
		while(++start < in.length);
		return start;
	}
	/**
	 * @param in
	 * @param start The last character. This character will not be counted as the last
	 *              non-whitespace character.
	 * @return The index of the last non-whitespace character before <code>start</code>.
	 */
	private static final int stripTailing(final char[] in,int start) {
		if(--start <= 0) return 0;
		do if(!Character.isWhitespace(in[start])) return start;
		while(--start > 0);
		return start;
	}
	/**
	 * @param in
	 * @param start The first character. This character will be skipped.
	 * @param end The last character. This character will be skipped.
	 * @return The sequence between <code>start</code> and <code>end</code> (exclusive) without whitespace.
	 */
	private static final char[] strip(final char[] in,int start,int end) {
		final char[] out = new char[end = stripTailing(in,end) - (start = stripLeading(in,start)) + 1];
		System.arraycopy(in,start,out,0,end);
		return out;
	}
	
	/**
	 * @param in
	 * @param start The first character. This should be an opening character ('{'
	 *              or '[').
	 * @return An array of sequences split across commas and stripped of whitespace.
	 */
	private static final char[][] stripCommas(final char[] in,final int start) {
		final int[] split = commas(in,start);
		final char[][] out = new char[split.length-1][];
		for(int i = 0; i < out.length; out[i] = strip(in,split[i],split[++i]));
		return out;
	}
	
	/**
	 * @param in There must be no leading or trailing whitespace.
	 * @return A {@linkplain JSONPair}.
	 */
	private static final JSONPair inferPair(final char[] in) {
		if(in.length == 0 || in[0] != '"') throw new IllegalArgumentException("No Key.");
		int i = endquote(in,0);
		final JSONString key = new JSONString(in,1,i-1);
		
		while(++i < in.length && in[i] != ':');
		if(i == in.length) throw new IllegalArgumentException("No colon.");
		while(Character.isWhitespace(in[++i]) && i < in.length);
		if(i == in.length) throw new IllegalArgumentException("No value.");
		
		final int l = in.length - i;
		final char[] out = new char[l];
		System.arraycopy(in,i,out,0,l);
		
		return new JSONPair(key,infer(out));
	}
	
	@SuppressWarnings("unchecked")
	public static final <V extends JSONValue> V infer(final char[] in) {
		final char c = in[0];
		if(c == '{' || c == '[') {
			final char[][] strip = stripCommas(in,0);
			if(c == '{') {
				final JSONObject out = new JSONObject();
				for(final char[] s : strip) out.put(inferPair(s));
				return (V) out;
			}
			final JSONArray out = new JSONArray();
			for(final char[] s : strip) out.put(infer(s));
			return (V) out;
		} else if(c == '"') return (V) new JSONString(in,1,endquote(in,0)-1);
		else if(c == 't' || c == 'f') {
			final String s = new String(in,0,in.length);
			final boolean t = s.equals("true");
			if(!(t || s.equals("false"))) throw new IllegalArgumentException("Invalid boolean.");
			return (V) new JSONBool(t);
		} else if(c == '-' || c == '+' || c >= '0' || c <= '9') {
			int i = 0;
			while(++i < in.length && in[i] >= '0' && in[i] <= '9');
			final String s = new String(in,0,in.length);
			return (V)(i<in.length&&(in[i]=='.'||in[i]=='e'||in[i]=='E')?new JSONDouble(Double.parseDouble(s)):new JSONInteger(Integer.parseInt(s)));
		}
		throw new IllegalArgumentException("Invalid.");
	}
	
	public static final JSONObject fromFile(final Path f) {
		final StringBuilder b = new StringBuilder();
		try {for(final String s : Files.readAllLines(f)) b.append(s);}
		catch (final IOException e) {e.printStackTrace();}
		return infer(b.toString().toCharArray());
	}
}