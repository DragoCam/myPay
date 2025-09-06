package net.nightzy.mypay;

import net.nightzy.mypay.commands.MyPayCommand;
import net.nightzy.mypay.utils.BalanceManager;
import net.nightzy.mypay.utils.MyPayEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class MyPay extends JavaPlugin implements Listener {

    private static MyPay instance;
    private BalanceManager balanceManager;

    @Override
    public void onEnable() {
        instance = this;

        // resources / config
        saveDefaultConfig();
        // ensure balances.yml exists in plugin folder (if present in jar it won't overwrite)
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        saveResource("balances.yml", false);

        // manager
        balanceManager = new BalanceManager();
        balanceManager.loadBalances();

        // command
        if (this.getCommand("mypay") != null) {
            this.getCommand("mypay").setExecutor(new MyPayCommand());
        }

        // events
        Bukkit.getPluginManager().registerEvents(this, this);

        // register as Vault economy provider if Vault present
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            MyPayEconomy economyProvider = new MyPayEconomy();
            Bukkit.getServicesManager().register(Economy.class, economyProvider, this, ServicePriority.Normal);
            getLogger().info("myPay registered as Vault Economy provider!");
        } else {
            getLogger().warning("Vault not found. myPay will still work standalone but will not be visible via Vault.");
        }

        getLogger().info("myPay has been enabled!");
    }

    @Override
    public void onDisable() {
        if (balanceManager != null) balanceManager.saveBalances();
        getLogger().info("myPay has been disabled!");
    }

    public static MyPay getInstance() {
        return instance;
    }

    public BalanceManager getBalanceManager() {
        return balanceManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (balanceManager.getBalance(uuid) == 0.0) {
            double starting = getConfig().getDouble("starting-balance", 100.0);
            balanceManager.setBalance(uuid, starting);
        }
    }
}
