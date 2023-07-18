package fr.mrpiano.hardium.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WCDoorItem extends MultiBlockItem
{
    public WCDoorItem(Block id)
    {
        super(id);
        this.maxStackSize = 1;
        //this.setCreativeTab(def.getCreativeTab());
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10)
    {
        if (world.isRemote)
        {
            return true;
        }
        if (side != 1)
        {
            return false;
        }
        else
        {
            ++y;

            if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack))
            {
                Block blk = getBlock();
                if ((!blk.canPlaceBlockAt(world, x, y, z)) ||
                    (!world.canPlaceEntityOnSide(blk, x, y, z, false, side, player, stack))) {
                    return false;
                }
                else
                {
                    int i1 = MathHelper.floor_double((double)((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
                    ItemDoor.placeDoorBlock(world, x, y, z, i1, blk);
                    --stack.stackSize;
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
    }

    @Override
    public int getMetadata(int damage) {
        return 0;
    }
}
