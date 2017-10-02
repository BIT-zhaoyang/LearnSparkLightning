package Chapter3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class LoadText {
    static final String INPUT = "shakespear.txt";
    static final String OUTPUT = "result";
    
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("load text");
        JavaSparkContext sc = new JavaSparkContext(conf);
        
        JavaRDD<String> lines = sc.textFile(INPUT);
        
        lines.collect().forEach(System.out::println);
        
        sc.stop();
    }
}
