package me.iforgot290.shops;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class InventoryListener implements Listener {

	private Shops main;

	public InventoryListener(){
		main = Shops.getInstance();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClose(InventoryCloseEvent event){
		Inventory inv = event.getInventory();
		// 35 last slot
		// <slot>:<id>:<damage>:<price>
		if (inv.getTitle().startsWith("[ADMIN]")){
			String name = inv.getTitle().replace("[ADMIN] [S] ", "");
			ArrayList<String> contents = new ArrayList<String>();
			int item = 0;
			for (ItemStack is : inv.getContents()){
				if (item<36){
					if (is != null){
						String slot = String.valueOf(item);
						String id = (is.getTypeId() == 0) ? "0" : String.valueOf(is.getTypeId());
						String damage = String.valueOf(is.getDurability()) == null ? "0" : String.valueOf(is.getDurability());
						String price = is.getItemMeta() == null ? "0" : ChatColor.stripColor(is.getItemMeta().getLore().get(0)).replace("Price: ", "");
						contents.add(slot+":"+id+":"+damage+":"+price);
					} else {
						contents.add(String.valueOf(item)+":0:0:0");
					}
				}
				item++;
			}
			File file = new File(main.getDataFolder()+"/shops/"+name.replaceAll(" ", "_")+".yml");
			FileConfiguration conf = new YamlConfiguration();
			try{conf.load(file);}catch(Exception e){e.printStackTrace();}
			conf.set("invcontents", contents);
			try{conf.save(file);}catch(Exception e){e.printStackTrace();}
			ShopPlayer sp = main.getPlayer(name);
			for (String str : contents){
				String[] iargs = str.split(":");
				int slot = Integer.valueOf(iargs[0]);
				int id = Integer.valueOf(iargs[1]);
				int damage = Integer.valueOf(iargs[2]);
				int price = Integer.valueOf(iargs[3]);
				if (id != 0){
					ItemStack is = new ItemStack(Material.getMaterial(id));
					is.setDurability((short)damage);
					ItemMeta meta = is.getItemMeta();
					ArrayList<String> lore = new ArrayList<String>();
					lore.add(ChatColor.GRAY+"Price: "+ChatColor.GREEN+String.valueOf(price));
					meta.setLore(lore);
					is.setItemMeta(meta);
					sp.getInv().setItem(slot, is);
				} else {
					ItemStack is = new ItemStack(Material.AIR);
					sp.getInv().setItem(slot, is);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(InventoryClickEvent event){
		if (event.getInventory().getTitle().startsWith("[ADMIN]")){
			if (event.isShiftClick()){
				event.setCancelled(true);
				event.setResult(Result.DENY);
				return;
			}
			ShopPlayer sp = main.getPlayer(event.getInventory().getTitle().replace("[ADMIN] [S] ", ""));
			boolean open = (!(sp.getPlayer().getName().equalsIgnoreCase(ChatColor.RED+sp.getInv().getTitle().replace("[S] ", ""))));
			if (event.getRawSlot()>=36&&event.getRawSlot()<=44){
				event.setCancelled(true);
				event.setResult(Result.DENY);
				if (event.getRawSlot()==36){
					if (open){
						sp.getPlayer().setName(ChatColor.RED+sp.getPlayer().getName());
						Player player = (Player)sp.getPlayer().getBukkitEntity();
						player.setCanPickupItems(false);
						player.setItemInHand(sp.getHand());
						player.getInventory().setHelmet(sp.getHelm());
						player.getInventory().setChestplate(sp.getChest());
						player.getInventory().setLeggings(sp.getLegs());
						player.getInventory().setBoots(sp.getBoots());
						sp.admin = true;
						event.setCurrentItem(Items.closed);
						for (HumanEntity h : sp.getInv().getViewers())
							h.closeInventory();
					} else {
						sp.getPlayer().setName(sp.getInv().getTitle().replace("[S] ", ""));
						Player player = (Player)sp.getPlayer().getBukkitEntity();
						player.setCanPickupItems(false);
						player.setMetadata("NPC", new FixedMetadataValue(main, true));
						player.setItemInHand(sp.getHand());
						player.getInventory().setHelmet(sp.getHelm());
						player.getInventory().setChestplate(sp.getChest());
						player.getInventory().setLeggings(sp.getLegs());
						player.getInventory().setBoots(sp.getBoots());
						sp.admin = false;
						event.setCurrentItem(Items.open);
					}
				} else if (event.getRawSlot()==37){
					Player clicker = (Player)event.getWhoClicked();
					clicker.closeInventory();
					clicker.sendMessage(ChatColor.GREEN+"Please type what you would like to rename this shop to");
					HashMap<Player, ShopPlayer> psp = new HashMap<Player, ShopPlayer>();
					psp.put(clicker, sp);
					ChatListener.editing.put(psp, "rename");
				} else if (event.getRawSlot()==38){
					Player clicker = (Player)event.getWhoClicked();
					clicker.closeInventory();
					clicker.sendMessage(ChatColor.GOLD+"Warning: Are you sure you want to delete this shop? (yes/no)");
					HashMap<Player, ShopPlayer> psp = new HashMap<Player, ShopPlayer>();
					psp.put(clicker, sp);
					ChatListener.editing.put(psp, "delete");
				} else if (event.getRawSlot()==39){
					Player clicker = (Player)event.getWhoClicked();
					clicker.closeInventory();
					clicker.openInventory(sp.getInv());
				} else if (event.getRawSlot()==40){
					sp.setHand(event.getCursor());
					Player player = (Player)sp.getPlayer().getBukkitEntity();
					player.setItemInHand(sp.getHand());
					event.setCurrentItem(sp.getHand());
					event.setCursor(new ItemStack(Material.AIR));
					if (sp.getHand().getEnchantments().size()==0){
						sp.conf.set("hand", String.valueOf(sp.getHand().getTypeId())+":false");
					} else {
						sp.conf.set("hand", String.valueOf(sp.getHand().getTypeId())+":true");
					}
					try {
						sp.conf.save(main.getDataFolder()+"/shops/"+sp.getPlayer().getName().replace(" ", "_")+".yml");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (event.getRawSlot()==41){
					sp.setHelm(event.getCursor());
					Player player = (Player)sp.getPlayer().getBukkitEntity();
					player.getInventory().setHelmet(sp.getHelm());
					event.setCurrentItem(sp.getHelm());
					event.setCursor(new ItemStack(Material.AIR));
					if (sp.getHelm().getEnchantments().size()==0){
						sp.conf.set("helm", String.valueOf(sp.getHelm().getTypeId())+":false");
					} else {
						sp.conf.set("helm", String.valueOf(sp.getHelm().getTypeId())+":true");
					}
					try {
						sp.conf.save(main.getDataFolder()+"/shops/"+sp.getPlayer().getName().replace(" ", "_")+".yml");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (event.getRawSlot()==42){
					sp.setChest(event.getCursor());
					Player player = (Player)sp.getPlayer().getBukkitEntity();
					player.getInventory().setChestplate(sp.getChest());
					event.setCurrentItem(sp.getChest());
					event.setCursor(new ItemStack(Material.AIR));
					if (sp.getChest().getEnchantments().size()==0){
						sp.conf.set("chest", String.valueOf(sp.getChest().getTypeId())+":false");
					} else {
						sp.conf.set("chest", String.valueOf(sp.getChest().getTypeId())+":true");
					}
					try {
						sp.conf.save(main.getDataFolder()+"/shops/"+sp.getPlayer().getName().replace(" ", "_")+".yml");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (event.getRawSlot()==43){
					sp.setLegs(event.getCursor());
					Player player = (Player)sp.getPlayer().getBukkitEntity();
					player.getInventory().setLeggings(sp.getLegs());
					event.setCurrentItem(sp.getLegs());
					event.setCursor(new ItemStack(Material.AIR));
					if (sp.getLegs().getEnchantments().size()==0){
						sp.conf.set("legs", String.valueOf(sp.getLegs().getTypeId())+":false");
					} else {
						sp.conf.set("legs", String.valueOf(sp.getLegs().getTypeId())+":true");
					}
					try {
						sp.conf.save(main.getDataFolder()+"/shops/"+sp.getPlayer().getName().replace(" ", "_")+".yml");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (event.getRawSlot()==44){
					sp.setBoots(event.getCursor());
					Player player = (Player)sp.getPlayer().getBukkitEntity();
					player.getInventory().setBoots(sp.getBoots());
					event.setCurrentItem(sp.getBoots());
					event.setCursor(new ItemStack(Material.AIR));
					if (sp.getBoots().getEnchantments().size()==0){
						sp.conf.set("boots", String.valueOf(sp.getBoots().getTypeId())+":false");
					} else {
						sp.conf.set("boots", String.valueOf(sp.getBoots().getTypeId())+":true");
					}
					try {
						sp.conf.save(main.getDataFolder()+"/shops/"+sp.getPlayer().getName().replace(" ", "_")+".yml");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (event.getRawSlot()<=35){
				if (!open){
					event.setCancelled(true);
					event.setResult(Result.DENY);
					if ((event.getCurrentItem()==null||event.getCurrentItem().getType()==Material.AIR)&&(event.getCursor().getType()!=Material.AIR)){
						//placing
						Player player = (Player)event.getWhoClicked();
						Inventory inv = event.getInventory();
						int slot = event.getRawSlot();
						ItemStack item = event.getCursor();
						event.setCursor(new ItemStack(Material.AIR));
						EditSession session = new EditSession(player, sp, inv, slot, item, true);
						ChatListener.sessions.add(session);
						player.closeInventory();
						player.sendMessage(ChatColor.GREEN+"Please enter a price for this item");
					} else if (event.getCursor()==null||event.getCursor().getType()==Material.AIR&&(event.getCurrentItem().getType()!=Material.AIR)){
						//taking
						ItemStack current = event.getCurrentItem();
						if (current.getItemMeta()!=null&&current.getItemMeta().getLore()!=null){
							event.setCurrentItem(new ItemStack(Material.AIR));
							Player player = (Player)event.getWhoClicked();
							ItemStack toadd = new ItemStack(current.getType());
							player.getInventory().addItem(toadd);
						}
					}
				} else {
					event.setCancelled(true);
					event.setResult(Result.DENY);
					Player player = (Player)event.getWhoClicked();
					player.sendMessage(ChatColor.RED+"Error: You must close the shop before editing its inventory");
				}
			} else {
				
			}

		} else if (event.getInventory().getTitle().startsWith("[S] ")){
			if (event.isShiftClick()){
				event.setCancelled(true);
				event.setResult(Result.DENY);
				return;
			}
			Player player = (Player)event.getWhoClicked();
			ShopPlayer sp = main.getPlayer(event.getInventory().getTitle().replace("[S] ", ""));
			if (event.getRawSlot()<36){
				EditSession es = new EditSession(player, sp, null, event.getRawSlot(), event.getCurrentItem(), false);
				ChatListener.sessions.add(es);
				player.closeInventory();
				player.sendMessage(ChatColor.GREEN+"Enter the amount you would like to buy");
			}
		}
	}

}
