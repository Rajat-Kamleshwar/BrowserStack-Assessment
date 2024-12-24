package Utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebElement;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ImageSaveUtil {
    String imagePath = System.getProperty("user.dir") + File.separator + "ArticleCoverImages";

    public void saveImageFromWebsite(List<WebElement> element){
        int count = 1;
        for (WebElement ele : element){
            String imageUrl = ele.getAttribute("src");
            System.out.println("\nImage URL:\t" + imageUrl + "\n");

            // Path to save the image
            String path = imagePath +  File.separator + "CoverImage_" + count + ".jpg";
            try {
                FileUtils.copyURLToFile(new URL(imageUrl), new File(path));
                System.out.println("---------------Image Saved Successfully!---------------");
                count++;
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    public void emptyDirectory(){
        // Empty the directory before saving images to ensure no previous images are overridden with new ones
        File directory = new File(imagePath);
        try {
            if (directory.exists() && directory.isDirectory()) {
                FileUtils.cleanDirectory(directory);
                System.out.println("Image Folder cleared successfully!");
            } else {
                System.out.println("Image Folder does not exist!");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
