package com.ts.mapreduce.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 
 * A job with a just a map-reduce phase to fetch all column qualifiers.
 */

public class ColumnsCounter extends Configured implements Tool {

	/** Name of this 'program'. */
	private final static String JOB_NAME_CONF_KEY = "mapreduce.job.name";

	/**
	 * 
	 * Mapper that emits the column family as key and value as qualifier.
	 */

	static class ColumnsMapper extends TableMapper<Text, NullWritable> {

		@Override
		public void map(ImmutableBytesWritable row, Result values, Context context) throws IOException,
				InterruptedException {

			// emit every combination of column family and qualifiers
			for (Entry<byte[], NavigableMap<byte[], byte[]>> columnFamilyMap : values.getNoVersionMap().entrySet()) {
				for (Entry<byte[], byte[]> entry : columnFamilyMap.getValue().entrySet()) {

					String cQualifier = Bytes.toString(entry.getKey());

					context.write(new Text(cQualifier), NullWritable.get());

				}

			}

		}
	}
	/**
	 * 
	 * Reducer that returns the column family with all qualifier.
	 */
	static class ColumnsReducer extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException,
				InterruptedException {
		Iterator<Text> columns=	values.iterator();
		List<String> columnsList=new ArrayList<String>();
		while(columns.hasNext())
			columnsList.add(columns.next().toString());	
			context.write(key, new Text(columnsList.toString()));

		}

	}

	/**
	 * 
	 * Sets up the actual job.
	 * @param conf
	 *            The current configuration.
	 * 
	 * @param args
	 *            The command line parameters.
	 * 
	 * @return The newly created job.
	 * 
	 * @throws IOException
	 *             When setting up the job fails.
	 */

	public static Job createSubmittableJob(Configuration conf, String[] args) throws IOException {

		String tableName =args[1];

		conf.set("habse.table.name", args[1]);

		System.out.println("table: " + tableName);
		deletePath(conf, args[2]);
		Job job = new Job(conf, conf.get(JOB_NAME_CONF_KEY, JOB_NAME_CONF_KEY + "_" + tableName));

		// Need to add Hbase jars to job class path

		HBaseConfiguration.addHbaseResources(job.getConfiguration());

		TableMapReduceUtil.addDependencyJars(job);

		TableMapReduceUtil.addDependencyJars(job.getConfiguration(), org.apache.hadoop.hbase.client.Put.class);

		job.setJarByClass(ColumnsCounter.class);

		Scan scan = new Scan();

		scan.setCacheBlocks(false);

		// scan.addFamily(Bytes.toBytes(family));

		TableMapReduceUtil.initTableMapperJob(
				tableName, scan, ColumnsMapper.class,
				Text.class,
				Text.class, job);
		job.setReducerClass(ColumnsReducer.class);

		//job.setNumReduceTasks(1);

		FileOutputFormat.setOutputPath(job, new Path(args[2]));

		return job;

	}

	/**
	 * Delete output directory
	 * */
	static boolean deletePath(Configuration conf, String path){
		try {
			FileSystem fs= FileSystem.get(conf);
			return fs.delete(new Path(path), true);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
	/*
	 * 
	 * @param errorMessage Can attach a message when error occurs.
	 */

	private static void printUsage(String errorMessage) {

		System.err.println("ERROR: " + errorMessage);

		printUsage();

	}

	/*
	 * 
	 * Prints usage without error message
	 */

	private static void printUsage() {
		
		System.err.println("Usage: RowCounter <tablename> <ouputLocation>");

		System.err.println("For performance consider the following options:\n" + "-Dhbase.client.scanner.caching=100\n"

		+ "-Dmapreduce.map.speculative=false");

	}

	public int run(String[] args) throws Exception {

		String[] otherArgs = new GenericOptionsParser(getConf(), args).getRemainingArgs();

		if (otherArgs.length < 2) {

			printUsage("Wrong number of parameters: " + args.length);

			return -1;

		}

		Job job = createSubmittableJob(getConf(), otherArgs);

		if (job == null) {

			return -1;

		}

		return (job.waitForCompletion(true) ? 0 : 1);

	}

	/**
	 * 
	 * Main entry point.
	 * 
	 *
	 * 
	 * @param args
	 *            The command line parameters.
	 * 
	 * @throws Exception
	 *             When running the job fails.
	 */

	public static void main(String[] args) throws Exception {
		System.out.println("starting job");
		Configuration conf = HBaseConfiguration.create();

		int errCode = ToolRunner.run(conf, new ColumnsCounter() , args);

		System.exit(errCode);

	}

}
