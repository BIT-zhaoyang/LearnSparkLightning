package Chapter3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;

public class ReduceAndAggregate {
    
    public static void main(String[] args) {
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        
        SparkConf conf = new SparkConf().setMaster("local")
                .setAppName("ReduceAndAggregate");
        JavaSparkContext sc = new JavaSparkContext(conf);
        
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 50; ++i) {
            list.add(i);
        }
        
        JavaRDD<Integer> input = sc.parallelize(list);

        
        AvgCount result1 = input.map(x -> new AvgCount(x, 1)).reduce(
                (x, y) -> {
                    x.total += y.total;
                    x.num += y.num;
                    return x;
                });
        System.out.println(result1.avg());
        
        
        
        Function2<AvgCount, Integer, AvgCount> addAndCount = (AvgCount x, Integer y) -> {
            x.total += y;
            x.num += 1;
            return x;
        };
        
        Function2<AvgCount, AvgCount, AvgCount> combine = (x, y) -> {
            x.total += y.total;
            x.num += y.num;
            return x;
        };
        AvgCount init = new AvgCount(0, 0);
        AvgCount result2 = input.aggregate(init, addAndCount, combine);
        System.out.println(result2.avg());
        
        sc.stop();
    }
    
}