package com.program.copyAgent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileController {
    private static final Logger logger = LogManager.getLogger(FileController.class);
    private static final String cpInDir = System.getenv("CP_INDIR_PATH");
    private static final String cpOutDir = System.getenv("CP_OUTDIR_PATH");
    static Scanner sc = new Scanner(System.in);
    public static String srcFile = "";
    public static Path srcPath = null;
    public static int copyNum = 0;
    public static int interval = 0;
    public static enum message {
        srcPath("Enter the file name to copy"),
        copyNum("Enter the number of files to copy"),
        interval("Enter the time(ms) interval for copying"),
    	repeat("Repeat again under the same conditions? (y/n)");

        private final String msg;
        message(String msg) {
            this.msg = msg;
        }
    }

    public static void run() {
    	while (true) {
    		srcPath = readFile(message.srcPath.msg);
    		copyNum = inputNum(message.copyNum.msg);
    		interval = inputNum(message.interval.msg);
    		copyFile(srcPath, srcFile, copyNum, interval);
    		
    		while (reRun()) {
    			copyFile(srcPath, srcFile, copyNum, interval);
    		}
    		
    	}
    }

    public static Path readFile(String msg) {
        boolean existsFile = false;
        while (!existsFile) {
            logger.info(msg);
            srcFile = sc.next();
            srcPath = Paths.get(cpInDir + File.separator + srcFile);

            if(Files.exists(srcPath, LinkOption.NOFOLLOW_LINKS)) {
                existsFile = true;
            } else {
                logger.error(File.separator + srcPath + " does not exist");
            }
        }
        return srcPath;
    }

    public static int inputNum(String msg) {
        int num = 0;
        boolean isNum = false;

        while (!isNum) {
            logger.info(msg);
            
            try {
                num = sc.nextInt();
                isNum = true;
            } catch (InputMismatchException e) {
                sc = new Scanner(System.in);
                logger.error("Enter the only num");
            }
        }
        return num;
    }

    public static void copyFile(Path srcFilePath, String srcFileName, int copyNum, int interval) {
        String copyFile = "";
        String modDate = "";
    	String outDir = cpOutDir;
        String fileName = srcFileName.substring(0, srcFileName.indexOf("."));
        String extension = srcFileName.substring(srcFileName.indexOf(".") + 1, srcFileName.length());

        try {
            for (int num = 1; num <= copyNum; num++) {
            	modDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            	copyFile = outDir + File.separator + fileName + "_" + modDate;
                Path copyFilePath = Paths.get(copyFile + "_" + num + "." + extension);
                Files.copy(srcFilePath, copyFilePath);
                logger.info("File Copy Successful - " + copyFilePath);
                Thread.sleep(interval);
            }
        } catch (IOException e) {
            logger.error("IOException", e);
        } catch (InterruptedException e) {
            logger.error("InterruptedException", e);
        }
    }
    
    public static boolean reRun() {
    	logger.info(message.repeat.msg);
    	boolean reFlag = false;
    	
    	while (!reFlag) {
    		String flag = sc.next();
    		
    		if (flag.equals("y")) {
    			return true;
    		} else if (flag.equals("n")) {
    			return false;
    		} else {
    			logger.error("Enter the y or n");
    			reFlag = false;
    		}
    	}
    	
    	return false;
    }
    
}
