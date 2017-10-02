package Chapter3;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;


public class RDDTransformation {
    
    static private void cleanDir(String dirName) {
        File dir = new File(dirName);
        String[] entries = dir.list();
        for(String s : entries) {
            File currentFile = new File(dir.getPath(), s);
            currentFile.delete();
        }
        dir.delete();
    }
    
    public static void main(String[] args) {
        final String INPUT = "shakespear.txt";
        final String OUTPUT = "result";
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        cleanDir(OUTPUT);
        
        // set up spark
        SparkConf conf =
                new SparkConf().setMaster("local").setAppName("TransformRDD");
        JavaSparkContext sc = new JavaSparkContext(conf);
        
        // load text file
        JavaRDD<String> lines = sc.textFile(INPUT);
        
        // Transform RDD
        JavaRDD<String> errorsLines = lines.filter(x -> x.contains("error"));
        errorsLines.persist(StorageLevel.MEMORY_ONLY());
        System.out.println("There are " + errorsLines.count()
                + " lines containing 'error'");
        errorsLines.take(10).forEach(System.out::println);
        System.out.println();
        
        JavaRDD<String> killLines = lines.filter(x -> x.contains("kill"));
        killLines.persist(StorageLevel.MEMORY_ONLY());
        System.out.println("There are " + killLines.count()
                + " lines containing 'kill'");
        killLines.take(10).forEach(System.out::println);
        System.out.println();
        
        JavaRDD<String> badLines = errorsLines.union(killLines);
        System.out.println("There are " + badLines.count() + " lines in total");
        badLines.take(100).forEach(System.out::println);
     
        sc.stop();
    }
    
}
