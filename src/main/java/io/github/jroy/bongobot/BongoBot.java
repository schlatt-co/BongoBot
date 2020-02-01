package io.github.jroy.bongobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class BongoBot {

  public static void main(String[] args) throws LoginException, InterruptedException {
    JDA jda = new JDABuilder(args[0])
        .setStatus(OnlineStatus.DO_NOT_DISTURB)
        .setActivity(Activity.listening("to the screams of purples"))
        .build();
    jda.awaitReady();
    Guild guild = jda.getGuildById("438337215584796692");
    assert guild != null;
    TextChannel log = guild.getTextChannelById("460082214689046538");
    TextChannel botsShouting = guild.getTextChannelById("594928459139252244");
    assert log != null;
    assert botsShouting != null;

    //What mode are we in?
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EST"));
    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && calendar.get(Calendar.HOUR) == 0 && calendar.get(Calendar.AM_PM) == Calendar.AM) {
      //Bongo Mode
      List<Member> purges = collectMembers(guild);
      log.sendMessage("yeah yeah whatup trevor here... bongo bot is bad so let's clean up the fuckin mess...").complete();
      StringBuilder builder = new StringBuilder();
      for (Member member : purges) {
        if (builder.length() > 1500) {
          botsShouting.sendMessage(builder.toString()).complete();
          builder = new StringBuilder();
        }
        builder.append(member.getUser().getName()).append("#").append(member.getUser().getDiscriminator()).append("\n");
        member.kick("Bongo purge").complete();
      }
      log.sendMessage("about " + purges.size() + " purples were just kicked...").complete();
      log.sendMessage("lmao bye bye").complete();
    } else {
      //Debug Mode
      System.out.println("Debug Mode");
      List<Member> purges = collectMembers(guild);
      botsShouting.sendMessage("Found " + purges.size() + " people who needs to up their pledge.").complete();
      StringBuilder builder = new StringBuilder();
      for (Member member : purges) {
        if (builder.length() > 1500) {
          botsShouting.sendMessage(builder.toString()).complete();
          builder = new StringBuilder();
        }
        builder.append(member.getAsMention()).append(" - ").append(member.getRoles().size()).append("\n");
      }
      builder.append("\n\n**better fucking resub or u guys about to get kicked on monday**");
      botsShouting.sendMessage(builder.toString()).complete();
    }
    jda.shutdown();
    System.exit(0);
  }

  private static List<Member> collectMembers(Guild guild) {
    List<Member> toPurge = new ArrayList<>();
    for (Member member : guild.getMembers()) {
      if (member.getUser().isBot()) {
        continue;
      }
      boolean purge = true;
      for (Role role : member.getRoles()) {
        if (!role.getName().startsWith("_")) {
          purge = false;
        }
      }
      if (purge) {
        toPurge.add(member);
      }
    }
    return toPurge;
  }
}
