package fraud.storm

import backtype.storm.Config
import backtype.storm.LocalCluster
import backtype.storm.StormSubmitter
import backtype.storm.topology.TopologyBuilder
import backtype.storm.utils.Utils
import fraud.storm.topology.MetricsUpdateBolt
import fraud.storm.topology.RedisTranSubscriberSpout

/**
 * Storm Velocity Topology runner
 */
class VelocityTopology {

    static void main(String[] args) throws Exception {
        if (args != null && args.length >= 4) {
            TopologyBuilder builder = new TopologyBuilder();
            String redisHost = args[0];
            int redisPort = Integer.parseInt(args[1]);
            String mongoHost = args[2];
            int mongoPort = Integer.parseInt(args[3]);

            builder.setSpout('transaction', new RedisTranSubscriberSpout(redisHost, redisPort, mongoHost, mongoPort))
            builder.setBolt('metrics', new MetricsUpdateBolt(redisHost, redisPort), 3).shuffleGrouping('transaction');

            Config conf = new Config();

            if (args.length >= 5) {
                conf.setDebug(false);
                conf.setNumWorkers(3);
                StormSubmitter.submitTopology(args[4], conf, builder.createTopology());
            } else {
                conf.setDebug(true);
                LocalCluster cluster = new LocalCluster();
                cluster.submitTopology('test', conf, builder.createTopology());
                Utils.sleep(10000);
                cluster.killTopology('test');
                cluster.shutdown();
            }
        }
    }
}