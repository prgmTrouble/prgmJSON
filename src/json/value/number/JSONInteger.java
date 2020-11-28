package json.value.number;

public class JSONInteger extends JSONNumber<Integer> {
	public JSONInteger() {super(0);}
	public JSONInteger(final boolean minimal) {super(minimal);}
	public JSONInteger(final int initial) {super(0);setValue(initial);}
	public JSONInteger(final int initial,final int defaultValue) {super(defaultValue);setValue(initial);}
}