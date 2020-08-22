package grozadonate.main;



import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;


public class CreateDonate implements CommandExecutor {

	private Donation plugin;
	private Logger log = Logger.getLogger("Minecraft");
	
	public CreateDonate(Donation plugin) {
		this.setPlugin(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender.hasPermission("GrozaDonate.createDonate"))) {
			sender.sendMessage(ChatColor.RED + "Недостаточно прав!");
			return true;
		}
		if (args.length == 0) {
			return false;
		}
		if (args[0].contentEquals("givedonate")) {
			if (!(args.length == 3)) {
				return false;
			}
			String plName = args[1]; // /gd givedonate wkera test1
			if (Donation.playersDonatList.containsKey(plName)) {
				Donation.playersDonatList.replace(plName, Donation.playersDonatList.get(plName) + args[2] + ";");
			} else {
				Donation.playersDonatList.put(plName, args[2] + ";");
			}
			if (Bukkit.getServer().getPlayer(plName) != null && Bukkit.getServer().getPlayer(plName).isOnline()) {
				Bukkit.getServer().getPlayer(plName).sendMessage(ChatColor.GOLD + "[GrozaDonate] Поздравляем с покупкой!\n " +
									ChatColor.YELLOW + "Чтобы получить донат, введите: " + ChatColor.GOLD + ChatColor.UNDERLINE + "/donate");
			}
			log.info(ChatColor.GOLD + "[GrozaDonate] " + ChatColor.RESET + "Игрок " + ChatColor.YELLOW + plName + ChatColor.RESET + " купил набор " + ChatColor.AQUA + args[2]);
			Donation.savePlayers();
			return true;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage("Команда только для игроков");
			return true;
		}
		Player p = (Player) sender;
		if (args[0].contentEquals("list")) {
			int i = 1;
			sender.sendMessage("Наборы с донатом:");
			for (String s : Donation.donatListItems.keySet()) {
				sender.sendMessage(i + ". " + s);
				i += 1;
			}
			return true;
		}
		if (args[0].contentEquals("add")) {
			if (!(args.length == 2)) {
				return false;
			}
			if (Donation.donatListItems.containsKey(args[1])) {
				sender.sendMessage("Набор " + args[1] + " уже существует!");
				return true;
			}
			Donation.donatListItems.put(args[1], Bukkit.createInventory(null, 54, args[1]));
			
			ItemStack tech;
			tech = new ItemStack(Material.EMERALD_BLOCK);
			ItemMeta meta = tech.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Сохранить и выйти");
			meta.setLore(Lists.newArrayList(args[1]));
			meta.setCustomModelData(1);
			tech.setItemMeta(meta);
			Donation.donatListItems.get(args[1]).setItem(53, tech);
			
			p.openInventory(Donation.donatListItems.get(args[1]));
			
			return true;
		}
		if (args[0].contentEquals("remove")) {
			if (!(args.length == 2)) {
				return false;
			}
			if (!Donation.donatListItems.containsKey(args[1])) {
				sender.sendMessage("Набора " + args[1] + " не существует!");
				return true;
			}
			//Donation.donatListItems.remove(args[1]);
			//Donation.donList.set(args[1], null);
			try {
				Donation.delete(args[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Donation.saveItems();
			p.sendMessage("Набор " + ChatColor.ITALIC + ChatColor.GOLD + args[1] + ChatColor.RESET + " успешно " + ChatColor.RED + "удален!");
			return true;
		}
		if (args[0].contentEquals("edit")) {
			if (!(args.length == 2)) {
				return false;
			}
			if (!Donation.donatListItems.containsKey(args[1])) {
				sender.sendMessage("Набора " + args[1] + " не существует!");
				return true;
			}
			ItemStack tech;
			tech = new ItemStack(Material.EMERALD_BLOCK);
			ItemMeta meta = tech.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Сохранить и выйти");
			meta.setLore(Lists.newArrayList(args[1]));
			meta.setCustomModelData(1);
			tech.setItemMeta(meta);
			Donation.donatListItems.get(args[1]).setItem(53, tech);
			
			p.openInventory(Donation.donatListItems.get(args[1]));
		}
		
		return true;
	}
	
	
	
	public Donation getPlugin() {
		return plugin;
	}

	public void setPlugin(Donation plugin) {
		this.plugin = plugin;
	}
}
