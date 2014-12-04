package fraud.controller.v1

import common.api.SortDirection
import common.api.StatusEntity
import common.rest.AbstractService
import fraud.service.velocity.IVelocityConfigService
import fraud.api.v1.velocity.AggregationConfig
import fraud.api.v1.velocity.VelocityConfig

import javax.ws.rs.*
import javax.ws.rs.core.*

import static javax.ws.rs.core.Response.ok

@Path('/velocity_config/')
@Produces(MediaType.APPLICATION_JSON)
class VelocityConfigController extends AbstractService {

    IVelocityConfigService velocityConfigManager

    @GET
    Response getVelocityConfigs(@QueryParam('page') final Integer page,
                                @QueryParam('size') final Integer size,
                                @QueryParam('sort_dir') final SortDirection sortDirection,
                                @QueryParam('sorted_by') final String sortedBy,
                                @QueryParam('primary_metrics') final Set<String> primaryMetrics,
                                @QueryParam('secondary_metrics') final Set<String> secondaryMetrics,
                                @QueryParam('updated_by') final String updatedByFilter,
                                @QueryParam('created_by') final String createdByFilter,
                                @Context final UriInfo uriInfo,
                                @Context final HttpHeaders headers) {
        respondWith(page == null && size == null ?
                velocityConfigManager.allVelocityConfigs :
                velocityConfigManager.getVelocityConfigs(page, size, sortDirection, sortedBy,
                        new VelocityConfig(
                                primaryMetrics: primaryMetrics,
                                updatedBy: updatedByFilter,
                                createdBy: createdByFilter,
                                aggregationConfigs: secondaryMetrics?.collect {
                                    new AggregationConfig(
                                            secondaryMetric: it,
                                            aggregation: null
                                    )
                                }?.toSet()
                        )),
                uriInfo, headers).build()
    }

    @GET
    @Path('/{id}/')
    Response getVelocityConfig(@PathParam('id') final String id,
                               @Context final UriInfo uriInfo,
                               @Context final HttpHeaders headers) {
        respondWith(velocityConfigManager.getVelocityConfig(id), uriInfo, headers).build()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response addVelocityConfig(final VelocityConfig velocityConfig,
                               @Context final UriInfo uriInfo,
                               @Context final HttpHeaders headers) {
        velocityConfigManager.createVelocityConfig(velocityConfig)
        ok(new StatusEntity("$uriInfo.absolutePath$velocityConfig.id")).build()
    }

    @PUT
    @Path('/{id}/')
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateVelocityConfig(@PathParam('id') final String id,
                                  final VelocityConfig velocityConfig,
                                  @Context final UriInfo uriInfo,
                                  @Context final HttpHeaders headers) {
        velocityConfigManager.updateVelocityConfig(id, velocityConfig)
        ok(new StatusEntity("$uriInfo.absolutePath")).build()
    }

    @DELETE
    @Path('/{id}/')
    Response deleteVelocityConfig(@PathParam('id') final String id,
                                  @Context final UriInfo uriInfo,
                                  @Context final HttpHeaders headers) {
        velocityConfigManager.deleteVelocityConfig(id)
        ok(new StatusEntity("VelocityConfig with ID='$id' has been successfully removed")).build()
    }
}
