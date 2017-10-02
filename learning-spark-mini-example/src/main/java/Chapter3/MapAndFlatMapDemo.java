package Chapter3;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;


public class MapAndFlatMapDemo {
    
    public static void main(String[] args) {
        String[] arr = {"coffee panda", "happy panda", "happiest panda party"};
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        
        SparkConf conf = new SparkConf().setMaster("local").setAppName("MapAndFlatMap");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.parallelize(Arrays.asList(arr));
        
        lines.map(x -> Arrays.asList(x.split(" "))).collect().forEach(System.out::println);
        lines.flatMap(x -> Arrays.asList(x.split(" "))).collect().forEach(System.out::println);
    }
    
}
