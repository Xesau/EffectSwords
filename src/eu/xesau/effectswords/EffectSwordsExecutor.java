package eu.xesau.effectswords;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectSwordsExecutor implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		PotionEffectType type;
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("remove")) {
				if (!sender.hasPermission("effectswords.remove")) {
					sender.sendMessage(ChatColor.RED + "You cannot use this command.");
					return true;
				}
				
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only players can add effects to their items.");
					return true;
				}

				Player p = (Player) sender;
				if (args.length > 1) {
					type = PotionEffectType.getByName(args[1]);
					if (type != null) {
						ItemStack holding = p.getItemInHand();
						ItemStack e = EffectSwordsPlugin.removeEffectFrom(holding, type);
						if (e != null) {
							p.setItemInHand(e);
							p.sendMessage(ChatColor.GREEN + "The effect was removed from the item.");
						} else {
							p.sendMessage(ChatColor.RED + "This effect could not be removed from the item.");
						}
					} else
						p.sendMessage(ChatColor.RED + "This potion effect type does not exist.\nUse " + ChatColor.ITALIC
								+ "/es list" + ChatColor.RED + " to get a list of valid types.");
				} else {
					p.sendMessage(ChatColor.RED + "Usage: /sm remove <effect>");
					return true;
				}

			}

			else if (args[0].equalsIgnoreCase("list")) {
				sender.sendMessage(ChatColor.BOLD + "Available potion types:");
				for (PotionEffectType listType : PotionEffectType.values()) {
					if (listType != null && EffectSwordsPlugin.canBuy(listType))
						sender.sendMessage(
								ChatColor.GRAY + "- " + ChatColor.RESET + WordUtils.capitalizeFully(listType.getName())
										+ ChatColor.GRAY + " ($" + EffectSwordsPlugin.getPrice(listType) + ")");
				}
			}

			else {
				if (!sender.hasPermission("effectswords.add")) {
					sender.sendMessage(ChatColor.RED + "You cannot use this command.");
					return true;
				}
				
				if (!EffectSwordsPlugin.canBuy() && !sender.hasPermission("effectswords.free")) {
					sender.sendMessage(ChatColor.RED + "You can't buy this effect.");
					return true;
				}
				
				if ((type = PotionEffectType.getByName(args[0])) != null
					&& EffectSwordsPlugin.canBuy(PotionEffectType.getByName(args[0]))) {
					if (sender instanceof Player) {
						int ampl = 1;
						if (args.length > 1) {
							try {
								int parse = Integer.parseInt(args[1]);
								ampl = parse;
							} catch (Exception ex) {
								sender.sendMessage(ChatColor.RED + args[1] + " is not a number");
								return true;
							}
						}
	
						Player p = (Player) sender;
						if (p.getItemInHand() != null || p.getItemInHand().getType() == Material.AIR) {
							if (EffectSwordsPlugin.isAllowed(p.getItemInHand().getType())) {
								
								List<PotionEffect> existingEffects = EffectSwordsPlugin.getEffectsFrom(p.getItemInHand());
								for(PotionEffect eff : existingEffects) {
									if (eff.getType() == type)  {
										sender.sendMessage(ChatColor.RED + "This effect is already on this item.");
										return true;
									}
								}
								
								int duration = new Random().nextInt(10) + 5;
	
								Double price = EffectSwordsPlugin.getPrice(type)
										* EffectSwordsPlugin.getAmplifierPrice(ampl);
	
								if (p.hasPermission("effectswords.free") || EffectSwordsPlugin.econ.has(p, price)) {
									if (!p.hasPermission("effectswords.free"))
										EffectSwordsPlugin.econ.withdrawPlayer(p, price);
	
									ItemStack s = EffectSwordsPlugin.applyEffect(p.getItemInHand(), new PotionEffect(type, duration, ampl));
									p.setItemInHand(s);
								}
							} else {
								sender.sendMessage(ChatColor.RED + "Invalid item.");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Please hold an item.");
						}
	
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can add effects to their items.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "This is not a value potion type. ");
					sender.sendMessage(ChatColor.RED + "Use " + ChatColor.ITALIC + "/es list" + ChatColor.RED
							+ " to get a list of valid types.");
				}
			}
			return true;
		}

		return false;
	}

}
