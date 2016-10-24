package imagemetaeditor;

import java.util.ArrayList;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Matt
 */
public class FileHandling {
    public static void checkFolderExists(String fileName) {
        try {
            if (!Files.exists(Paths.get(fileName))) {
                Files.createDirectory(Paths.get(fileName));
            }
        } catch (Exception e) {
            System.out.println(Errors.couldntCheckFolderExists);
        }
    }
    
    public static ArrayList<File> getFilesInPath(String file)
    {
        ArrayList<File> files = new ArrayList<>();
        try
        {
            File[] filesArray = new File(file).listFiles(File::isFile);
            
            if(filesArray.length == 0) {
                System.out.println(Errors.noFilesFound);
                return null;
            }
            
            for (File fileInDirectory : filesArray) {
                files.add(fileInDirectory);
            }
        }
        catch (Exception e)
        {
            System.out.println(Errors.errInFileHandling);
            files = null;
        }
        return files;
    }
}
