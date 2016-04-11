package net.betzel.bytedeco.javacv.bid;

import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * Created by Maurice on 11.04.2016.
 *
 * Blank Image Detection with JavaCV
 * https://github.com/bytedeco/javacv
 */
public class BID {

    public static void main(String[] args) {
        try {
            new BID().execute(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execute(String[] args) throws Exception {
        // If no params provided, compute the defaut image
        BufferedImage bufferedImage = args.length >= 1 ? ImageIO.read(new File(args[0])) : ImageIO.read(this.getClass().getResourceAsStream("/images/A4.jpg"));
        System.out.println("Image type: " + bufferedImage.getType());
        // Convert BufferedImage to Mat
        try (Mat matrix = new OpenCVFrameConverter.ToMat().convert(new Java2DFrameConverter().convert(bufferedImage));
             Mat mask = new Mat();
             Mat gray = new Mat()) {
            cvtColor(matrix, gray, COLOR_BGR2GRAY);
            Mat mean = new Mat();
            Mat stddev = new Mat();
            meanStdDev(gray, mean, stddev);
            printMat(stddev);
            printMat(mean);
            DoubleIndexer doubleIndexer = stddev.createIndexer();
            System.out.println(doubleIndexer.get(0, 0));
            //System.out.println(doubleIndexer.get(1, 0));
            //System.out.println(doubleIndexer.get(2, 0));

        }
    }

    private static void printMat(Mat mat) {
        System.out.println("Channels: " + mat.channels());
        System.out.println("Rows: " + mat.rows());
        System.out.println("Cols: " + mat.cols());
        System.out.println("Type: " + mat.type());
        System.out.println("Dims: " + mat.dims());
        System.out.println("Depth: " + mat.depth());
    }

}
