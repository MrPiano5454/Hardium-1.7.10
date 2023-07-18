package fr.mrpiano.hardium.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WCCuboidNEStackItem extends MultiBlockItem {

    public WCCuboidNEStackItem(Block par1) {
        super(par1);
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
        int dir = 0;

        switch (side) {
            case 0:
                --y;
                dir = MathHelper.floor_double((double)((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
                break;
            case 1:
                ++y;
                dir = MathHelper.floor_double((double)((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
                break;
            case 2:
                --z;
                dir = 1;
                break;
            case 3:
                ++z;
                dir = 3;
                break;
            case 4:
                --x;
                dir = 0;
                break;
            case 5:
                ++x;
                dir = 2;
                break;
        }
        // Check if we can change both blocks
        if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
            Block block = getBlock();

            if ((block == null) || (!block.canPlaceBlockOnSide(world, x, y, z, side))) {
                return false;
            }
            else if (!world.canPlaceEntityOnSide(block, x, y, z, false, side, player, stack)) {
                world.notifyBlocksOfNeighborChange(x, y, z, block, 0);
                return false;
            }
            else {
                placeCuboidBlock(world, x, y, z, dir, block, stack.getItemDamage() & 0x6);
                --stack.stackSize;
                return true;
            }
        }
        else {
            return false;
        }
    }
    public static void placeCuboidBlock(World world, int x, int y, int z, int side, Block block, int meta)
    {
        meta += (8 * (side % 2));

        world.setBlock(x, y, z, block, meta, 3);
        if (world.getBlock(x, y, z) == block) {
            world.setBlock(x, y + 1, z, block, meta | 0x1, 3);
            world.notifyBlocksOfNeighborChange(x, y, z, block);
            world.notifyBlocksOfNeighborChange(x, y + 1, z, block);
        }
    }
}
