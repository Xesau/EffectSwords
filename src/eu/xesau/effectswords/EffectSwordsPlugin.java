package eu.xesau.effectswords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.milkbowl.vault.economy.Economy;

public class EffectSwordsPlugin extends JavaPlugin {

	private static ArrayList<Material> allowedTypes = new ArrayList<Material>();
	private static HashMap<PotionEffectType, Double> pricing = new HashMap<PotionEffectType, Double>();
	private static boolean buyable = false;
	private static double amplifierPrice = 10.40;

	public static Economy econ = null;

	public void onEnable() {
		loadConfig(false);

		if(buyable && !setupEconomy()) {
			getLogger().severe("Vault is necessary for buyable Sword Effects. Disabling the plugin.");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		getServer().getPluginManager().registerEvents(new EffectSwordsListener(), this);
		getCommand("effectswords").setExecutor(new EffectSwordsExecutor());

	}

	public void onDisable() {
		allowedTypes = null;
	}

	public void loadConfig(boolean reload) {
		if (reload) {
			reloadConfig();
		} else {
			saveDefaultConfig();
		}

		buyable = getConfig().getBoolean("buyable");
		amplifierPrice = getConfig().getDouble("amplifierPrice");

		for (String materialName : getConfig().getStringList("allowedMaterials")) {
			allowedTypes.add(Material.getMaterial(materialName));
		}

		for (String effectName : getConfig().getConfigurationSection("pricing").getKeys(false)) {
			pricing.put(PotionEffectType.getByName(effectName), getConfig().getDouble("pricing." + effectName));
		}
	}

	public static boolean canBuy() {
		return buyable;
	}

	public static boolean canBuy(PotionEffectType effect) {
		return pricing.containsKey(effect);
	}

	public static Double getPrice(PotionEffectType type) {
		return (pricing.containsKey(type) ? pricing.get(type) : -1.00);
	}

	public static boolean isAllowed(Material type) {
		return allowedTypes.contains(type);
	}

	public static ArrayList<PotionEffect> getEffectsFrom(ItemStack item) {
		ArrayList<PotionEffect> output = new ArrayList<PotionEffect>();
		if (item.getItemMeta() == null || item.getItemMeta().getLore() == null)
			return output;
		
		int index = -1;
		List<String> lore = item.getItemMeta().getLore();
		for (int i = 0; i <= lore.size() && index == -1; i++) {
			if (lore.get(i).equals("§0§r§8Effects:"))
				index = i + 1;
		}
		for (int i = index; i <= lore.size() && index != -1; i++) {
			if (!lore.get(i).startsWith("§0§r§8- §7"))
				index = -1;
			else {
				// Parse the lore (substring 10), (split ' '), (last one for duration, second
				// last one for amplifier)
				String[] splitParams = lore.get(i).substring(10).split("\\s+");
				String durationParam = splitParams[splitParams.length - 1];
				String amplifierParam = splitParams[splitParams.length - 2];

				durationParam = durationParam.replace("(", "").replace(")", "");

				String[] durationSplit = durationParam.split(":");
				int duration = Integer.parseInt(durationSplit[1]) + (60 * Integer.parseInt(durationSplit[0]));
				int amplifier = RomanNumeral.romanToDecimal(amplifierParam);

				output.add(new PotionEffect(PotionEffectType.getByName(splitParams[0]), duration * 20, amplifier, true, true));
			}
		}
		return output;
	}

	public static ItemStack removeEffectFrom(ItemStack item, PotionEffectType effect) {
		int index = -1;
		List<String> lore = item.getItemMeta().getLore();
		for (int i = 0; i <= lore.size() && index == -1; i++) {
			if (lore.get(i).equals("§0§r§8Effects:"))
				index = i + 1;
		}
		for (int i = index; i <= lore.size() && index != -1; i++) {
			if (!lore.get(i).startsWith("§0§r§8- §7"))
				index = -1;
			else {
				if (lore.get(i).startsWith("§0§r§8- §7" + WordUtils.capitalizeFully(effect.getName()) + " ")) {
					lore.remove(i);
					ItemMeta m = item.getItemMeta();
					m.setLore(lore);
					item.setItemMeta(m);
					return item;
				}
			}
		}
		return null;
	}

	public static ItemStack applyEffect(ItemStack item, PotionEffect effect) {
		int effectIndex = -1;
		List<String> lore = item.getItemMeta().getLore();

		int counter = 0;

		if (lore != null) {
			for (String line : lore) {
				if (line.equals("§0§r§8Effects:")) {
					effectIndex = counter;
					break;
				}
				counter++;
			}
		}

		if (effectIndex > -1) {
			lore.add(effectIndex + 1,
					"§0§r§8- §7" + WordUtils.capitalizeFully(effect.getType().getName()) + " "
							+ RomanNumeral.arabicToRoman(effect.getAmplifier()) + " ("
							+ secondsToTime(effect.getDuration()) + ")");
		} else {
			if (lore == null)
				lore = new ArrayList<String>();
			lore.add("");

			lore.add("§0§r§8Effects:");
			lore.add("§0§r§8- §7" + WordUtils.capitalizeFully(effect.getType().getName()) + " "
					+ RomanNumeral.arabicToRoman(effect.getAmplifier()) + " (" + secondsToTime(effect.getDuration())
					+ ")");
			lore.add("");
		}

		ItemMeta meta = item.getItemMeta();

		meta.setLore(lore);

		item.setItemMeta(meta);

		return item;
	}

	public static boolean hasEffect(ItemStack item, PotionEffectType type) {
		for (PotionEffect effect : getEffectsFrom(item)) {
			if (effect.getType().equals(type))
				return true;
		}
		return false;
	}

	public static String secondsToTime(int seconds) {
		seconds = seconds % 60;
		return (new Double(seconds / 60).intValue() + ":" + (seconds < 10 ? "0" + seconds : seconds));
	}

	public static Double getAmplifierPrice(int amplifier) {
		amplifier--;
		if (amplifier < 1) return 0.0;
		return amplifierPrice * amplifier;
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

}
