package pt.ulisboa.tecnico.cnv.compression;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.sun.net.httpserver.HttpHandler;
import pt.ulisboa.tecnico.cnv.javassist.tools.MethodStatistic;

public class CompressImageHandlerImpl extends BaseCompressingHandler implements HttpHandler {

    public CompressImageHandlerImpl() {
    }

    public CompressImageHandlerImpl(List<MethodStatistic> methodStatistics) {
        super(methodStatistics);
    }

    @Override
    byte[] process(BufferedImage bi, String targetFormat, float compressionQuality) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(targetFormat);
            ImageWriter writer = (ImageWriter) writers.next();

            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();

            String compressionType = getCompressionType(targetFormat);
            System.out.printf("Available compression types for the %s format: %s%n", targetFormat,
                    Arrays.toString(param.getCompressionTypes()));
            System.out.println(param.canWriteCompressed());
            if (compressionType != null) {
                System.out.printf("Compression type chosen: %s, compression quality: %f%n",
                        compressionType, compressionQuality);
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionType(compressionType);
                param.setCompressionQuality(compressionQuality);
            }

            writer.write(null, new IIOImage(bi, null, null), param);
            byte[] result = baos.toByteArray();

            ios.close();
            baos.close();
            writer.dispose();

            return result;
        } catch (IOException e) {
            throw e;
        }
    }

    private static String getCompressionType(String targetFormat) {
        switch (targetFormat) {
            case "jpg":
            case "JPG":
            case "jpeg":
            case "JPEG":
                return "JPEG";
            case "gif":
            case "GIF":
                return "LZW";
            case "bmp": // supported types:
            case "BMP": // [BI_RGB, BI_RLE8, BI_RLE4, BI_BITFIELDS, BI_JPEG, BI_PNG]
                return "BI_RGB";
            case "png":
            case "PNG":
                return "Deflate";
            case "tiff": // supported types:
            case "TIFF": // [CCITT RLE, CCITT T.4, CCITT T.6, LZW, JPEG, ZLib, PackBits, Deflate, EXIF JPEG]
                return "ZLib";
            default:
                return null;
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.err.println("Expected arguments: <input> <format> <quality> <output>");
            System.exit(1);
        }
        String inputFilename = args[0];
        String targetFormat = args[1];
        float compressionQuality = Float.parseFloat(args[2]);
        String outputFile = args[3];

        File inputFile = new File(inputFilename);
        BufferedImage bi = ImageIO.read(inputFile);

        byte[] resultImage = new CompressImageHandlerImpl().process(bi, targetFormat, compressionQuality);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(resultImage);
        }
    }
}
