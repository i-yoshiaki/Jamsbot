package constants;

public enum BlackDesertBossTimerDbPropertyKey {
	BLACKDESERT_DB_NAME ("blackdesert.db.name"),
	BOSS_TABLE ("bosstimer.boss.table"),
	BOSS_TIME_TABLE ("bosstimer.boss.time.table"),
	TIMETABLE_TABLE ("bosstimer.timetable.table");

	private String key;
	
	private BlackDesertBossTimerDbPropertyKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}
