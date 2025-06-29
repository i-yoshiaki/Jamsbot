package constants;

public enum BotConnectionPropertyKey {
	TOKEN("bot.token"), 
	GUILD_ID("asobiba.guild.id"),
	ADMIN_CHANNEL_ID("asobiba.admin.channel.id"),
	MAIN_CHANNEL_ID("asobiba.main.channel.id"),
	MONSTERSTRIKE_CHANNEL_ID("asobiba.monsterstrike.channel.id"),
	BLACKDESERT_CHANNEL_ID("asobiba.blackdesert.channel.id"),
	SERVER_BATCHFILE_PATH("server.batchfile.path");

	private String key;

	private BotConnectionPropertyKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
