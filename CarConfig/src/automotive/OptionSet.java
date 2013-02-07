package automotive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class OptionSet implements Serializable {

	private static final long serialVersionUID = -7562075447134713419L;
	private String name;
	private ArrayList<Option> options;

	public OptionSet() {
	}

	public OptionSet(String name) {
		this.name = name;
	}

	public OptionSet(String name, int count) {
		this.name = name;
		this.options = new ArrayList<Option>(count);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Option> getOptions() {
		return (ArrayList<Option>) Collections.unmodifiableList(this.options);
	}

	public void setOptions(ArrayList<Option> options) {
		this.options = options;
	}

	public void setOption(int i, String name, int price) {
		if (this.options == null) {
			this.options = new ArrayList<Option>();
		}
		if (i < this.options.size())
			this.options.set(i, new Option(name, price));
		else 
			this.options.add(i, new Option(name, price));
	}

	public Option getOption(String name) {
		int index = findOption(name);
		if (index == -1) {
			return null;
		} else {
			return this.options.get(index);
		}
	}

	public int getOptionPrice(String name) {
		int index = findOption(name);
		if (index == -1) {
			return 0;
		} else {
			return this.options.get(index).getPrice();
		}
	}

	public void printAllOptions() {
		for (Option op : this.options) {
			System.out.println(op.getName() + " : " + op.getPrice());
		}
	}

	private int findOption(String name) {
		if (name == null) {
			return -1;
		}
		if (this.options != null) {
			for (int i = 0; i < this.options.size(); i++) {
				if (this.options.get(i) != null
						&& this.options.get(i).getName().equals(name)) {
					return i;
				}
			}
			return 0;
		} else {
			return -1;
		}
	}

	private class Option implements Serializable {

		private static final long serialVersionUID = -5285313855089330316L;

		private String name;
		private int price;

		public Option(String name, int price) {
			this.name = name;
			this.price = price;
		}

		public String getName() {
			return this.name;
		}

		@SuppressWarnings("unused")
		public void setName(String name) {
			this.name = name;
		}

		public int getPrice() {
			return this.price;
		}

		@SuppressWarnings("unused")
		public void setPrice(int price) {
			this.price = price;
		}
	}
}
