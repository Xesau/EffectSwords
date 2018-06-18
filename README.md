![EffectSwords logo](https://www.spigotmc.org/data/resource_icons/57/57850.jpg?1529261961)

# EffectSwords
EffectSword is a simple plugin for Bukkit/Spigot that allows users to buy potion effects to put on their swords. When a player hits another player or an animal with the sword, the potino effect gets transfered 

When `buyable` is turned on in config.yml, Vault and an economy plugin are needed. You can configure the price for each effect, and also configure for which itemms effects can be bought.

## Commands

**/es &lt;effect&gt; [amplifier]**  
Adds an effect to the held item  
Permission: `effectswords.add`

**/es remove <effect>**  
Removes an effect from the held item  
Permission: `effectswords.remove`

**/es list**  
Shows a list of available effects and their prices

The `effectswords.free` permission makes adding effects free.

## API

This plugin contains a small API. The methods are described below.

### EffectSwordsPlugin.getEffectsFrom(ItemStack item);
Description: Returns all effects that have been added to this item.  
Return type: `ArrayList<PotionEffect>`

### EffectSwordsPlugin.applyEffect(ItemStack item, PotionEffect effect)
Description: Adds an effect to an item  
Return type: `ItemStack`

### EffectSwordsPlugin.removeEffectFrom(ItemStack item, PotionEffectType type)
Description: Removes an effect from this item if possible. Returns null if the effect wasn't found.  
Return type: `ItemStack`
