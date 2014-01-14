package me.iforgot290.shops;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class EditSession {
	
	private Shops main;
	private Player player;
	private ShopPlayer entity;
	private Inventory admin;
	private int slot;
	private ItemStack item;
	private boolean setprice;
	
	public EditSession(Player player, ShopPlayer entity, Inventory admin, int slot, ItemStack is, boolean setprice){
		main = Shops.getInstance();
		this.player = player;
		this.entity = entity;
		this.admin = admin;
		this.slot = slot;
		is.setAmount(1);
		item = is;
		this.setprice = setprice;
	}
	
	public boolean isAdminSetPrice(){
		return setprice;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public ShopPlayer getShopPlayer(){
		return entity;
	}
	
	public Inventory getAdminInv(){
		return admin;
	}
	
	public int getRawSlot(){
		return slot;
	}
	
	public ItemStack getItem(){
		return item;
	}

}
