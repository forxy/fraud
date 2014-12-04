package fraud.controller.v1

import common.rest.AbstractService
import fraud.service.velocity.IVelocityService
import org.joda.time.DateTime

import javax.ws.rs.*
import javax.ws.rs.core.*

@Path('/velocity/')
@Produces(MediaType.APPLICATION_JSON)
class VelocityController extends AbstractService {

    IVelocityService velocityManager

    @POST
    @Path('/cassandra/check/')
    @Consumes(MediaType.APPLICATION_JSON)
    Response cassandraCheck(final Map<String, String[]> metrics,
                            @Context final UriInfo uriInfo,
                            @Context final HttpHeaders headers) {
        respondWith(velocityManager.cassandraGetMetrics(metrics, true), uriInfo, headers).build()
    }

    @POST
    @Path('/redis/check/')
    @Consumes(MediaType.APPLICATION_JSON)
    Response redisCheck(final Map<String, String[]> metrics,
                        @Context final UriInfo uriInfo,
                        @Context final HttpHeaders headers) {
        respondWith(velocityManager.redisGetMetrics(metrics, true), uriInfo, headers).build()
    }

    @GET
    @Path('/cassandra/metrics/')
    Response getCassandraMetrics(@Context final UriInfo uriInfo,
                                 @Context final HttpHeaders headers) {
        respondWith(velocityManager.cassandraGetMetrics(toMetrics(uriInfo), false), uriInfo, headers).build()
    }

    @GET
    @Path('/redis/metrics/')
    Response getRedisMetrics(@Context final UriInfo uriInfo,
                             @Context final HttpHeaders headers) {
        respondWith(velocityManager.redisGetMetrics(toMetrics(uriInfo), false), uriInfo, headers).build()
    }

    @GET
    @Path('/cassandra/history/')
    Response getCassandraHistoricalData(@QueryParam('start_date') String startDate,
                                        @QueryParam('end_date') String endDate,
                                        @QueryParam('start_id') String startID,
                                        @QueryParam('end_id') String endID,
                                        @Context final UriInfo uriInfo,
                                        @Context final HttpHeaders headers) {
        respondWith(velocityManager.cassandraGetHistory(
                toMetricsFilter(uriInfo),
                startDate ? DateTime.parse(startDate) : null,
                endDate ? DateTime.parse(endDate) : null,
                startID ? UUID.fromString(startID) : null,
                endID ? UUID.fromString(endID) : null
        ), uriInfo, headers).build()
    }

    @GET
    @Path('/redis/history/')
    Response getRedisHistoricalData(@QueryParam('start_date') String startDate,
                                    @QueryParam('end_date') String endDate,
                                    @QueryParam('start_id') String startID,
                                    @QueryParam('end_id') String endID,
                                    @Context final UriInfo uriInfo,
                                    @Context final HttpHeaders headers) {
        respondWith(velocityManager.redisGetHistory(
                toMetricsFilter(uriInfo),
                startDate ? DateTime.parse(startDate) : null,
                endDate ? DateTime.parse(endDate) : null,
                startID,
                endID
        ), uriInfo, headers).build()
    }

    private static Map<String, String[]> toMetrics(UriInfo uriInfo) {
        def metrics = [:]
        uriInfo.getQueryParameters()?.each { metric, values ->
            if (metric != 'start_date' && metric != 'end_date' && metric != 'start_id' && metric != 'end_id') {
                metrics[(metric)] = values.toArray()
            }
        }
        return metrics
    }

    private static Map<String, String> toMetricsFilter(UriInfo uriInfo) {
        def filter = [:]
        uriInfo.getQueryParameters()?.each { metric, values ->
            if (values.size() > 0
                    && metric != 'start_date' && metric != 'end_date' && metric != 'start_id' && metric != 'end_id') {
                filter[(metric)] = values.first()
            }
        }
        return filter
    }
}
