package me.iforgot290.shops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;

public class InteractListener implements Listener {

	private Shops main;
	
	public InteractListener(){
		main = Shops.getInstance();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event){
		Player player = event.getPlayer();
		ShopPlayer sp = main.getPlayer(event.getRightClicked().getEntityId());
		if (sp==null)
			return;
		if (player.isOp()){
			adminEdit(player, sp);
			return;
		}
		if (sp.admin){
			player.sendMessage(ChatColor.RED+"This shop is currently being edited");
			return;
		}
		player.openInventory(sp.getInv());
	}
	
	public void adminEdit(Player player, ShopPlayer entity){
		Inventory admin = Bukkit.createInventory(null, 45, "[ADMIN] "+entity.getInv().getTitle());
		admin.setContents(entity.getInv().getContents());
		boolean open = (!(entity.getPlayer().getName().equalsIgnoreCase(ChatColor.RED+entity.getInv().getTitle().replace("[S] ", ""))));
		if (open)
			admin.setItem(36, Items.open);
		else
			admin.setItem(36, Items.closed);
		admin.setItem(37, Items.getRenameTag(entity.getPlayer().getName()));
		admin.setItem(38, Items.delete);
		admin.setItem(39, Items.playerview);
		admin.setItem(40, entity.getHand());
		admin.setItem(41, entity.getHelm());
		admin.setItem(42, entity.getChest());
		admin.setItem(43, entity.getLegs());
		admin.setItem(44, entity.getBoots());
		player.openInventory(admin);
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event){
		boolean contains = false;
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.equals(event.getPlayer()))
				contains = true;
		if (!contains)
			event.setCancelled(true);
	}
	
}
