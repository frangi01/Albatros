package it.overlands.albatros.commands;

import it.overlands.albatros.Albatros;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsHandler implements CommandExecutor {
    //commands
    private final String _ATTIVACHEST = "attivachest";
    private final String _FLUSH = "flush";
    private final String _RESETTA = "resetta";
    private final String _IMPORT = "import";
    private final String _TERMINA = "termina";
    private final String _HELP = "aiuto";


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player && (command.getName().equalsIgnoreCase("migrachest") ||
                command.getName().equalsIgnoreCase("mch"))) {
            if(args.length == 0){/* controlla se è stato scritto qualcosa oltre a /bedwars */}
            else {
                String cmdargs = args[0];
                switch (cmdargs.toLowerCase()) {
                    case _ATTIVACHEST:
                        //inizia la sequenza per piazzare e memorizzare le casse
                        Albatros.getInstance().addExecutingPlayer((Player) sender);
                    case _TERMINA:
                        /* termina la sequenza per piazzare e memorizzare le casse
                         * SE non avviene in modo automatico
                         * (EG vuoi piazzare solo 3 casse su 5 e fare altro)*/
                        Albatros.getInstance().removeExecutingPlayer((Player)sender);
                    case _FLUSH:
                        /*funzione che deve scrivere e aggiornare il database */
                    case _HELP:
                        //funzione di help
                        String message ="\\albatros attivachest --> inizia la sequenza per piazzare e memorizzare le casse\n"+
                                "\\albatros termina --> termina la sequenza, le casse attualmente memorizzare rimarranno attive\n"+
                                "\\albatros resetta --> cancella tutte le chest memorizzante";
                        sender.sendMessage(message);
                    case _RESETTA:
                        // cancella tutte le chest attive di un player;
                        Albatros.getInstance().removeChests2Player((Player) sender);
                    case _IMPORT:
                        //funzione che legge il database e importa la roba
                    default:
                        sender.sendMessage("comando errato, usa \"/albatros aiuto\" per la lista comandi");
                }

            }
        }
        return false;
    }


}
