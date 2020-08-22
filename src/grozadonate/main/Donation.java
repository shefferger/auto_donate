package grozadonate.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Donation extends JavaPlugin implements Listener{
	
	public static HashMap<String, Inventory> donatListItems = new HashMap<String, Inventory>();
	public static HashMap<String, String> playersDonatList = new HashMap<String, String>();
	public static YamlConfiguration donList;
	public static YamlConfiguration playersList;
	private static File donListFile;
	private static File playersListFile;
	
	public void onEnable() {
		getCommand("gd").setExecutor(new CreateDonate(this));
		getCommand("donate").setExecutor(new GetDonate(this));
		Bukkit.getPluginManager().registerEvents(this, this);
		File cDir = new File("plugins" + System.getProperty("file.separator") + "GrozaDonate");
        if (!cDir.exists()) {
        	cDir.mkdir();
        	getLogger().info("Папка GrozaDonate создана в /plugins/");
        }
        donListFile = new File("plugins" + System.getProperty("file.separator") + "GrozaDonate" + System.getProperty("file.separator") + "donateList.yml");
        if (!donListFile.exists()) {
        	try {
				donListFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        playersListFile = new File("plugins" + System.getProperty("file.separator") + "GrozaDonate" + System.getProperty("file.separator") + "playersDonateList.yml");
        if (!playersListFile.exists()) {
        	try {
				playersListFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        donList = YamlConfiguration.loadConfiguration(donListFile);
        playersList = YamlConfiguration.loadConfiguration(playersListFile);
        load();
	}
	
	@EventHandler
	public void onPlayerConnect(PlayerJoinEvent e) {
		if (playersDonatList.containsKey(e.getPlayer().getName())) {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {

				@Override
				public void run() {
					e.getPlayer().sendMessage(ChatColor.GOLD + "[GrozaDonate] Для вас есть набор с донатом!\n " +
							ChatColor.YELLOW + "Чтобы получить донат, введите: " + ChatColor.GOLD + ChatColor.UNDERLINE + "/donate");
				}
				
			}, 210);
		}
	}
	
	public static void saveItems() {
		for (String s : donatListItems.keySet()) {
			ItemStack[] values = donatListItems.get(s).getContents();
			donList.set("items." + s, values);
		}
		try {
			donList.save(donListFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void delete(String del) throws IOException {
		donatListItems.remove(del);
		donList.set("items." + del, null);
		donList.save(donListFile);
	}
	
	public static void savePlayers() {
		/*for (String s : playersDonatList.keySet()) {
			String values = playersDonatList.get(s);
			playersList.set("players." + s, values);
		}*/
		playersList.set("players", playersDonatList);
		try {
			playersList.save(playersListFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void load() {
		if (!donList.contains("items") || !playersList.contains("players")) {
			return;
		}
		for (String s : donList.getConfigurationSection("items").getKeys(false)) {
			Inventory inv = Bukkit.createInventory(null, 54, s);
			@SuppressWarnings("unchecked")
			ArrayList<ItemStack> content = (ArrayList<ItemStack>) donList.getList("items." + s);
			ItemStack[] items = new ItemStack[content.size()];
			for (int i = 0; i < content.size(); i++) {
			    ItemStack item = content.get(i);
			    if (item != null) {
			        items[i] = item;
			    } else {
			        items[i] = new ItemStack(Material.AIR);
			    }
			}
			inv.setContents(items);
			donatListItems.put(s, inv);
		}
		
		for (String s : playersList.getConfigurationSection("players").getKeys(false)) {
			String value = playersList.getString("players." + s);
			
			playersDonatList.put(s, value);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onClick(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();;
		if (donatListItems.size() == 0) {
			return;
		} else {
			if (!donatListItems.containsValue(inv)) {
				return;
			}
		}
		if (inv.getSize() == 54) {	
			if ((e.getCursor().getType() == (Material.AIR) || e.getCursor() == null || e.getCurrentItem().getType() != (Material.AIR) || e.getCurrentItem() != null)) {
				ItemStack item = inv.getItem(53);
				if (item == null) {
					return;
				}
				if (item.getType().equals(Material.EMERALD_BLOCK) && item.hasItemMeta() && item.getItemMeta().getCustomModelData() == 1) {
					if (e.getSlot() != 53) {
						return;
					}
					e.setCancelled(true);
					String iSet = item.getItemMeta().getLore().get(0);
					Player p = (Player) e.getWhoClicked();
					inv.remove(e.getCurrentItem());
					Donation.saveItems();
					p.sendMessage("Набор " + ChatColor.ITALIC + ChatColor.GOLD + iSet + ChatColor.RESET + " успешно " + ChatColor.GREEN + "сохранен!");
					Bukkit.getScheduler().runTask(this, new Runnable() {
						@Override
						public void run() {
							p.closeInventory();
						}
					});
				}
			}
		}
	}
}
