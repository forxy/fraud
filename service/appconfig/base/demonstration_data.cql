//======================================================================================================================
// FIRST CALL: {
//   "Email" :      "aaa@mail.com",
//   "CreditCard" : "1111222233334444",
//   "Amount" :     "1100.00"
// }
//======================================================================================================================
// synchronous: concurrent call and return the following data in the response
select * from velocity where metric_type = 'Email' and metric_value = 'aaa@mail.com';
select * from velocity where metric_type = 'CreditCard' and metric_value = '1111222233334444';
//======================================================================================================================
// RESPONSE: return empty response since this is our first call and there are no metrics saved yet
//======================================================================================================================

// ASYNCHRONOUS:

// insert new data
insert into velocity_data (metric_type, metric_value, related_metric_type, create_date, related_metric_value)
values ('Email', 'aaa@mail.com', 'CreditCard', dateof(now()), '1111222233334444')
USING TTL 7776000; --TimeToLive = 90 days if needed

insert into velocity_data (metric_type, metric_value, related_metric_type, create_date, related_metric_value)
values ('Email', 'aaa@mail.com', 'Amount', dateof(now()), '1100.00')
USING TTL 7776000; --TimeToLive = 90 days if needed

insert into velocity_data (metric_type, metric_value, related_metric_type, create_date, related_metric_value)
values ('CreditCard', '1111222233334444', 'Email', dateof(now()), 'aaa@mail.com')
USING TTL 7776000; --TimeToLive = 90 days if needed

// AGGREGATION: concurrently

// get historical data
select * from velocity_data
where metric_type = 'Email'
and metric_value = 'aaa@mail.com'
and related_metric_type = 'CreditCard'
and create_date > dateOf(minTimeuuid('2014-08-10 19:58:43+0300'));
// let's say that '2014-09-10 19:58:43+0300' is 30 days ago or any other configured period

// apply aggregation functions and insert the following velocity metrics
insert into velocity (metric_type, metric_value, related_metric_type, aggregation_type, aggregated_value)
values ('Email', 'aaa@mail.com', 'CreditCard', 'Count', 1);

// get historical data
select * from velocity_data
where metric_type = 'Email'
and metric_value = 'aaa@mail.com'
and related_metric_type = 'Amount'
and create_date > dateOf(minTimeuuid('2014-08-10 19:58:43+0300'));
// let's say that '2014-09-10 19:58:43+0300' is 30 days ago or any other configured period

// apply aggregation functions and insert the following velocity metrics
insert into velocity (metric_type, metric_value, related_metric_type, aggregation_type, aggregated_value)
values ('Email', 'aaa@mail.com', 'Amount', 'Sum', 1100.00);

// get historical data
select * from velocity_data
where metric_type = 'CreditCard'
and metric_value = '1111222233334444'
and related_metric_type = 'Email'
and create_date > dateOf(minTimeuuid('2014-08-10 19:58:43+0300'));
// let's say that '2014-09-10 19:58:43+0300' is 30 days ago or any other configured period

// apply aggregation functions and insert the following velocity metrics
insert into velocity (metric_type, metric_value, related_metric_type, aggregation_type, aggregated_value)
values ('CreditCard', '1111222233334444', 'Email', 'UniqueCount', 1);


//======================================================================================================================
// SECOND CALL: {
//   "Email" :      "aaa@mail.com",
//   "CreditCard" : "2222333344445555",
//   "Amount" :     "3900.00"
// }
//======================================================================================================================
// synchronous: concurrent call and return the following data in the response
select * from velocity where metric_type = 'Email' and metric_value = 'aaa@mail.com';
select * from velocity where metric_type = 'CreditCard' and metric_value = '2222333344445555';
//======================================================================================================================
// RESPONSE: {
//   "Email" : {
//     "CreditCard": {"Count" : 1},
//     "Amount": {"Sum": 1100.0}
//   },
//   "CreditCard" : {
//     "Email" : {"UniqueCount", 1}
//   }
// }
//======================================================================================================================

// ASYNCHRONOUS:

// insert new data
insert into velocity_data (metric_type, metric_value, related_metric_type, create_date, related_metric_value)
values ('Email', 'aaa@mail.com', 'CreditCard', dateof(now()), '2222333344445555')
USING TTL 7776000; --TimeToLive = 90 days if needed

insert into velocity_data (metric_type, metric_value, related_metric_type, create_date, related_metric_value)
values ('Email', 'aaa@mail.com', 'Amount', dateof(now()), '3900.00')
USING TTL 7776000; --TimeToLive = 90 days if needed

// get historical data
select * from velocity_data
where metric_type = 'Email'
and metric_value = 'aaa@mail.com'
and related_metric_type = 'CreditCard'
and create_date > dateOf(minTimeuuid('2014-08-10 19:58:43+0300'));
// let's say that '2014-09-10 19:58:43+0300' is 30 days ago or any other configured period

// apply aggregation functions and insert the following velocity metrics
insert into velocity (metric_type, metric_value, related_metric_type, aggregation_type, aggregated_value)
values ('Email', 'aaa@mail.com', 'CreditCard', 'Count', 2);

// get historical data
select * from velocity_data
where metric_type = 'Email'
and metric_value = 'aaa@mail.com'
and related_metric_type = 'Amount'
and create_date > dateOf(minTimeuuid('2014-08-10 19:58:43+0300'));
// let's say that '2014-09-10 19:58:43+0300' is 30 days ago or any other configured period

// apply aggregation functions and insert the following velocity metrics
insert into velocity (metric_type, metric_value, related_metric_type, aggregation_type, aggregated_value)
values ('Email', 'aaa@mail.com', 'Amount', 'Sum', 5000.00);

// get historical data
select * from velocity_data
where metric_type = 'CreditCard'
and metric_value = '1111222233334444'
and related_metric_type = 'Email'
and create_date > dateOf(minTimeuuid('2014-08-10 19:58:43+0300'));
// let's say that '2014-09-10 19:58:43+0300' is 30 days ago or any other configured period

// apply aggregation functions and insert the following velocity metrics
insert into velocity (metric_type, metric_value, related_metric_type, aggregation_type, aggregated_value)
values ('CreditCard', '2222333344445555', 'Email', 'UniqueCount', 1);
