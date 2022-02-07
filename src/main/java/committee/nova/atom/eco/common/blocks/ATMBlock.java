package committee.nova.atom.eco.common.blocks;

import committee.nova.atom.eco.common.tiles.ATMTile;
import committee.nova.atom.eco.init.ModTiles;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 11:02
 * Version: 1.0
 */
public class ATMBlock extends HorizontalRotatableBlock {


    public ATMBlock() {
        super(Properties.of(Material.GLASS)
                .harvestLevel(1)
                .harvestTool(ToolType.AXE)
                .strength(1.0F, 10.0F)
        );
        setRegistryName("atm");

    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(world.isClientSide || hand != Hand.MAIN_HAND || player.isCrouching()) return ActionResultType.FAIL;
        ATMTile tileEntity = (ATMTile) world.getBlockEntity(pos);
        if( hit.getBlockPos().getY() > 0.5f){
            //NetworkHooks.openGui((ServerPlayerEntity) player, tileEntity, pos);
            NetworkHooks.openGui((ServerPlayerEntity) player, tileEntity, buf -> {
                buf.writeBoolean(true);
                buf.writeBlockPos(pos);
            });
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTiles.ATM.create();
    }
}
