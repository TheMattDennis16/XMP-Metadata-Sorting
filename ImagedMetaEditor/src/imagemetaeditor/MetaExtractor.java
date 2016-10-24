package imagemetaeditor;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MetaExtractor {
    
    private enum Destination {
        NEWSPAPER,
        SPORT_MAGAZINE,
        SHOWBIZ_WEBSITE,
        ERROR
    }
    private static String[] folderNames = { "newspaper", "sport magazine", "showbiz website" };
    
    private static void checkFoldersExist() {
        for(String name : folderNames) {
            FileHandling.checkFolderExists(name);
        }
    }
    
    private static Destination toFileInLocation(String name) {
        name = name.toLowerCase();
        if(name.contains("editorial contributor"))
            return Destination.NEWSPAPER;
        else if (name.contains("sport contributor"))
            return Destination.SPORT_MAGAZINE;
        else if (name.contains("singer") 
            || name.contains("dance") 
            || name.contains("showbiz")) {
            return Destination.SHOWBIZ_WEBSITE;
        } 
        else return Destination.ERROR;
            
    }
    
    private static String extractFromFile(File file) {
        try {
            //Generic approach, rather than JpegMetadataReader as the type may
            //not be known in future.
            Metadata meta = ImageMetadataReader.readMetadata(file);
            
            for(Directory dir : meta.getDirectories()) {
                for(Tag tag : dir.getTags()) {
                    if(tag.hasTagName() 
                        && tag.getTagName().toLowerCase().equals("copyright notice")) {
                        return tag.toString();
                    }
                }
            }
        }
        catch (IOException e) {
            System.out.println(Errors.errInExtraction);
        } 
        catch (ImageProcessingException ex) {
            System.out.println(Errors.xmpCreationErr);
        }
        return null;
    }
    
    //Return value indicates whether an error occured or not.
    private static boolean moveToNewFolder(Destination folder, File file) {
        try {
            int index = 0;
            if(folder == Destination.NEWSPAPER)
                index = 0;
            else if (folder == Destination.SPORT_MAGAZINE)
                index = 1;
            else if (folder == Destination.SHOWBIZ_WEBSITE) 
                index = 2;
            
            Path destination = Paths.get(folderNames[index] + "/" + file.getName());
            if(Files.exists(destination)) {
                System.out.println(Errors.fileAlreadyInDir);
                return true;
            }
            Files.copy(file.toPath(), destination);
        }
        catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }
    
    public static void processFolder(String folder) {
         ArrayList<File> files = FileHandling.getFilesInPath(folder);
         for (File file : files) {
             String copyrightNotice = extractFromFile(file);
             if(copyrightNotice == null) {
                 System.out.println(Errors.noCopyrightFound);
                 continue;
             }
             
             Destination result = toFileInLocation(copyrightNotice);
             if(result == Destination.ERROR) {
                 System.out.println(Errors.couldntSortNotice);
                 continue;
             }
             
             checkFoldersExist();
             boolean errWhileMove = moveToNewFolder(result, file);
             if(errWhileMove) {
                 System.out.println(Errors.couldntMoveFile);
             }
         }
    }
}
