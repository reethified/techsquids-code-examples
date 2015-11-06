package com.ts.mapreduce.matrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public class Driver extends Configured implements Tool {
	private static Logger logger = Logger.getLogger(Driver.class);

	static class MatrixSumMapper extends
			Mapper<LongWritable, Text, LongWritable, Text> {
		String fName = null;
		char keySeprator;

		@Override
		protected void setup(
				Mapper<LongWritable, Text, LongWritable, Text>.Context context)
				throws IOException, InterruptedException {
			fName = ((FileSplit)context.getInputSplit()).getPath().getName();
			keySeprator=(char)context.getConfiguration().getInt("matrix.key.separator",0x001);
		}

		@Override
		protected void map(LongWritable key, Text value,
				Mapper<LongWritable, Text, LongWritable, Text>.Context context)
				throws IOException, InterruptedException {
			LongWritable	keyM = new LongWritable(Long.parseLong(value.toString().split(String.format("%c",keySeprator))[0]));
			Text val = new Text(value.toString().split(String.format("%c",keySeprator))[1]);
			context.write(keyM, val);
		}
	}

	static class MatrixSumReducer extends
			Reducer<LongWritable, Text, LongWritable, Text> {
		String vseparator;
		String fseparator;
		private static Logger logger = Logger.getLogger(MatrixSumMapper.class);

		@Override
		protected void setup(
				Reducer<LongWritable, Text, LongWritable, Text>.Context context)
				throws IOException, InterruptedException {
			logger.debug("reducer- setup: begin");
			vseparator = context.getConfiguration().get("matrix.element.separator");
			logger.debug("reducer- setup: end");
		}

		@Override
		protected void reduce(LongWritable key, Iterable<Text> value,
				Reducer<LongWritable, Text, LongWritable, Text>.Context ctxt)
				throws IOException, InterruptedException {
			List<Integer[]> matrixValues = new ArrayList<Integer[]>();
			List<Integer> val = new ArrayList<Integer>();
			for (Text t : value) {
				prepareKeyValueMap(t, matrixValues);
			}
			for (Integer[] i : matrixValues) {
				for (int j = 0; j < i.length; j++) {
					try{
						
						int sum = val.remove(j);
						val.add(j,sum+i[j]);
					}catch(Exception e){
						System.out.println("throwing exception");
						val.add(i[j]);
					}
				}
			}
			ctxt.write(key, new Text(val.toString()));
		}

		private void prepareKeyValueMap(Text value, List<Integer[]> valuesLst) {
			String[] valuesStr = value.toString().split(vseparator);
			Integer[] valuesInt = new Integer[valuesStr.length];
			for (int i = 0; i < valuesStr.length; i++) {
				try {
					valuesInt[i] = Integer.parseInt(valuesStr[i]);
				} catch (NumberFormatException e) {
					logger.error(e.getMessage());
				}
			}
			valuesLst.add(valuesInt);
		}
	}

	private boolean deleteDirectory(Path path) throws IOException {
		FileSystem fs = FileSystem.get(getConf());
		return fs.delete(path, true);
	}

	public int run(String[] args) throws Exception {
		logger.info("job Matrix Sum Driver Begin");
		Configuration conf = getConf();
		conf.setInt("matrix.key.separator", 0x001);
		conf.set("matrix.element.separator",",");
		Job job = new Job(conf, "Matrix Sum");

		job.setJarByClass(Driver.class);
		
		Path input1 = new Path(args[0]);
		Path input2 = new Path(args[1]);
		Path output = new Path(args[2]);
		deleteDirectory(output);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setMapperClass(MatrixSumMapper.class);
		job.setReducerClass(MatrixSumReducer.class);
		job.setNumReduceTasks(1);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		logger.info("deleting output directory: " + deleteDirectory(output));
		FileInputFormat.setInputPaths(job, input1, input2);
		FileOutputFormat.setOutputPath(job, output);
		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		for (String str : args)
			System.out.println(str);
		Configuration config = new Configuration();
		System.exit(ToolRunner.run(config, new Driver(), args));
	}
}
