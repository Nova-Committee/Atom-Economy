package committee.nova.atom.eco.common.net.packets;


import com.google.gson.JsonObject;
import committee.nova.atom.eco.utils.JsonUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 20:43
 * Version: 1.0
 */
public class JsonObjectPacket extends IPacket {

    public JsonObject obj;


    public JsonObjectPacket(JsonObject obj){
        this.obj = obj;
    }

    public JsonObjectPacket(PacketBuffer buf){
        int length = buf.readInt();
        String str = buf.toString(buf.readerIndex(), length, StandardCharsets.UTF_8);
        buf.readerIndex(buf.readerIndex() + length);
        obj = JsonUtil.getObjectFromString(str);
    }


    @Override
    public void toBytes(PacketBuffer buffer) {
        byte[] bytes = obj.toString().getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {

    }
}
