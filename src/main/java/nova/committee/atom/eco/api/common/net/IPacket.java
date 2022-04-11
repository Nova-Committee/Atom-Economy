package nova.committee.atom.eco.api.common.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 20:59
 * Version: 1.0
 */
public abstract class IPacket {

    public abstract void toBytes(FriendlyByteBuf buffer);

    public abstract void handle(Supplier<NetworkEvent.Context> ctx);

}
