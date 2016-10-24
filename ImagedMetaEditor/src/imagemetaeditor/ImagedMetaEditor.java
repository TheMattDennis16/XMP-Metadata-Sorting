package imagemetaeditor;

public class ImagedMetaEditor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String filePath = "TestImages";
        MetaExtractor.processFolder(filePath);
    }
    
}
