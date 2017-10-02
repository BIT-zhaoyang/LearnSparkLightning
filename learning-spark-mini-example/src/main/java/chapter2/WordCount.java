package chapter2;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class WordCount {
    
    public static void main(String[] args) {
        String inputFile = "shakespear.txt";
        String outputFile = "result";
        
 SparkConf conf =
                new SparkConf().setMaster("local").setAppName("wordCount");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> input = sc.textFile(inputFile);
        
        // JavaRDD<String> words = input.flatMap(new FlatMapFunction<String,
        // String>() {
        // public Iterable<String> call(String x) {
        // return Arrays.asList(x.split(" "));
        // }
        // });
        
        JavaRDD<String> words = input
                .flatMap(x -> (Iterable<String>) Arrays.asList(x.split(" ")));
        
//        JavaPairRDD<String, Integer> counts =
//                words.mapToPair(new PairFunction<String, String, Integer>() {
//                    public Tuple2<String, Integer> call(String x) {
//                        return new Tuple2(x, 1);
//                    }
//                }).reduceByKey(new Function2<Integer, Integer, Integer>() {
//                    public Integer call(Integer x, Integer y) {
//                        return x + y;
//                    }
//                });
        
        JavaPairRDD<String, Integer> counts =
                words.mapToPair((String x) -> new Tuple2(x, 1))
                        .reduceByKey((x, y) -> ((Integer)x + (Integer)y));
        counts.saveAsTextFile(outputFile);
        
        sc.stop();
    }
    
}
