package grozadonate.main;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GetDonate implements CommandExecutor {

	private Donation plugin;
	private Logger log = Logger.getLogger("Minecraft");
	
	public GetDonate(Donation plugin) {
		this.setPlugin(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Команда только для игроков");
			return true;
		}
		Player p = (Player) sender;
		String pName = p.getName();
		if (Donation.playersDonatList.containsKey(pName)) {
			String[] dons = Donation.playersDonatList.get(pName).split(";");
			for (int i = 0; i < dons.length; i++) {
				giveSet(p, dons[i]);
				log.info(ChatColor.GOLD + "[GrozaDonate] " + ChatColor.RESET + "Выдан набор " + dons[i] + " игроку " + pName);
			}
			p.sendMessage(ChatColor.GOLD + "Вы получили свой набор! :)");
			Donation.playersDonatList.remove(pName);
			Donation.savePlayers();
		} else {
			p.sendMessage(ChatColor.RED + "Нет доступных наборов доната!\n" + ChatColor.YELLOW + "Приобрести можно на нашем сайте: " + ChatColor.GOLD + ChatColor.UNDERLINE + "mcgroza.ru/donate.html");
		}
		return true;
	}
	
	private void giveSet(Player p, String itemSet){
		if (Donation.donatListItems.containsKey(itemSet)) {
			Inventory inv = Donation.donatListItems.get(itemSet);
			for (int i = 0; i < 54; i++) {
				if (inv.getItem(i) != null && inv.getItem(i).getType() != Material.AIR) {
					p.getInventory().addItem(inv.getItem(i));
				}
			}
		} else {
			log.warning(ChatColor.YELLOW + "Игроком " + p.getName() + " был куплен и выдан несуществующий набор: " + ChatColor.UNDERLINE + itemSet);
		}
	}
	
	public Donation getPlugin() {
		return plugin;
	}

	public void setPlugin(Donation plugin) {
		this.plugin = plugin;
	}

}
