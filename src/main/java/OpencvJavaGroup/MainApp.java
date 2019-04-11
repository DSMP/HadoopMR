package OpencvJavaGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import OpencvJavaGroup.Model.FingerSample;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.codehaus.jackson.map.ObjectMapper;

import static org.apache.hadoop.mapreduce.lib.input.FileInputFormat.addInputPath;
import static org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.*;

public class MainApp {
    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, DoubleWritable>{
        private Text word = new Text();
        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            ObjectMapper mapper = new ObjectMapper();
            while (itr.hasMoreTokens()) {
                FingerSample fingerSample = mapper.readValue(itr.nextToken(), FingerSample.class);
                for (String fkey : fingerSample.getFeatures2D().keySet()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(fingerSample.getSide());
                    sb.append("-");
                    sb.append(fingerSample.getSeries());
                    sb.append("-");
                    sb.append(fkey);
                    word.set(sb.toString());
                    DoubleWritable one = new DoubleWritable(fingerSample.getFeatures2D().get(fkey));
                    context.write(word, one);
                }
            }
        }
    }
    public static class IntSumReducer
            extends Reducer<Text,DoubleWritable,Text,DoubleWritable> {
        private DoubleWritable result = new DoubleWritable();
        public void reduce(Text key, Iterable<DoubleWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            ArrayList<Double> nValues = new ArrayList<Double>();
            int sum = 0;
            double avg = 0;
            for (DoubleWritable val : values) {
                sum += val.get();
                nValues.add(val.get());
            }
            avg = sum/nValues.size();
            double sig = 0;
            for (Double val : nValues) {
                sig += Math.pow(val-avg,2.0);
            }
            result.set(Math.sqrt(sig/nValues.size()));
            context.write(key, result);
        }
    }
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(MainApp.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        addInputPath(job, new Path(args[0]));
        setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

