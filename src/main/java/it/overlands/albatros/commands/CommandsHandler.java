package it.overlands.albatros.commands;

import it.overlands.albatros.Albatros;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandsHandler implements CommandExecutor {
    //commands
    private final String _REGISTRA = "registra";
    //private final String _RESETTA = "resetta";
    private final String _TERMINA = "termina";
    private final String _HELP = "aiuto";
    //private final String _LISTAPLAYER = "listaplayers";
    //private final String _LISTACHESTS = "listachests";
    //private final String _RESETTATUTTI = "resettatutti";

    @Override
    public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
        if(s instanceof Player) {
            Player sender = (Player) s;
            if(args.length == 0){/* controlla se è stato scritto qualcosa oltre a /bedwars */}
            else {
                String cmdargs = args[0];
                switch (cmdargs.toLowerCase()) {
                    case _REGISTRA:
                        // togli il  nick
                        Albatros.getInstance().getServer().dispatchCommand(Albatros.getInstance().getServer().getConsoleSender(),"nick off "+sender.getName());
                        //inizia la sequenza per piazzare e memorizzare le casse
                        if(Albatros.getExecutingPlayers().contains(sender.getName())){
                            //comando già attivo
                            sender.sendMessage("Comando già attivo: piazza le chests o scrivi \"/albatros termina\" per completare la procedura");
                        }else {
                            //comando non attivo
                            try {
                                if (Albatros.getOnePlayerChestMap(sender.getName()).size() == Albatros.get_MAXNUMCHEST()) {
                                    //max numero chest raggiunto
                                    sender.sendMessage("Numero massimo di chests raggiunto");
                                    break;
                                }
                            }catch (NullPointerException ex){
                                //prima volta che usa /albatros registra, non fare nulla
                            }
                                sender.sendMessage("Piazza le chest per registrarle");
                                Albatros.addExecutingPlayer(sender.getName());
                        }
                        break;
                    case _TERMINA:
                        /** termina la sequenza per piazzare e memorizzare le casse
                         * SE non avviene in modo automatico
                         * (EG vuoi piazzare solo 3 casse su 5 e fare altro)**/

                        sender.sendMessage("registrazione chests terminata!");
                        Albatros.removeExecutingPlayer(sender.getName());
                        break;
                    case _HELP:
                        //funzione di help
                        String message = "\\albatros registra --> inizia la sequenza per piazzare e memorizzare le casse\n"+
                                "\\albatros termina --> termina la sequenza, le casse attualmente memorizzare rimarranno attive\n"+
                                "\\albatros listachests --> numero chest piazzate\n";
                        sender.sendMessage(message);
                        break;
                   /* case _RESETTA:
                        // cancella tutte le chest attive di un player;
                        Albatros.removeChests2Player(sender);
                        sender.sendMessage("registro delle tue chests resettato!");
                        break;
                    case _RESETTATUTTI:
                        // cancella tutte le chest attive di un player;
                        Albatros.resetPlayerChestsMap();
                        sender.sendMessage("registro dei player resettato!");
                        break;
                    case _LISTAPLAYER:
                        Set<String> players = Albatros.getPlayerList();
                        sender.sendMessage("players registrati: " + players.size());
                        for(String p: players){
                            sender.sendMessage(p);
                        }
                        break;
                        case _LISTACHESTS:
                            int size = Albatros.getSizeChestListofPlayer(sender);
                            if(size == -1 || size == 0){
                                sender.sendMessage("Non hai ancora registrato chests!");
                                break;
                            }
                            else {
                                sender.sendMessage("hai piazzato " + size + " chests");
                                break;
                            }*/
                    default:
                        sender.sendMessage("comando errato, usa \"/albatros aiuto\" per la lista comandi");
                        break;
                }

            }
        }
        return false;
    }


}
