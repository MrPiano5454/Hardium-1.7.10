package fr.mrpiano.hardium.blocks;

import java.util.ArrayList;
import java.util.List;

import fr.mrpiano.hardium.HardiumBlockDef;
import fr.mrpiano.hardium.HardiumFactory;
import fr.mrpiano.hardium.HardiumLifecycle;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import fr.mrpiano.hardium.items.WCCuboidNEItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WCCuboidNEBlock extends WCCuboidBlock implements HardiumLifecycle {

    public static class Factory extends HardiumFactory {
        @Override
        public Block[] buildBlockClasses(HardiumBlockDef def) {
            def.setMetaMask(0x7);
            if (!def.validateMetaValues(new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }, new int[] { 0 })) {
                return null;
            }
            return new Block[] { new WCCuboidNEBlock(def) };
        }
    }
    
    private List<HardiumBlockDef.Cuboid> cuboids_by_meta[];
    
    @SuppressWarnings("unchecked")
    protected WCCuboidNEBlock(HardiumBlockDef def) {
        super(def);
        // Set rotations (for effects placement)
        this.metaRotations = new HardiumBlockDef.CuboidRotation[]{ HardiumBlockDef.CuboidRotation.NONE, HardiumBlockDef.CuboidRotation.ROTY90 };
        
        cuboids_by_meta = (List<HardiumBlockDef.Cuboid>[])new List[16];
        for (int i = 0; i < 8; i++) {
            List<HardiumBlockDef.Cuboid> lst = def.getCuboidList(i);
            if (lst == null) continue;
            cuboids_by_meta[i] = lst;
            cuboids_by_meta[i+8] = new ArrayList<HardiumBlockDef.Cuboid>();
            for (HardiumBlockDef.Cuboid c : lst) {
                cuboids_by_meta[i+8].add(c.rotateCuboid(HardiumBlockDef.CuboidRotation.ROTY90));
            }
            setBoundingBoxFromCuboidList(i+8);
        }
    }

    public boolean registerBlockDefinition() {
        def.doStandardRegisterActions(this, WCCuboidNEItem.class);
        
        return true;
    }
    
    @Override
    public int damageDropped(int meta) {
        return meta & 0x7;
    }
        
    /**
     *  Get cuboid list at given meta
     *  @param meta
     */
    public List<HardiumBlockDef.Cuboid> getCuboidList(int meta) {
        return cuboids_by_meta[meta];
    }
    
    /**
     * Set active cuboid during render
     */
    @Override
    public void setActiveRenderCuboid(HardiumBlockDef.Cuboid c, RenderBlocks renderer, int meta, int index) {
        super.setActiveRenderCuboid(c, renderer, meta, index);
        int dir = (meta >> 3);
        if ((c != null) && (dir == 1)) {
            renderer.uvRotateTop = 1;
            renderer.uvRotateBottom = 2;
        }
        else {
            renderer.uvRotateTop = renderer.uvRotateBottom = 0;
        }
    }
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return (def.alphaRender?1:0);
    }
}
