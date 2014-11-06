package fraud.storm.topology

import backtype.storm.spout.SpoutOutputCollector
import backtype.storm.task.TopologyContext
import backtype.storm.topology.OutputFieldsDeclarer
import backtype.storm.topology.base.BaseRichSpout
import backtype.storm.tuple.Fields
import backtype.storm.utils.Utils
import com.mongodb.Mongo
import fraud.api.v1.velocity.Transaction
import fraud.api.v1.velocity.VelocityConfig
import org.quartz.*
import org.quartz.impl.JobDetailImpl
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.triggers.CronTriggerImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.core.mapping.SnakeCaseFieldNamingStrategy
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisPubSub

import java.util.concurrent.LinkedBlockingQueue

/**
 * Velocity Data reader
 */
class RedisTranSubscriberSpout extends BaseRichSpout {

    private static final long serialVersionUID = 737015318988609460L

    private static Logger LOG = LoggerFactory.getLogger(RedisTranSubscriberSpout.class)

    private SpoutOutputCollector collector
    private final String redisHost
    private final int redisPort
    private final String mongoHost
    private final int mongoPort
    private LinkedBlockingQueue<String> queue
    private JedisPool pool
    private MongoTemplate mongoTemplate
    private List<VelocityConfig> configs
    private Scheduler scheduler

    RedisTranSubscriberSpout(String redisHost, int redisPort, String mongoHost, int mongoPort) {
        this.redisHost = redisHost
        this.redisPort = redisPort
        this.mongoHost = mongoHost
        this.mongoPort = mongoPort
    }

    class ListenerThread extends Thread {
        private final LinkedBlockingQueue<String> queue
        private final JedisPool pool
        private final MongoTemplate mongoTemplate

        ListenerThread(LinkedBlockingQueue<String> queue, JedisPool pool, MongoTemplate mongoTemplate) {
            this.queue = queue
            this.pool = pool
            this.mongoTemplate = mongoTemplate
        }

        void run() {

            JedisPubSub listener = new JedisPubSub() {

                @Override
                void onMessage(String channel, String transactionID) {
                    queue.offer(transactionID)
                }

                @Override
                void onPMessage(String pattern, String channel, String transactionID) {
                    queue.offer(transactionID)
                }

                @Override
                void onPSubscribe(String channel, int subscribedChannels) {
                }

                @Override
                void onPUnsubscribe(String channel, int subscribedChannels) {
                }

                @Override
                void onSubscribe(String channel, int subscribedChannels) {
                }

                @Override
                void onUnsubscribe(String channel, int subscribedChannels) {
                }
            } as JedisPubSub

            Jedis jedis = pool.getResource()
            try {
                jedis.psubscribe(listener, Transaction.SUBSCRIPTION_PATTERN)
            } finally {
                pool.returnResource(jedis)
            }
        }
    }

    class ConfigsUpdater implements Job {
        @Override
        void execute(final JobExecutionContext context) throws JobExecutionException {
            configs = readConfigs()
        }
    }

    void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector
        queue = new LinkedBlockingQueue<String>(1000)
        pool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort)

        def mongoFactory = new SimpleMongoDbFactory(new Mongo(mongoHost, mongoPort), 'fraud')
        def mappingContext = new MongoMappingContext()
        mappingContext.fieldNamingStrategy = new SnakeCaseFieldNamingStrategy()
        def mappingConverter = new MappingMongoConverter(mongoFactory, mappingContext)
        mappingConverter.typeMapper = new DefaultMongoTypeMapper(null)
        mongoTemplate = new MongoTemplate(mongoFactory, mappingConverter)

        configs = readConfigs()

        scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        JobDetail jobDetail = new JobDetailImpl('configUpdater', 'velocity', ConfigsUpdater.class)
        CronTrigger trigger = new CronTriggerImpl('configUpdateTrigger', 'velocity', '0 0/1 * * * ?')
        scheduler.scheduleJob(jobDetail, trigger);

        ListenerThread listener = new ListenerThread(queue, pool, mongoTemplate)
        listener.start()
    }

    List<VelocityConfig> readConfigs() {
        return mongoTemplate.findAll(VelocityConfig.class)
    }

    void close() {
        pool.destroy()
    }

    void nextTuple() {
        String transactionID = queue.poll()
        if (transactionID == null) {
            Utils.sleep(10)
        } else {
            collector.emit([configs, readTransaction(transactionID)])
        }
    }

    Transaction readTransaction(final String id) {
        Transaction transaction = null
        Jedis jedis = pool.getResource()
        try {
            Set<String> keys = jedis.keys("transaction:$id:data:*".toString())
            Long createDate = jedis.hget("transaction:$id:details" as String, 'createDate') as Long
            transaction = new Transaction(id: Long.valueOf(id), data: [:], createDate: (createDate ? new Date(createDate) : null))
            keys?.each {
                transaction.data << [(it.tokenize(':')[-1]): jedis.lrange(it, 0, -1)]
            }
        } finally {
            pool.returnResource(jedis)
        }
        return transaction
    }

    void ack(Object msgId) {
    }

    void fail(Object msgId) {
    }

    void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields('configs', 'transaction'))
    }
}