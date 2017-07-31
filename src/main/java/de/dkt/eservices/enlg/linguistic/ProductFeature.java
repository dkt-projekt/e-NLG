package de.dkt.eservices.enlg.linguistic;

public class ProductFeature implements LinguisticFeature{

	public String name;
	public String value;
	
	public ProductFeature() {
		super();
	}
	
	public ProductFeature(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
