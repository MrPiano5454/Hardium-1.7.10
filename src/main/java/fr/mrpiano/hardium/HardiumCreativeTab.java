package fr.mrpiano.hardium;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
public class HardiumCreativeTab extends CreativeTabs {

    public static final CreativeTabs tabWesterosBlocks = new HardiumCreativeTab("WesterosBlocks", "Westeros Blocks", "metal_block_0");
    public static final CreativeTabs tabWesterosDecorative = new  HardiumCreativeTab("WesterosDeco", "Westeros Decorative", "metal_block_0_stair_1");
    public static final CreativeTabs tabWesterosPlants = new  HardiumCreativeTab("WesterosPlants", "Westeros Plants", "yellow_flower_block_0");
    public static final CreativeTabs tabWesterosSounds = new  HardiumCreativeTab("WesterosSounds", "Westeros Sounds", "sound_blocks_0");

    public static void init() {

    }

    private String lbl;
    private String type;
    private Block blk = null;
    public  HardiumCreativeTab(String id, String label, String type) {
        super(id);
        lbl = label;
        this.type = type;
        HardiumBlockDef.addCreativeTab(id,  this);
    }

    @Override
    public ItemStack getIconItemStack() {
        if (blk == null) {
            blk = Hardium.findBlockByName(this.type);
            if (blk == null) {
                blk = Hardium.customBlocks[0];
            }
        }
        return new ItemStack(blk, 1, 0);
    }
    @Override
    public String getTranslatedTabLabel() {
        return lbl;
    }

    @Override
    public Item getTabIconItem() {
        return getIconItemStack().getItem();
    }
}
