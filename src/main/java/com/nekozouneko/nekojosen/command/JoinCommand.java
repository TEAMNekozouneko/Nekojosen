package com.nekozouneko.nekojosen.command;

import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class JoinCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cプレイヤーとして実行してください。");
                return true;
            }

            Player p = (Player) sender;

            Nekojosen.getInstance().getGame().join(p.getUniqueId());
            p.sendMessage("§aゲームに参加しました。");
        }
        else {
            if (!sender.hasPermission("nekojosen.command.join.other")) {
                sender.sendMessage("権限が不足しています: \"nekojosen.command.join.other\"");
                return true;
            }

            for (String arg : args) {
                Player p = Bukkit.getPlayer(arg);

                if (p == null) {
                    sender.sendMessage("§cE: \"" + arg + "\" というプレイヤーは見つかりませんでした。");
                    continue;
                }

                Nekojosen.getInstance().getGame().join(p.getUniqueId());
                p.sendMessage("§eW: ゲームに参加しました。");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tab = new ArrayList<>();
        if (sender.hasPermission("nekojosen.command.join.other")) {
            for (String n : Util.toPlayerNames(Bukkit.getOnlinePlayers())) {
                if (n.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    tab.add(n);
                }
            }
        }
        return tab;
    }
}
