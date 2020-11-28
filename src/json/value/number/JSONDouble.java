package json.value.number;

public class JSONDouble extends JSONNumber<Double> {
	public JSONDouble() {super(0.0);}
	public JSONDouble(final boolean minimal) {super(minimal);}
	public JSONDouble(final double initial) {super(0.0);setValue(initial);}
	public JSONDouble(final double initial,final double defaultValue) {super(defaultValue);setValue(initial);}
}