package me.iforgot290.shops;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.kumpelblase2.remoteentities.api.RemoteEntity;

public class ShopPlayer {

	private RemoteEntity player;
	private Inventory inv;
	private ItemStack hand;
	private ItemStack helm;
	private ItemStack chest;
	private ItemStack legs;
	private ItemStack boots;
	public FileConfiguration conf;
	public boolean admin = false;

	public ShopPlayer(RemoteEntity player, Inventory inv, ItemStack hand, ItemStack helm, ItemStack chest, ItemStack legs, ItemStack boots, FileConfiguration conf){
		this.player = player;
		this.inv = inv;
		this.hand = hand;
		this.conf = conf;
		if (hand!=null&&hand.getType()!=Material.AIR){
			ItemMeta handMeta = this.hand.getItemMeta();
			handMeta.setDisplayName(ChatColor.GREEN+"Hand Contents");
			this.hand.setItemMeta(handMeta);
		} if (helm!=null&&helm.getType()!=Material.AIR){
			ItemMeta helmMeta = helm.getItemMeta();
			helmMeta.setDisplayName(ChatColor.GREEN+"Helmet Contents");
			helm.setItemMeta(helmMeta);
			this.helm = helm;
		} if (chest!=null&&chest.getType()!=Material.AIR){
			ItemMeta chestMeta = chest.getItemMeta();
			chestMeta.setDisplayName(ChatColor.GREEN+"Chestplate Contents");
			chest.setItemMeta(chestMeta);
			this.chest = chest;
		} if (legs!=null&&legs.getType()!=Material.AIR){
			ItemMeta legsMeta = legs.getItemMeta();
			legsMeta.setDisplayName(ChatColor.GREEN+"Leggings Contents");
			legs.setItemMeta(legsMeta);
			this.legs = legs;
		} if (legs!=null&&legs.getType()!=Material.AIR){
			ItemMeta bootsMeta = boots.getItemMeta();
			bootsMeta.setDisplayName(ChatColor.GREEN+"Boots Contents");
			boots.setItemMeta(bootsMeta);
			this.boots = boots;
		}
	}

	public RemoteEntity getPlayer(){
		return player;
	}

	public Inventory getInv(){
		return inv;
	}

	public void rename(String name){
		try {
			File inFile = new File(Shops.getInstance().getDataFolder()+"/shops/"+player.getName().replace(" ", "_")+".yml");
			File outFile = new File(Shops.getInstance().getDataFolder()+"/shops/"+name.replace(" ", "_")+".yml");
			InputStream in = new FileInputStream(inFile);
			OutputStream out = new FileOutputStream(outFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
			inFile.delete();
			FileConfiguration conf = new YamlConfiguration();
			try{conf.load(outFile);}catch(Exception e){e.printStackTrace();}
			conf.set("name", name);
			try{conf.save(outFile);}catch(Exception e){e.printStackTrace();}
			this.conf = conf;
			if (admin)
				player.setName(ChatColor.RED+name);
			else
				player.setName(name);
			Player player = (Player)this.player.getBukkitEntity();
			player.setItemInHand(getHand());
			player.getInventory().setHelmet(getHelm());
			player.getInventory().setChestplate(getChest());
			player.getInventory().setLeggings(getLegs());
			player.getInventory().setBoots(getBoots());
			for (HumanEntity e : this.inv.getViewers())
				e.closeInventory();
			Inventory inv = Bukkit.createInventory(null, 36, "[S] "+name);
			inv.setContents(this.inv.getContents());
			this.inv = inv;
		} catch (Exception e){

		}
	}

	public ItemStack getHand(){
		return hand;
	}

	public void setHand(ItemStack is){
		if (is!=null&&is.getType()!=Material.AIR){
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN+"Hand Contents");
			is.setItemMeta(meta);
			this.hand = is;
		} else {
			hand.setType(Material.AIR);
		}
	}

	public ItemStack getHelm(){
		return helm;
	}

	public void setHelm(ItemStack is){
		if (is!=null&&is.getType()!=Material.AIR){
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN+"Helmet Contents");
			is.setItemMeta(meta);
			this.helm = is;
		} else {
			helm.setType(Material.AIR);
		}
	}

	public ItemStack getChest(){
		return chest;
	}

	public void setChest(ItemStack is){
		if (is!=null&&is.getType()!=Material.AIR){
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN+"Chestplate Contents");
			is.setItemMeta(meta);
			this.chest = is;
		} else {
			chest.setType(Material.AIR);
		}
	}

	public ItemStack getLegs(){
		return legs;
	}

	public void setLegs(ItemStack is){
		if (is!=null&&is.getType()!=Material.AIR){
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN+"Leggings Contents");
			is.setItemMeta(meta);
			this.legs = is;
		} else {
			legs.setType(Material.AIR);
		}
	}

	public ItemStack getBoots(){
		return boots;
	}

	public void setBoots(ItemStack is){
		if (is!=null&&is.getType()!=Material.AIR){
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN+"Boots Contents");
			is.setItemMeta(meta);
			this.boots = is;
		} else {
			boots.setType(Material.AIR);
		}
	}

}
