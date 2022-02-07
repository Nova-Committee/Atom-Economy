package committee.nova.atom.eco.utils;

import committee.nova.atom.eco.Static;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 9:53
 * Version: 1.0
 */
public class PrintUtil {
    private static final Logger logger = LogManager.getLogger("AtomL");

    public static void log(Object obj){
        if(obj instanceof Iterable){
            Iterable<?> inte = (Iterable<?>)obj;
            logger.info("ITERABLE: {");
            for(Object object : inte){
                logger.info("    " + (object == null ? ">> IS null;" : String.valueOf(object)));
            }
            logger.info("}");
            return;
        }
        if(obj instanceof Throwable){
            logger.info(ExceptionUtils.getStackTrace((Throwable)obj));
        }
        logger.info(obj == null ? "[OBJ IS NULL]" : String.valueOf(obj));
    }

    public static void log(Object prefix, Object obj){
        logger.info("[" + String.valueOf(prefix) + "]> " + String.valueOf(obj));
    }


    public static void debug(Object obj){
        if(Static.dev()){
            log(obj);
        }
    }

    public static <T> T debugR(Object obj){
        if(Static.dev()){ log(obj); }
        return (T)obj;
    }

    @SafeVarargs
    public static <T> void debug(T... objs){
        if(Static.dev()){
            String str = "[\n";
            for(int i = 0; i < objs.length; i++){
                str += "\t" + (objs[i] == null ? ">> IS null;" : String.valueOf(objs[i]) + (i == objs.length - 1 ? "" : ", ")) + "\n";
            }
            log(str + "]");
        }
    }

    public static final void console(String url){
        System.out.println(url);
    }

    public static final void console(Object... objects){
        console(true, objects);
    }

    public static final void console(boolean newlines, Object... objects){
        StringBuffer buff = new StringBuffer();
        buff.append("[ " + (newlines && objects.length > 1 ? "\n" : ""));
        for(int i = 0; i < objects.length; i++){
            buff.append(objects[i] == null ? "%=NULL=%" : objects[i].toString());
            if(i < objects.length - 1) buff.append(newlines ? ",\n" : ", ");
        }
        buff.append((newlines && objects.length > 1 ? "\n" : " ") + "]");
        System.out.println(buff.toString());
    }
}
