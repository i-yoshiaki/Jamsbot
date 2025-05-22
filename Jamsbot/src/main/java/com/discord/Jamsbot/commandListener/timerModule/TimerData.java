package com.discord.Jamsbot.commandListener.timerModule;

import java.time.LocalDateTime;

public record TimerData(long id, String userId, String channelId, LocalDateTime triggerTime) {
}