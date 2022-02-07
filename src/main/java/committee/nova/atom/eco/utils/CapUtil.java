package committee.nova.atom.eco.utils;

import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapUtil {
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static <T> T get(@Nonnull LazyOptional<T> lazyOptional) {
        return lazyOptional.orElse(null);
    }

}
