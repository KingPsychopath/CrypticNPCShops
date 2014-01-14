package me.iforgot290.shops;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.kumpelblase2.remoteentities.api.DespawnReason;

public class ChatListener implements Listener{

	private Shops main;
	public static ArrayList<EditSession> sessions = new ArrayList<EditSession>();
	public static HashMap<HashMap<Player, ShopPlayer>, String> editing = new HashMap<HashMap<Player, ShopPlayer>, String>();

	public ChatListener(){
		main = Shops.getInstance();
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		HashMap<Player, ShopPlayer> set = null;
		EditSession session = null;
		for (HashMap<Player, ShopPlayer> keyset : editing.keySet())
			if (keyset.keySet().contains(event.getPlayer()))
				set = keyset;
		if (set==null)
			for (EditSession es : sessions)
				if (es.getPlayer().getName().equalsIgnoreCase(event.getPlayer().getName()))
					session = es;

		if (set!=null){
			Player player = set.keySet().iterator().next();
			ShopPlayer sp = set.get(player);
			String type = editing.get(set);
			event.setCancelled(true);
			if (type.equalsIgnoreCase("rename")){
				sp.rename(event.getMessage());
				editing.remove(set);
			} else if (type.equalsIgnoreCase("delete")){
				if (event.getMessage().equalsIgnoreCase("yes")){
					for (HumanEntity he : sp.getInv().getViewers())
						he.closeInventory();
					main.players.remove(sp);
					File inFile = new File(main.getDataFolder()+"/shops/"+sp.getPlayer().getName()+".yml");
					File outFile = new File(inFile+"."+String.valueOf(System.currentTimeMillis())+".backup");
					try {
						InputStream in = new FileInputStream(inFile);
						main.copy(in, outFile);
						inFile.delete();
						sp.getPlayer().despawn(DespawnReason.CUSTOM);
						player.sendMessage(ChatColor.GREEN+"Shop sucessfully deleted");
					} catch (Exception e){
						player.sendMessage(ChatColor.RED+"There was an error deleting this shop: "+e.getMessage());
						e.printStackTrace();
					}
				} else {
					player.sendMessage(ChatColor.RED+"Shop deletion cancelled");
				}
				editing.remove(set);
			}
		} else if (session != null){
			sessions.remove(session);
			if (session.isAdminSetPrice()){
				event.setCancelled(true);
				String msg = event.getMessage();
				Player player = session.getPlayer();
				Inventory admin = session.getAdminInv();
				int slot = session.getRawSlot();
				ItemStack is = session.getItem();
				if (!validate(msg)){
					player.sendMessage(ChatColor.RED+"Error: Please enter an integer value as a price");
					return;
				}
				ItemMeta meta = is.getItemMeta();
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(ChatColor.GRAY+"Price: "+ChatColor.GREEN+msg);
				meta.setLore(lore);
				is.setItemMeta(meta);
				admin.setItem(slot, is);
				player.openInventory(admin);
			} else {
				event.setCancelled(true);
				String msg = event.getMessage();
				Player player = session.getPlayer();
				ItemStack is = session.getItem();
				if (!validate(msg)){
					player.sendMessage(ChatColor.RED+"Error: Please enter an integer value");
					return;
				}
				int amount = Integer.parseInt(msg);
				if (amount>0&&amount<65){
					int price = Integer.valueOf(ChatColor.stripColor(is.getItemMeta().getLore().get(0)).replace("Price: ", ""));
					int total = price*amount;
					if (main.eco.getBalance(player.getName())>=total){
						main.eco.withdrawPlayer(player.getName(), total);
						player.getInventory().addItem(new ItemStack(is.getType(), amount));
					} else {
						player.sendMessage(ChatColor.RED+"Error: You do not have the required amount of money to purchase this");
					}
				} else {
					player.sendMessage(ChatColor.RED+"Error: Please enter a value between 1 and 64");
				}
			}
		}
	}

	public boolean validate(String str){
		for (char c : str.toCharArray())
			if (!Character.isDigit(c))
				return false;
		return true;
	}

}
