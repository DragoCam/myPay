package net.nightzy.mypay.utils;

import net.nightzy.mypay.MyPay;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BalanceManager {

    private final Map<UUID, Double> balances = new HashMap<>();
    private final File file;
    private final YamlConfiguration config;

    public BalanceManager() {
        file = new File(MyPay.getInstance().getDataFolder(), "balances.yml");
        if (!file.exists()) {
            try {
                // create empty file so loadConfiguration doesn't throw later
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void loadBalances() {
        if (!file.exists()) return;
        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                double val = config.getDouble(key, 0.0);
                balances.put(uuid, val);
            } catch (IllegalArgumentException ignored) {
                // skip invalid keys
            }
        }
    }

    public void saveBalances() {
        for (Map.Entry<UUID, Double> e : balances.entrySet()) {
            config.set(e.getKey().toString(), e.getValue());
        }
        try {
            config.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, 0.0);
    }

    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, amount);
    }

    public void deposit(UUID uuid, double amount) {
        if (amount < 0) return;
        balances.put(uuid, getBalance(uuid) + amount);
    }

    public void withdraw(UUID uuid, double amount) {
        if (amount < 0) return;
        balances.put(uuid, getBalance(uuid) - amount);
    }
}
