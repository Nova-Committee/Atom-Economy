package committee.nova.atom.eco.utils;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 9:45
 * Version: 1.0
 */
public class UCResourceLocation extends ResourceLocation {
    public UCResourceLocation(String... resourceName){
        super(resourceName.length < 2 ? new String[]{"§", resourceName[0]} : resourceName);
        Field domain = ResourceLocation.class.getDeclaredFields()[0];
        domain.setAccessible(true);
        Field path = ResourceLocation.class.getDeclaredFields()[1];
        path.setAccessible(true);
        try {
            domain.set(this, resourceName[0].replace("§", ""));
            path.set(this, resourceName[1].indexOf(":") == 0 ? resourceName[1].substring(1) : resourceName[1]);
        }
        catch(IllegalArgumentException | IllegalAccessException e){
            e.printStackTrace();
        }
        Validate.notNull(this.path);
    }

    public UCResourceLocation(ResourceLocation rs){
        this(rs.getNamespace(), rs.getPath());
    }

}
