package info.curtbinder.reefangel.phone;

public class TempSensor {

	private Number data;
	private String label;
	
	public TempSensor() {
		data = new Number((byte) 1);
		// TODO use strings.xml instead of hard code
		label = "T";
	}
	
	public void setTemp(int temp) {
		data.setValue(temp);
	}
	
	public String getTemp() {
		return data.toString();
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
