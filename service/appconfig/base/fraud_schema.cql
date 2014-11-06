CREATE KEYSPACE IF NOT EXISTS fraud
 WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

USE fraud;

DROP TABLE IF EXISTS blacklist;
CREATE TABLE IF NOT EXISTS blacklist (
    type TEXT,
    value TEXT,
    is_active BOOLEAN,
    create_date TIMEUUID,
    created_by TEXT,
    update_date TIMEUUID,
    updated_by TEXT,
    PRIMARY KEY ((type, value))
);

DROP TABLE IF EXISTS whitelist;
CREATE TABLE IF NOT EXISTS whitelist (
    type TEXT,
    value TEXT,
    is_active BOOLEAN,
    create_date TIMEUUID,
    created_by TEXT,
    update_date TIMEUUID,
    updated_by TEXT,
    PRIMARY KEY ((type, value))
);

DROP TABLE IF EXISTS velocity;
CREATE TABLE IF NOT EXISTS velocity (
    metric_type TEXT,
    metric_value TEXT,
    related_metric_type TEXT,
    aggregation_type TEXT,
    aggregated_value double,
    PRIMARY KEY ((metric_type, metric_value), related_metric_type, aggregation_type)
);

DROP TABLE IF EXISTS velocity_data;
CREATE TABLE IF NOT EXISTS velocity_data (
    metric_type TEXT,
    metric_value TEXT,
    related_metric_type TEXT,
    create_date TIMEUUID,
    related_metric_value TEXT,
    PRIMARY KEY ((metric_type, metric_value), related_metric_type, create_date)
);
