package its_meow.claimit.common.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import its_meow.claimit.Ref;
import its_meow.claimit.common.command.claimit.CommandSubAdmin;
import its_meow.claimit.common.command.claimit.CommandSubClaim;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandClaimIt extends CommandTreeBase {

	public CommandClaimIt() {
		this.addSubcommand(new CommandSubClaim());
		this.addSubcommand(new CommandSubAdmin());
	}

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "claimit";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "claimit <subcommand>";
	}

	private String aliasList = "";

	@Override
	public List<String> getAliases() {
		List<String> aliases = new ArrayList<String>();
		aliases.add("claimit");
		aliases.add("ci");
		aliases.iterator().forEachRemaining(s -> aliasList += "/" + s + " ");
		return aliases;
	}


	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		super.execute(server, sender, args);
		if(args.length == 0) {
			sendMessage(sender, "�7�lClaimIt�r�5 Version �e" + Ref.VERSION + "�5 by �4�lits_meow");
			sendMessage(sender, "�b�lSubcommands: ");
			sendMessage(sender, "�e/claimit claim");
			sendMessage(sender, "�e/claimit admin");
			sendMessage(sender, "�bAlias(es): �e" + aliasList);
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		List<String> completions = new LinkedList<String>();
		if(args.length == 1) {
			completions.add("claim");
			completions.add("admin");
		} else {
			if(args[1].equals("claim") && args.length == 2) {
				completions.add("info");
				completions.add("delete");
				completions.add("list");
				completions.add("setname");
			}
		}
		
		return completions;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}
