package task;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class MonsterStrikeDays implements Runnable{
	private Guild guild = null;
	private String CHANNEL_ID = "";
	
	
	public MonsterStrikeDays(Guild guild, String channelId) {
		this.guild = guild;
		this.CHANNEL_ID = channelId;
	}

	@Override
	public void run() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor("Jam'sBot","https://x.com/monst_mixi","https://pbs.twimg.com/profile_images/1723906355701141504/7DJ8-Nn7_400x400.png");
		eb.setTitle("モンストの日です。");
		eb.setColor(0xff4500);
		eb.setThumbnail("https://pbs.twimg.com/profile_images/1723906355701141504/7DJ8-Nn7_400x400.png");
		eb.setFooter("Made by Jam");
		//メインチャンネルに送信
		guild.getTextChannelById(CHANNEL_ID).sendMessageEmbeds(eb.build()).queue();
	}

}
