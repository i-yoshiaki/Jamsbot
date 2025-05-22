package constants;

public enum DatabasePropertyKey {
	
	USER ("database.username"),
	PASS ("database.password");

	private String key;
	
	private DatabasePropertyKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}
