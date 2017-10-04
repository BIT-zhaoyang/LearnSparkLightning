package Chapter4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

import scala.Tuple2;

public class CombineByKey {
    
    public static void main(String[] args) {
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        SparkConf conf = new SparkConf().setMaster("local").setAppName("MapAndFlatMap");
        JavaSparkContext sc = new JavaSparkContext(conf);
        
        List<Tuple2<String, Integer> > inMemo = new ArrayList<Tuple2<String, Integer> >();
        inMemo.add(new Tuple2("panda", 0));
        inMemo.add(new Tuple2("pink", 3));
        inMemo.add(new Tuple2("pirate", 3));
        inMemo.add(new Tuple2("panda", 1));
        inMemo.add(new Tuple2("pink", 4));
        JavaPairRDD<String, Integer> nums = sc.parallelizePairs(inMemo);
        
        // rdd.combineByKey takes three parameters
        // 1. accumulator initiator
        Function<Integer, AvgCount> createAcc = new Function<Integer, AvgCount>() {
            public AvgCount call(Integer x) {
                return new AvgCount(x, 1);
            }
        };
        
        // 2. operation rule between accumulator and rdd element
        Function2<AvgCount, Integer, AvgCount> addAndCount =
            new Function2<AvgCount, Integer, AvgCount>() {
                public AvgCount call(AvgCount a, Integer x) {
                    a.total += x;
                    a.num += 1;
                    return a;
                }
            };

        // 3. operation rule between accumulator
        Function2<AvgCount, AvgCount, AvgCount> combine = 
            new Function2<AvgCount, AvgCount, AvgCount>() {
                public AvgCount call(AvgCount a, AvgCount b) {
                    a.num += b.num;
                    a.total += b.total;
                    return a;
                }
        };

        JavaPairRDD<String, AvgCount> avgCounts = 
                nums.combineByKey(createAcc, addAndCount, combine);
        Map<String, AvgCount> countMap = avgCounts.collectAsMap();
        for (Entry<String, AvgCount> entry : countMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().avg());
        }
        
        // Another way to compute the average
        // group the data together then compute average
        JavaPairRDD<String, Iterable<Integer> > groupRDD = nums.groupByKey();
        Function<Iterable<Integer>, Double> getAvg = 
            new Function<Iterable<Integer>, Double>() {
                public Double call(Iterable<Integer> iterable) {
                    double total = 0;
                    int num = 0;
                    Iterator<Integer> iter = iterable.iterator();
                    while(iter.hasNext()) {
                        total += iter.next();
                        num += 1;
                    }
                    return total / num;
                }
        };
        Map<String, Double> result = groupRDD.mapValues(getAvg).collectAsMap();
        for (Entry<String, Double> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        
        sc.stop();
    }
    
}
