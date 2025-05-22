package constants;

public enum TimerDbPropertyKey {
	TIMER_DB_NAME ("timer.db.name");

	private String key;
	
	private TimerDbPropertyKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}
