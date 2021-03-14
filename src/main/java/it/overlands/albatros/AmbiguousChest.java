package it.overlands.albatros;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;

public class AmbiguousChest {
    private Chest chest;
    private DoubleChest doubleChest;
    private String type;

    public AmbiguousChest(Chest chest) {
        this.chest = chest;
        this.type = "c";
    }
    public AmbiguousChest(DoubleChest doubleChest) {
        this.doubleChest = doubleChest;
        this.type = "dc";
    }
}
