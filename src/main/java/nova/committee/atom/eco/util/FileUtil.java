package nova.committee.atom.eco.util;


import java.io.File;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/1 15:30
 * Version: 1.0
 */
public class FileUtil {

    public static void Check(File file) {
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("Failed to create necessary folder!");
            }
        }
    }

    public static File createSubFile(String fileName, File parrentFolder) {
        return new File(parrentFolder.getAbsolutePath() + "/" + fileName);
    }


}
