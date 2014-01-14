package me.iforgot290.shops;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.NBTTagList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.kumpelblase2.remoteentities.EntityManager;
import de.kumpelblase2.remoteentities.RemoteEntities;
import de.kumpelblase2.remoteentities.api.DespawnReason;
import de.kumpelblase2.remoteentities.api.RemoteEntity;
import de.kumpelblase2.remoteentities.api.RemoteEntityType;
import de.kumpelblase2.remoteentities.api.thinking.goals.DesireLookAtNearest;

public class Shops extends JavaPlugin{

	private EntityManager entityManager;
	private static Shops instance;
	public ArrayList<ShopPlayer> players = new ArrayList<ShopPlayer>();
	public Economy eco;

	@Override
	public void onEnable(){
		instance = this;
		Items.initalize();
		entityManager = RemoteEntities.createManager(this);
		getServer().getPluginManager().registerEvents(new InteractListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		initializeEntities();
		getServer().getPluginManager().registerEvents(new CommandManager(), this);
		setupEconomy();
	}

	@Override
	public void onDisable(){
		entityManager = null;
		instance = null;
		ChatListener.editing.clear();
		for (ShopPlayer sp : players){
			sp.getPlayer().despawn(DespawnReason.CUSTOM);
		}
	}
	
	private boolean setupEconomy(){
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            eco = economyProvider.getProvider();
        }
        return (eco != null);
    }

	public EntityManager getManager(){
		return entityManager;
	}

	public static Shops getInstance(){
		return instance;
	}

	@SuppressWarnings("deprecation")
	private void initializeEntities(){
		File folder = new File(getDataFolder()+"/shops/");
		folder.mkdirs();
		if (!folder.isDirectory()){
			getServer().getPluginManager().disablePlugin(this);
			getLogger().severe("Shops directory is returned as a non-directory");
			return;
		}
		for (File file : folder.listFiles()){
			if (file.getName().endsWith(".yml")){
				FileConfiguration conf = new YamlConfiguration();
				try{conf.load(file);}catch(Exception e){e.printStackTrace();}
				Location loc = getLocationFromString(conf.getString("location"));
				String name = conf.getString("name");
				RemoteEntity entity = createHuman(loc, name);
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
					if (id != 0){
						ItemStack is = new ItemStack(Material.getMaterial(id));
						is.setDurability((short)damage);
						ItemMeta meta = is.getItemMeta();
						ArrayList<String> lore = new ArrayList<String>();
						lore.add(ChatColor.GRAY+"Price: "+ChatColor.GREEN+String.valueOf(price));
						meta.setLore(lore);
						is.setItemMeta(meta);
						inv.setItem(slot, is);
					} else {
						ItemStack is = new ItemStack(Material.AIR);
						inv.setItem(slot, is);
					}
				}
				entity.getBukkitEntity().setMetadata("NPC", new FixedMetadataValue(this, true));
				ShopPlayer sp = new ShopPlayer(entity, inv, hand, helm, chest, legs, boots, conf);
				players.add(sp);
			}
		}
	}

	public void copy(InputStream in, File outFile) throws Exception{
		OutputStream out = new FileOutputStream(outFile);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		in.close();
	}

	public RemoteEntity createHuman(Location loc, String name){
		RemoteEntityType human = RemoteEntityType.Human;
		return entityManager.createNamedEntity(human, loc, name);
	}

	public Location getLocationFromString(String location){
		String[] loc = location.split(":");
		return new Location(Bukkit.getWorld(loc[0]), Integer.valueOf(loc[1]), Integer.valueOf(loc[2]), Integer.valueOf(loc[3]), Float.valueOf(loc[4]), Float.valueOf(loc[5]));
	}

	public String getStringFromLocation(Location location){
		String loc = "";
		loc = location.getWorld().getName()+":";
		loc = loc + location.getBlockX()+":";
		loc = loc + location.getBlockY()+":";
		loc = loc + location.getBlockZ()+":";
		loc = loc + location.getYaw()+":";
		loc = loc + location.getPitch();
		return loc;
	}

	public ShopPlayer getPlayer(int id){
		for (ShopPlayer sp : players)
			if (sp.getPlayer().getBukkitEntity().getEntityId()==id)
				return sp;
		return null;
	}

	public ShopPlayer getPlayer(String str){
		for (ShopPlayer sp : players)
			if (sp.getInv().getTitle().replace("[S] ", "").equalsIgnoreCase(str))
				return sp;
		return null;
	}

	public org.bukkit.inventory.ItemStack removeAttributes(org.bukkit.inventory.ItemStack item){
		if (item == null) {
			return item;
		}
		net.minecraft.server.v1_7_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag;
		if (!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		else {
			tag = nmsStack.getTag();
		}
		NBTTagList am = new NBTTagList();
		tag.set("AttributeModifiers", am);
		nmsStack.setTag(tag);
		return CraftItemStack.asCraftMirror(nmsStack);
	}

}
