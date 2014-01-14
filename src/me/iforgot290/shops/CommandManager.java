package me.iforgot290.shops;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import de.kumpelblase2.remoteentities.api.RemoteEntity;
import de.kumpelblase2.remoteentities.api.thinking.goals.DesireLookAtNearest;

public class CommandManager implements Listener{
	
	private Shops main;
	
	public CommandManager(){
		main = Shops.getInstance();
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event){
		if (event.getMessage().startsWith("/shops")){
			event.setCancelled(true);
			ArrayList<String> args = new ArrayList<String>();
			for (String str : event.getMessage().split(" "))
				if (!str.startsWith("/"))
					args.add(str);
			handleCommand(event.getPlayer(), "shops", "shops", args);
		}
	}
	
	public void handleCommand(Player player, String cmd, String label, ArrayList<String> args){
		if (args.size()==0){
			sendHelp(player);
			return;
		}
		
		if (args.get(0).equalsIgnoreCase("create")){
			if (args.get(1)==null){
				sendHelp(player);
				return;
			}
			if (!player.isOp()){
				player.sendMessage(ChatColor.RED+"Permission denied");
				return;
			}
			createShop(player.getLocation(), args.get(1), player);
			return;
		}
		
		sendHelp(player);
	}
	
	@SuppressWarnings("deprecation")
	private void createShop(Location loc, String name, Player player1) {
		File file = new File(main.getDataFolder()+"/shops/"+name.replace(" ", "_")+".yml");
		if (file.exists()||file.isDirectory()){
			player1.sendMessage(ChatColor.RED+"There is already a shop named this, or an error with your file system");
			return;
		} try {
		main.copy(main.getResource("default.yml"), file);
		} catch (Exception e){
			e.printStackTrace();
		}
		FileConfiguration conf = new YamlConfiguration();
		try{conf.load(file);}catch(Exception e){e.printStackTrace();}
		conf.set("name", name);
		conf.set("location", main.getStringFromLocation(loc));
		try{conf.save(file);}catch(Exception e){e.printStackTrace();}
		RemoteEntity entity = main.createHuman(loc, name);
		entity.setStationary(true);
		entity.getMind().addMovementDesire(new DesireLookAtNearest(Player.class, 8F), 1);
		Player player = (Player)entity.getBukkitEntity();
		player.setCanPickupItems(false);
		ItemStack hand = new ItemStack(Material.AIR);
		ItemStack helm = new ItemStack(Material.AIR);
		ItemStack chest = new ItemStack(Material.AIR);
		ItemStack legs = new ItemStack(Material.AIR);
		ItemStack boots = new ItemStack(Material.AIR);
		if (conf.getString("hand")!=null){
			String[] handargs = conf.getString("hand").split(":");
			hand = new ItemStack(Material.getMaterial(Integer.valueOf(handargs[0])));
			if (Boolean.valueOf(handargs[1]))
				hand.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			player.setItemInHand(hand);
		}
		if (conf.getString("helm")!=null){
			String[] helmargs = conf.getString("helm").split(":");
			helm = new ItemStack(Material.getMaterial(Integer.valueOf(helmargs[0])));
			if (Boolean.valueOf(helmargs[1]))
				helm.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			player.getInventory().setHelmet(helm);
		}
		if (conf.getString("chest")!=null){
			String[] chestargs = conf.getString("chest").split(":");
			chest = new ItemStack(Material.getMaterial(Integer.valueOf(chestargs[0])));
			if (Boolean.valueOf(chestargs[1]))
				chest.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			player.getInventory().setChestplate(chest);
		}
		if (conf.getString("legs")!=null){
			String[] legsargs = conf.getString("legs").split(":");
			legs = new ItemStack(Material.getMaterial(Integer.valueOf(legsargs[0])));
			if (Boolean.valueOf(legsargs[1]))
				legs.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			player.getInventory().setLeggings(legs);
		}
		if (conf.getString("boots")!=null){
			String[] bootsargs = conf.getString("boots").split(":");
			boots = new ItemStack(Material.getMaterial(Integer.valueOf(bootsargs[0])));
			if (Boolean.valueOf(bootsargs[1]))
				boots.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			player.getInventory().setBoots(boots);
		}
		Inventory inv = Bukkit.createInventory(null, 36, "[S] "+name);
		ArrayList<String> invcontents = (ArrayList<String>) conf.getStringList("invcontents");
		for (String str : invcontents){
			String[] iargs = str.split(":");
			int slot = Integer.valueOf(iargs[0]);
			int id = Integer.valueOf(iargs[1]);
			int damage = Integer.valueOf(iargs[2]);
			int price = Integer.valueOf(iargs[3]);
			ItemStack is = new ItemStack(Material.getMaterial(id));
			is.setDurability((short)damage);
			ItemMeta meta = is.getItemMeta();
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("");
			lore.add(ChatColor.GRAY+"Price: "+String.valueOf(price));
			meta.setLore(lore);
			is.setItemMeta(meta);
			inv.setItem(slot, is);
		}
		entity.getBukkitEntity().setMetadata("NPC", new FixedMetadataValue(main,true));
		ShopPlayer sp = new ShopPlayer(entity, inv, hand, helm, chest, legs, boots, conf);
		main.players.add(sp);
	}

	public void sendHelp(Player sender){
		sender.sendMessage("Test");
	}

}
