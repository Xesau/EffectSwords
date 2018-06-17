package eu.xesau.effectswords;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;

public class EffectSwordsListener implements Listener {

	@EventHandler
	public void onHitEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager().getType() == EntityType.PLAYER) {
			Player p = (Player) e.getDamager();
			if (EffectSwordsPlugin.isAllowed(p.getInventory().getItemInHand().getType())) {
				if (e.getEntity() instanceof LivingEntity) {
					for (PotionEffect effect : EffectSwordsPlugin.getEffectsFrom(p.getInventory().getItemInHand())) {
						((LivingEntity) e.getEntity()).addPotionEffect(effect);
					}
				}
			}
		}
	}

}
