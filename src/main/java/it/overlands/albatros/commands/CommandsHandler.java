package it.overlands.albatros.commands;

import it.overlands.albatros.Albatros;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class CommandsHandler implements CommandExecutor {
    //commands
    private final String _ATTIVACHEST = "registrachest";
    private final String _FLUSH = "flush";
    private final String _RESETTA = "resetta";
    private final String _IMPORT = "import";
    private final String _TERMINA = "termina";
    private final String _HELP = "help";
    private final String _LISTAPLAYER = "listaplayer";
    private final String _LISTACHEST = "listachest";

    @Override
    public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
        if(s instanceof Player) {
            Player sender = (Player) s;
            if(args.length == 0){/* controlla se è stato scritto qualcosa oltre a /bedwars */}
            else {
                String cmdargs = args[0];
                switch (cmdargs.toLowerCase()) {
                    case _ATTIVACHEST:
                        //inizia la sequenza per piazzare e memorizzare le casse
                        sender.sendMessage("Piazza le chest per registrarle");
                        Albatros.addExecutingPlayer(sender);
                        break;
                    case _TERMINA:
                        /* termina la sequenza per piazzare e memorizzare le casse
                         * SE non avviene in modo automatico
                         * (EG vuoi piazzare solo 3 casse su 5 e fare altro)*/
                        sender.sendMessage("registrazione chest terminata!");
                        Albatros.removeExecutingPlayer(sender);
                        break;
                    case _FLUSH:
                        /*funzione che deve scrivere e aggiornare il database */
                        break;
                    case _HELP:
                        //funzione di help
                        String message ="\\albatros attivachest --> inizia la sequenza per piazzare e memorizzare le casse\n"+
                                "\\albatros termina --> termina la sequenza, le casse attualmente memorizzare rimarranno attive\n"+
                                "\\albatros resetta --> cancella tutte le chest memorizzante";
                        sender.sendMessage(message);
                        break;
                    case _RESETTA:
                        // cancella tutte le chest attive di un player;
                        sender.sendMessage("registro delle tue chest resettato!");
                        Albatros.removeChests2Player(sender);
                    break;
                    case _IMPORT:
                        //funzione che legge il database e importa la roba
                        break;
                    case _LISTAPLAYER:
                        Set<String> players = Albatros.getPlayerList();
                        sender.sendMessage("players registrati: " + players.size());
                        for(String p: players){
                            sender.sendMessage(p);
                        }
                        break;
                        case _LISTACHEST:
                            int size = Albatros.getSizeChestListofPlayer(sender);
                            sender.sendMessage("hai piazzato "+size+ " chests");
                            break;
                    default:
                        sender.sendMessage("comando errato, usa \"/albatros aiuto\" per la lista comandi");
                        break;
                }

            }
        }
        return false;
    }


}
