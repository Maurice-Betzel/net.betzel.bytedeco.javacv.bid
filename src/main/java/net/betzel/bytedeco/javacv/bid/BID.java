package net.betzel.bytedeco.javacv.bid;

import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.javacv.CanvasFrame;
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
 * <p>
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
        BufferedImage bufferedImage = args.length >= 1 ? ImageIO.read(new File(args[0])) : ImageIO.read(this.getClass().getResourceAsStream("/images/scanblank.png"));
        System.out.println("Image type: " + bufferedImage.getType());
        // Convert BufferedImage to Mat
        try (Mat matrix = new OpenCVFrameConverter.ToMat().convert(new Java2DFrameConverter().convert(bufferedImage));
             Mat gray = new Mat()) {
            cvtColor(matrix, gray, COLOR_BGR2GRAY);
            Mat mean = new Mat();
            Mat stddev = new Mat();
            showMatrix("gray", gray);
            meanStdDev(gray, mean, stddev);
            DoubleIndexer doubleIndexer = stddev.createIndexer();
            System.out.println(doubleIndexer.get(0, 0));
            doubleIndexer.release();
            System.out.println(gray.cols() + " " + gray.rows());
            // remove approx 5% of image borders to get rid of scan artifacts
            int border = gray.cols() / 100 * 5;
            Mat roi = gray.apply(new Rect(border, border, gray.cols() - border * 2, gray.rows() - border * 2));
            showMatrix("roi", roi);
            meanStdDev(roi, mean, stddev);
            doubleIndexer = stddev.createIndexer();
            double result = doubleIndexer.get(0, 0);
            System.out.println(result);
            doubleIndexer.release();
            System.out.println(border + " " + roi.cols() + " " + roi.rows());
            // if using 3 channels you get a result for every channel
            //System.out.println(doubleIndexer.get(1, 0));
            //System.out.println(doubleIndexer.get(2, 0));
            // the threshold is set on personal preference and experience
            double threshold = 1.5;
            if(result < threshold) {
                System.out.println("Blank image!");
            } else {
                System.out.println("Non blank image!");
            }
        }
    }

    private void showMatrix(String title, Mat matrix) {
        CanvasFrame canvas = new CanvasFrame(title, 1);
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvas.setCanvasSize(matrix.size().width() / 4, matrix.size().height() / 4);
        OpenCVFrameConverter converter = new OpenCVFrameConverter.ToIplImage();
        canvas.showImage(converter.convert(matrix));
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