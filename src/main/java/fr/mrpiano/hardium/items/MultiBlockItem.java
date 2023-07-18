package fr.mrpiano.hardium.items;


import fr.mrpiano.hardium.HardiumLifecycle;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MultiBlockItem extends ItemBlock {
    private Block blk;

    public MultiBlockItem(Block par1) {
        super(par1);
        setMaxDamage(0);
        setHasSubtypes(true);
        this.blk = par1;
    }

    protected Block getBlock() {
        return blk;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack itemstack, int par2) {
        return getBlock().getRenderColor(itemstack.getItemDamage());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int meta) {
        Block b = getBlock();
        if (b instanceof HardiumLifecycle) {
            return ((HardiumLifecycle)b).getWBDefinition().getItemIcon(meta);
        }
        return getBlock().getIcon(0, meta);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return super.getUnlocalizedName(itemstack) + "_" + itemstack.getItemDamage();
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IIconRegister iconRegister)
    {
        Block b = getBlock();
        if (b instanceof HardiumLifecycle) {
            ((HardiumLifecycle)b).getWBDefinition().doStandardItemRegisterIcons(iconRegister);;
        }
    }
}

