package committee.nova.atom.eco.utils;

import com.google.common.collect.ImmutableMap;
import committee.nova.atom.eco.Eco;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Optional;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 21:12
 * Version: 1.0
 */
public class BufferUtil {

    public static Vector3d readVec3d(PacketBuffer buffer) {
        return new Vector3d
                (
                        buffer.readDouble(),
                        buffer.readDouble(),
                        buffer.readDouble()
                );
    }

    public static PacketBuffer writeVec3d(PacketBuffer buffer, Vector3d vec3d) {
        buffer.writeDouble(vec3d.x);
        buffer.writeDouble(vec3d.y);
        buffer.writeDouble(vec3d.z);

        return buffer;
    }

    public static BlockState readState(PacketBuffer buffer)
    {
        String id = buffer.readUtf();
        int size = buffer.readInt();

        Block block = Registry.BLOCK.get(new ResourceLocation(id));
        BlockState state = block.defaultBlockState();
        StateContainer<Block, BlockState> stateContainer = block.getStateDefinition();
        for (int i = 0; i < size; i++)
        {
            String p = buffer.readUtf();
            Property<?> property = stateContainer.getProperty(p);
            String v = buffer.readUtf();
            if (property != null)
            {
                state = setValueHelper(state, property, v);
            }
            else
            {
                Eco.LOGGER.warn("Unknown property {} for {}", p, block);
            }
        }

        return state;
    }

    private static <S extends BlockState, T extends Comparable<T>> S setValueHelper(S state, Property<T> property, String valueName)
    {
        Optional<T> optional = property.getValue(valueName);
        if (optional.isPresent())
        {
            return (S)(state.setValue(property, (T)(optional.get())));
        }
        else
        {
            Eco.LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", property.getName(), valueName, state.toString());
            return state;
        }
    }

    public static PacketBuffer writeState(PacketBuffer buffer, BlockState state)
    {
        String id = Registry.BLOCK.getKey(state.getBlock()).toString();
        ImmutableMap<Property<?>, Comparable<?>> values = state.getValues();
        int size = values.size();
        buffer.writeUtf(id);
        buffer.writeInt(size);

        for(Map.Entry<Property<?>, Comparable<?>> entry : values.entrySet())
        {
            Property<?> property = entry.getKey();
            String name = property.getName();
            String value = getName(property, entry.getValue());
            buffer.writeUtf(name);
            buffer.writeUtf(value);
        }

        return buffer;
    }

    public static <T extends Comparable<T>> String getName(Property<T> property, Comparable<?> value) {
        return property.getName((T)value);
    }
}
