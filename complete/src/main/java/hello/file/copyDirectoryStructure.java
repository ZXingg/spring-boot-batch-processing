package hello.file;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by ZXing at 2018/4/13
 * QQ:1490570560
 */
public class copyDirectoryStructure {

    private static String targetDir;

    static {
        try {
            targetDir = new ClassPathResource("sample-data.csv").getFile().getParent();
            System.out.println("目标文件夹：" + targetDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            copy("E:\\java\\Image\\self", "\\self", targetDir + "\\new", "");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static FileInputStream in;
    private static FileOutputStream out;

    private static void copy(String sourcePath, String sourceRoot, String targetPath, String filterSuffix) {

        File[] sourceFiles = new File(sourcePath)
                .listFiles(
                        (file -> file.isDirectory() || file.getName().endsWith(filterSuffix))
                );

        if (sourceFiles == null) {
            return;
        }

        Arrays.stream(sourceFiles).forEach(sourceFile -> {

            if (sourceFile.isDirectory()) {
                copy(sourceFile.getAbsolutePath(), sourceRoot, targetPath, filterSuffix);
            } else {
                try {

                    in = new FileInputStream(sourceFile);

                    //拼接目标文件路径
                    String sourceFilePath = sourceFile.getAbsolutePath();
                    File newFile = new File(targetPath
                            + sourceFilePath.substring(
                            sourceFilePath.indexOf(sourceRoot) + sourceRoot.length()
                            , sourceFilePath.length()
                    )
                    );
                    if (!newFile.exists()) {
                        boolean mkdirs = newFile.getParentFile().mkdirs();
                        //newFile.createNewFile();
//                        if (mkdirs) {
//                            throw new Exception("创建文件夹失败！");
//                        }
                    }
                    out = new FileOutputStream(newFile);

                    byte[] temp = new byte[4096];
                    int c;
                    while ((c = in.read(temp)) != -1) {
                        out.write(temp, 0, c);
                    }

                    out.close();
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
