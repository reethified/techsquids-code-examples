package com.ts.mapreduce.index;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class IndexDriver extends Configured implements Tool{
static class IndexMapper extends Mapper<Object , Text, LongWritable, Text>{
	@Override
	protected void map(Object key, Text value, Mapper<Object, Text, LongWritable, Text>.Context context)
			throws IOException, InterruptedException {
		context.write(new LongWritable(Long.parseLong(key.toString())), value);
	}

}
static class IndexReucer extends Reducer<LongWritable, Text, NullWritable, Text>{
	static enum Counter{COUNT}
	static final char SEPARATOR=0x001;
		@Override
		protected void setup(
				Reducer<LongWritable, Text, NullWritable, Text>.Context context)
				throws IOException, InterruptedException {
			context.getCounter(Counter.COUNT).setValue(1);
			super.setup(context);
		}
	@Override
	protected void reduce(LongWritable key, Iterable<Text> value,Reducer<LongWritable, Text, NullWritable, Text>.Context context)
				throws IOException, InterruptedException {
			Long counter = context.getCounter(Counter.COUNT).getValue();
			context.write(NullWritable.get(), new Text(String.format("%d%c%s", counter,SEPARATOR,value.iterator().next())));
			context.getCounter(Counter.COUNT).increment(1);;
	}
	}
	private boolean deleteDirectory(Path path) throws IOException {
		FileSystem fs = FileSystem.get(getConf());
		return fs.delete(path, true);
	}
	public int run(String[] args) throws Exception {
		Configuration conf =getConf();
		Job job = new Job(conf, "Indexing Job");
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setMapperClass(IndexMapper.class);
		job.setReducerClass(IndexReucer.class);
		job.setNumReduceTasks(1);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		Path output = new Path(args[1]);
		deleteDirectory(output);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, output);
		job.setJarByClass(IndexDriver.class);
		return job.waitForCompletion(true)?0:1;
	}
	public static void main(String[] args) throws Exception {
		Configuration config = new Configuration();
		System.exit(ToolRunner.run(config,new IndexDriver(), args));
	}
}
