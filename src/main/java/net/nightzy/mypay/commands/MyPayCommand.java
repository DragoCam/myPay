package net.nightzy.mypay.commands;

import net.nightzy.mypay.MyPay;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class MyPayCommand implements CommandExecutor {

    private static final DecimalFormat DF = new DecimalFormat("#0.00");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
    sender.sendMessage("§cTa komenda jest tylko dla graczy!");
    return true;
}
Player player = (Player) sender;

        double balance = MyPay.getInstance().getBalanceManager().getBalance(player.getUniqueId());
        player.sendMessage("§aYour balance: §e" + DF.format(balance) + " Coins");
        return true;
    }
}
