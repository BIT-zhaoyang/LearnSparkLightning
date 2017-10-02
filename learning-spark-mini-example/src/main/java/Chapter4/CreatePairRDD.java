package Chapter4;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.storage.StorageLevel;

import scala.Tuple2;

public class CreatePairRDD {
    
    public static void main(String[] args) {
        final String INPUT = "shakespear.txt";
        final String OUTPUT = "result";
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        SparkConf conf = new SparkConf().setMaster("local").setAppName("MapAndFlatMap");
        JavaSparkContext sc = new JavaSparkContext(conf);
        
        JavaRDD<String> lines = sc.textFile(INPUT);
        PairFunction<String, String, String> keyData = 
            new PairFunction<String, String, String> () {
                public Tuple2<String, String> call(String x) {
                    return new Tuple2(x.split(" ")[0], x);
                }
            };
        JavaPairRDD<String, String> pairs = lines.mapToPair(keyData);
        lines.persist(StorageLevel.MEMORY_ONLY());
        pairs.take(100).forEach(System.out::println);
    }
    
}
