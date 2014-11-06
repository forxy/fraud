DROP KEYSPACE IF EXISTS fraud;
CREATE KEYSPACE fraud WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};


USE fraud;
DROP TABLE IF EXISTS transaction;
CREATE TABLE transaction (
transaction_id TIMEUUID,
data_type TEXT,
data LIST<TEXT>,
PRIMARY KEY (transaction_id, data_type)
);

DROP TABLE IF EXISTS history;
CREATE TABLE history (
metric_type TEXT,
metric_value TEXT,
transaction_id TIMEUUID,
PRIMARY KEY ((metric_type, metric_value), transaction_id)
);

DROP TABLE IF EXISTS metric;
CREATE TABLE IF NOT EXISTS metric (
    metric_type TEXT,
    metric_value TEXT,
    secondary_metric TEXT,
    aggregation_type TEXT,
    aggregated_value double,
    PRIMARY KEY ((metric_type, metric_value), secondary_metric, aggregation_type)
);