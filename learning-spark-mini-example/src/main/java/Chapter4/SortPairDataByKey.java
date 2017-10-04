package Chapter4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class SortPairDataByKey {
    
    public static void main(String[] args) {
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        SparkConf conf =
                new SparkConf().setMaster("local").setAppName("MapAndFlatMap");
        JavaSparkContext sc = new JavaSparkContext(conf);
        
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 100; ++i) {
            list.add(i);
        }
        
        JavaRDD<Integer> intRDD = sc.parallelize(list);
        JavaPairRDD<Integer, String> pairRDD =
                intRDD.mapToPair((Integer x) -> new Tuple2<Integer, String>(x,
                        String.valueOf(x)));
        
        List<Tuple2<Integer, String>> sorted =
                pairRDD.sortByKey(new IntegerComparator(), false).collect();
        for (Tuple2<Integer, String> entry : sorted) {
            System.out.println(entry._1() + " : " + entry._2());
        }
        
        sc.stop();
    }
}

class IntegerComparator implements Comparator<Integer>, Serializable {
    public int compare(Integer a, Integer b) {
        return String.valueOf(a).compareTo(String.valueOf(b));
    }
};
