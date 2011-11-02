package info.curtbinder.reefangel.phone;

public class Host {
	private String host;
	private int port;
	private String command;
	private int timeoutConnect;
	private int timeoutRead;
	private String raUserid;
	private final String RAHOST = "http://www.reefangel.com/status/xml.aspx?id=";
	
	// for memory reading/writing
	private int location;
	private int value;
	private boolean write;
	
	Host(String userid) {
		setDefaults("", 80, Globals.requestReefAngel);
		raUserid = userid;
	}
	
	Host(String host, int port, String command) {
		setDefaults(host, port, command);
	}
	
	Host(String host, String port, String command) {
		setDefaults(host, Integer.parseInt(port), command);
	}
	
	private void setDefaults(String host, int port, String command) {
		this.host = host;
		this.port = port;
		this.command = command;
		timeoutConnect = 15000;  // milliseconds
		timeoutRead = 10000;  // milliseconds
		location = 0;
		value = 0;
		write = false;
		raUserid = "";
	}
	
	public int getConnectTimeout() {
		return timeoutConnect;
	}
	
	public int getReadTimeout() {
		return timeoutRead;
	}
	
	public void setReadLocation(int location) {
		this.location = location;
		this.value = 0;
		this.write = false;
	}
	
	public void setWriteLocation(int location, int value) {
		this.location = location;
		this.value = value;
		this.write = true;
	}
	
	public String toString() {
		String s = "";
		if ( (command.equals( Globals.requestStatusOld )) ||
			 (command.equals( Globals.requestStatus )) || 
			 (command.equals( Globals.requestVersion)) ) {
			s = new String(String.format("http://%s:%d%s", host, port, command));
		} else if ( (command.equals( Globals.requestMemoryInt )) ||
					(command.equals( Globals.requestMemoryByte)) ) {
			if ( write ) {
				s = new String(String.format("http://%s:%d%s%d,%d", 
						host, port, command, location, value));
			} else {
				s = new String(String.format("http://%s:%d%s%d", 
						host, port, command, location));
			}
		} else if ( command.equals( Globals.requestReefAngel ) ) {
			s = RAHOST + raUserid;
		}
		return s;
	}
}
