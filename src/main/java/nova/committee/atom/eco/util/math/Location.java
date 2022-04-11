//package nova.committee.atom.eco.util.math;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.material.Material;
//import net.minecraft.world.phys.shapes.CollisionContext;
//import net.minecraft.world.phys.shapes.Shapes;
//import net.minecraftforge.common.extensions.IForgeBlockState;
//
//import java.util.List;
//
///**
// * Description:
// * Author: cnlimiter
// * Date: 2022/2/8 15:29
// * Version: 1.0
// */
//public class Location {
//    public Level world;
//    public int x, y, z;
//    private BlockPos blockPos;
//
//    public Location(Level world, BlockPos pos) {
//        this(world, pos.getX(), pos.getY(), pos.getZ());
//    }
//
//    public Location(Level world, int x, int y, int z) {
//
//        this.world = world;
//        this.x = x;
//        this.y = y;
//        this.z = z;
//
//        blockPos = new BlockPos(x, y, z);
//    }
//
//    public Location(BlockEntity tileEntity) {
//        this(tileEntity.getLevel(), tileEntity.getBlockPos().getX(), tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ());
//    }
//
//    public Location(Entity entity) {
//        this(entity.level, entity.blockPosition().getX(), entity.blockPosition().getY(), entity.blockPosition().getZ());
//    }
//
//    public Location(Location location, Direction dir) {
//        this(location, dir, 1);
//    }
//
//    public Location(Location location, Direction dir, int distance) {
//
//        this.world = location.world;
//        this.x = location.x + (dir.getStepX() * distance);
//        this.y = location.y + (dir.getStepY() * distance);
//        this.z = location.z + (dir.getStepZ() * distance);
//
//        blockPos = new BlockPos(x, y, z);
//    }
//
//    public static Location readFromNBT(Level world, CompoundTag nbt) {
//
//        int x = nbt.getInt("locX");
//        int y = nbt.getInt("locY");
//        int z = nbt.getInt("locZ");
//
//        Location loc = new Location(world, x, y, z);
//
//        if (!loc.isZero()) {
//            return loc;
//        }
//
//        return null;
//    }
//
//    public Location translate(Direction dir, int distance) {
//
//        this.x += (dir.getStepX() * distance);
//        this.y += (dir.getStepY() * distance);
//        this.z += (dir.getStepZ() * distance);
//        blockPos = new BlockPos(x, y, z);
//
//        return this;
//    }
//
//    public Location translate(Location location) {
//
//        this.x += location.x;
//        this.y += location.y;
//        this.z += location.z;
//        blockPos = new BlockPos(x, y, z);
//        return this;
//    }
//
//    public Location copy() {
//        return new Location(this.world, this.x, this.y, this.z);
//    }
//
//    public BlockPos getBlockPos() {
//        return blockPos;
//    }
//
//    public IForgeBlockState getForgeBlockState() {
//
//        if (getBlockPos() == null) {
//            return null;
//        }
//
//        return world.getBlockState(getBlockPos());
//    }
//
//    public BlockState getBlockState() {
//
//        if (getForgeBlockState() == null) {
//            return null;
//        }
//
//        return getForgeBlockState().self();
//    }
//
//    public Block getBlock() {
//
//        if (getBlockState() == null) {
//            return null;
//        }
//
//        return getBlockState().getBlock();
//    }
//
//    public void setBlock(Block block) {
//        world.setBlockAndUpdate(getBlockPos(), block.defaultBlockState());
//    }
//
//    public void setBlock(BlockState state) {
//        world.setBlockAndUpdate(getBlockPos(), state.getBlock().defaultBlockState());
//        world.setBlockAndUpdate(getBlockPos(), state);
//    }
//
//    public Material getBlockMaterial() {
//        return getBlockState().getMaterial();
//    }
//
//    public List<ItemStack> getDrops(Player player, ItemStack heldStack) {
//        return Block.getDrops(getBlockState(), (ServerLevel) world, getBlockPos(), null, player, heldStack);
//    }
//
//    public int getLightValue() {
//        return world.getLightEmission(getBlockPos());
//    }
//
//    public BlockEntity getTileEntity() {
//        return world.getBlockEntity(getBlockPos());
//    }
//
//    public double getDistance(Location location) {
//
//        int dx = x - location.x;
//        int dy = y - location.y;
//        int dz = z - location.z;
//
//        return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
//    }
//
//    public void setBlockWithoutNotify(BlockState state) {
//        world.setBlock(getBlockPos(), state, 18);
//    }
//
//    public void setBlock(BlockState state, Player placer) {
//        world.setBlock(getBlockPos(), state, 2);
//        state.getBlock().setPlacedBy(world, getBlockPos(), state, placer, new ItemStack(state.getBlock()));
//    }
//
////    public void breakBlock(PlayerEntity player, ItemStack heldItem) {
////
////        if (player instanceof FakePlayer) {
////            return;
////        }
////
////        if (player instanceof ServerPlayerEntity) {
////            ((ServerPlayerEntity) player).interactionManager.tryHarvestBlock(blockPos);
////        }
////
////        SoundHelper.playBlockPlace(world, player, getBlockState(), this);
////    }
//
//    public void setBlockToAir() {
//        setBlock(Blocks.AIR);
//    }
//
//    public boolean isZero() {
//        return x == 0 && y == 0 && z == 0;
//    }
//
//    public boolean isAirBlock() {
//        return getBlockMaterial() == Material.AIR;
//    }
//
//    public boolean isBlockValidForPlacing() {
//        return getBlockMaterial().isReplaceable() || isAirBlock();
//    }
//
//    public boolean isFullCube() {
//        return getBlockState().isViewBlocking(world, getBlockPos());
//    }
//
//    public boolean isEntityAtLocation(Entity entity) {
//
//        int entityX = entity.blockPosition().getX();
//        int entityY = entity.blockPosition().getY();
//        int entityZ = entity.blockPosition().getZ();
//
//        return entityX == x && entityZ == z && (entityY == y || entityY + 1 == y);
//    }
//
//    public boolean doesBlockHaveCollision() {
//        return getBlock().getCollisionShape(getBlockState(), world, getBlockPos(), CollisionContext.empty()) != Shapes.empty();
//    }
//
//    public void writeToNBT(CompoundTag nbt) {
//        nbt.putInt("locX", x);
//        nbt.putInt("locY", y);
//        nbt.putInt("locZ", z);
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//
//        if (obj instanceof Location) {
//            Location newLoc = (Location) obj;
//            return world == newLoc.world && x == newLoc.x && y == newLoc.y && z == newLoc.z;
//        }
//
//        return super.equals(obj);
//    }
//
//    @Override
//    public String toString() {
//        return "[" + x + ", " + y + ", " + z + "]";
//    }
//}
