package com.discord.Jamsbot.commandListener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class worldbosstimetable extends commandListenerAbstract {

	public worldbosstimetable(SlashCommandInteractionEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle("ボスの時間割り表示");
		embedBuilder.setDescription("下のボタンを押すと、応じた曜日の時間割が表示されます。");
		embedBuilder.setColor(0x120033);
		MessageEmbed initialEmbed = embedBuilder.build();

		Button today = Button.success("worldbosstoday", "今日");
		
		Button mon = Button.primary("worldbossmon", "月");
		Button tue = Button.primary("worldbosstue", "火");
		Button wed = Button.primary("worldbosswed", "水");
		Button thu = Button.primary("worldbossthu", "木");
		Button fri = Button.primary("worldbossfri", "金");
		Button sat = Button.primary("worldbosssat", "土");
		Button sun = Button.primary("worldbosssun", "日");

		event.replyEmbeds(initialEmbed)
				.addActionRow(today) // ボタンをActionRowに追加
				.addActionRow(mon,tue,wed,thu,fri) // ボタンをActionRowに追加
				.addActionRow(sat,sun) // ボタンをActionRowに追加
				.setEphemeral(true) // falseにすると他のユーザーにも見えます。trueだと実行者のみ。
				.queue();
	}

}
