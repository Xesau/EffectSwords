package eu.xesau.effectswords;

import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectSwordsExecutor implements CommandExecutor {

	public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args )
	{
		if( args.length > 0 )
		{
			if( args[0].equalsIgnoreCase( "remove" ) )
			{
				if( args.length > 1 )
				{
					
				}
			}
			
			else if ( args[0].equalsIgnoreCase( "list" ) )
			{
				sender.sendMessage( ChatColor.BOLD + "Available potion types:" );
				for( PotionEffectType type : PotionEffectType.values() )
				{
					if ( type != null && EffectSwordsPlugin.canBuy( type ) )
						sender.sendMessage( ChatColor.GRAY + "- " + ChatColor.RESET + WordUtils.capitalizeFully( type.getName() ) + ChatColor.GRAY + " ($" + EffectSwordsPlugin.getPrice( type ) + ")" );
				}
			}
			
			else if( PotionEffectType.getByName( args[0] ) != null && EffectSwordsPlugin.canBuy( PotionEffectType.getByName( args[0] ) ) )
			{
				if( sender instanceof Player )
				{
					if( args.length > 1 )
					{
						Player p = (Player) sender;
						if( p.getItemInHand() != null || p.getItemInHand().getType() == Material.AIR )
						{
							if( EffectSwordsPlugin.isAllowed( p.getItemInHand().getType() ) )
							{
								PotionEffectType type = PotionEffectType.getByName( args[0] );
								int amplifier = Integer.parseInt( args[1] );
								int duration = new Random().nextInt( ( 45 - 15 ) + 1) + 15;
								
								Double price = EffectSwordsPlugin.getPrice( type ) * EffectSwordsPlugin.getAmplifierPrice( amplifier );
								
								if( p.hasPermission( "effectswords.free" ) || EffectSwordsPlugin.econ.has( p, price ) )
								{
									if( !p.hasPermission( "effectswords.free" ) )
										EffectSwordsPlugin.econ.withdrawPlayer( p, price );
									
									p.setItemInHand( EffectSwordsPlugin.applyEffect( p.getItemInHand(), new PotionEffect( type, duration, amplifier ) ) );
								}
							}
							else
							{
								sender.sendMessage( ChatColor.RED + "Invalid item.");
							}
						}
						else
						{
							sender.sendMessage( ChatColor.RED + "Please hold an item.");
						}
					}
					else
					{
						return false;
					}
				}
				else
				{
					sender.sendMessage( ChatColor.RED + "Only players can add effects to their items." );
				}
			}
			else
			{
				sender.sendMessage( ChatColor.RED + "This is not a value potion type. ");
				sender.sendMessage( ChatColor.RED + "Use " + ChatColor.ITALIC + "/es list" + ChatColor.RED + " to get a list of valid types." );
			}
			return true;
		}
		
		return false;
	}
	
}
