package nova.committee.atom.eco.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import nova.committee.atom.eco.api.common.block.HorizontalRotatableBlock;
import nova.committee.atom.eco.common.menu.ATMMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 11:02
 * Version: 1.0
 */
public class ATMBlock extends HorizontalRotatableBlock {

    public static final TranslatableComponent TITLE = new TranslatableComponent("container.atm");

    public ATMBlock() {
        super(Properties.of(Material.GLASS)
                .strength(1.0F, 10.0F)
        );
        setRegistryName("atm");
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));

    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState p_60563_, Level p_60564_, BlockPos p_60565_) {
        return new SimpleMenuProvider((p_48785_, p_48786_, p_48787_) -> {
            return ATMMenu.getServerSideInstance(p_48785_, p_48786_);
        }, TITLE);
    }


    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (world.isClientSide || hand != InteractionHand.MAIN_HAND || player.isCrouching()) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(world, pos));
            return InteractionResult.CONSUME;
        }


    }


}
