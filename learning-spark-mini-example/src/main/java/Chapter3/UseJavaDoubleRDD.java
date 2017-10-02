package Chapter3;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.DoubleFunction;

/*
 * User specialized RDD type can give us access to certain functionalities,
 * which can benefits us.
 * 
 * This example demonstrates how to compute the average from a collection of
 * integer. If we use the more general type of JavaRDD<AvgCount>, we have to convert
 * the elements explicitly. This requires a lot of work. However, if we use 
 * JavaDoubleRDD, we only need to convert integer to double.
 */
public class UseJavaDoubleRDD {
    
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
        
        JavaDoubleRDD result2 = input.mapToDouble(new DoubleFunction<Integer>() {
            public double call(Integer x) {
                return (double)x;
            }
        });
        System.out.println(result2.mean());
    }
    
}
