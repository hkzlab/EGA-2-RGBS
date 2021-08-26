package info.hkzlab.ega2rgbs.epromgen;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.*;

import info.hkzlab.ega2rgbs.epromgen.utilities.EPROMTools;

public class App {
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    private final static String version = App.class.getPackage().getImplementationVersion();

    private static String outFile;

    public static void main(String[] args) throws Exception {
        logger.info("EGA2RGBS EPROM generator " + version);

        if (args.length < 1) {
            logger.error("Wrong number of arguments passed.\n" + "ega2rgbs <outfile>\n\n");

            return;
        }

        parseArgs(args);

        logger.info("Building the buffer!");

        byte[] buf = EPROMTools.buildBuffer();

        writeBinary(buf);
        //writeTable(buf);
    }

    private static void writeBinary(byte[] buf) throws IOException {
        int count = 128; // Write the file 128 times, to build an image fit for a 27C010
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
        while(count-- > 0) bos.write(buf);
        bos.flush();
        bos.close();        
    }

    private static void writeTable(byte[] buf) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
        
        bos.write(".i 10\n".getBytes());
        bos.write(".o 8\n".getBytes());
        bos.write(".ilb mode bh bl gh gli rh rl vs hs bfix\n".getBytes());
        bos.write(".ob obh obl ogh ogl orh orl ocs oncs\n".getBytes());
        bos.write(".phase 11111111\n\n".getBytes());
       
        for(int idx = 0; idx < buf.length; idx++) {
            StringBuffer line = new StringBuffer();

            for(int aidx = 0; aidx < 10; aidx++) {
                line.append(((idx >> aidx) & 0x01) != 0 ? '1' : '0');
            }

            line.append(' ');

            for(int aidx = 0; aidx < 8; aidx++) {
                line.append(((buf[idx] >> aidx) & 0x01) != 0 ? '1' : '0');
            }

            line.append('\n');
            bos.write(line.toString().getBytes());
        }
        
        bos.write(".e\n".getBytes());

        bos.flush();
        bos.close();   
    }

    private static void parseArgs(String[] args) {
        outFile = args[0];

        checkFilePath(outFile);
    }

    private static void checkFilePath(String path) {
        File file = new File(path);

        boolean exists = file.exists();
        boolean isDirectory = file.isDirectory();
        boolean isWritable = file.canWrite();

        if(isDirectory) {
            logger.error("Path " + path + " points to a directory, please specify an output file!");
            System.exit(-1);
        }

        if(exists && !isWritable) {
            logger.error("Path " + path + " does not point to a writable file!");
            System.exit(-1);
        }
    }
}
