package me.iforgot290.shops;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Items {

	public static void initalize(){
		ItemStack closed = new ItemStack(Material.INK_SACK);
		closed.setDurability((short) 8);
		ItemMeta closedMeta = closed.getItemMeta();
		closedMeta.setDisplayName(ChatColor.RED+"Shop is CLOSED");
		ArrayList<String> closedLore = new ArrayList<String>();
		closedLore.add(ChatColor.GRAY+"Click to change the status of the shop");
		closedMeta.setLore(closedLore);
		closed.setItemMeta(closedMeta);
		Items.closed = closed;
		
		ItemStack open = new ItemStack(Material.INK_SACK);
		open.setDurability((short) 10);
		ItemMeta openMeta = open.getItemMeta();
		openMeta.setDisplayName(ChatColor.GREEN+"Shop is OPEN");
		ArrayList<String> openLore = new ArrayList<String>();
		openLore.add(ChatColor.GRAY+"Click to change the status of the shop");
		openMeta.setLore(openLore);
		open.setItemMeta(openMeta);
		Items.open = open;
		
		ItemStack rename = new ItemStack(Material.NAME_TAG);
		ItemMeta renameMeta = rename.getItemMeta();
		ArrayList<String> renameLore = new ArrayList<String>();
		renameLore.add(ChatColor.GRAY+"Click to rename this shop");
		renameMeta.setLore(renameLore);
		rename.setItemMeta(renameMeta);
		Items.rename = rename;
		
		ItemStack delete = new ItemStack(Material.REDSTONE);
		ItemMeta deleteMeta = delete.getItemMeta();
		deleteMeta.setDisplayName(ChatColor.RED+"Delete Shop");
		ArrayList<String> deleteLore = new ArrayList<String>();
		deleteLore.add(ChatColor.GRAY+"Click to delete this shop");
		deleteMeta.setLore(deleteLore);
		delete.setItemMeta(deleteMeta);
		Items.delete = delete;
		
		ItemStack player = new ItemStack(Material.SKULL_ITEM);
		ItemMeta playerMeta = player.getItemMeta();
		playerMeta.setDisplayName(ChatColor.GREEN+"Open normal view");
		ArrayList<String> playerLore = new ArrayList<String>();
		playerLore.add(ChatColor.GRAY+"Click to see what a normal player would see");
		playerMeta.setLore(playerLore);
		player.setItemMeta(playerMeta);
		playerview = player;
	}

	public static ItemStack closed;
	public static ItemStack open;
	private static ItemStack rename;
	public static ItemStack delete;
	public static ItemStack playerview;
	
	public static ItemStack getRenameTag(String name){
		ItemStack rename = Items.rename.clone();
		ItemMeta meta = rename.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN+"Rename shop ("+name.toUpperCase()+ChatColor.RESET+ChatColor.GREEN+")");
		rename.setItemMeta(meta);
		return rename;
	}

}
