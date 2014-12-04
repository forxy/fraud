package fraud.controller.v1

import common.api.StatusEntity
import common.rest.AbstractService
import fraud.api.v1.derog.BlackListItem
import fraud.service.derog.IDerogService

import javax.ws.rs.*
import javax.ws.rs.core.*

@Path('/')
@Produces(MediaType.APPLICATION_JSON)
class DerogController extends AbstractService {

    IDerogService blackListManager

    @GET
    @Path('/blacklists/')
    Response getBlackLists(@QueryParam('type') final String type,
                           @QueryParam('value') final String value,
                           @Context final UriInfo uriInfo,
                           @Context final HttpHeaders headers) {
        respondWith(blackListManager.getMoreItemsFrom(type, value), uriInfo, headers).build()
    }

    @GET
    @Path('/blacklist/')
    Response getBlackListItem(@QueryParam('type') final String type,
                              @QueryParam('value') final String value,
                              @Context final UriInfo uriInfo,
                              @Context final HttpHeaders headers) {
        respondWith(blackListManager.get(type, value), uriInfo, headers).build()
    }

    @POST
    @Path('/blacklist/')
    @Consumes(MediaType.APPLICATION_JSON)
    Response addToBlackList(final BlackListItem blackListItem,
                            @Context final UriInfo uriInfo,
                            @Context final HttpHeaders headers) {
        blackListManager.add(blackListItem)
        Response.ok(new StatusEntity("$uriInfo.absolutePath" +
                "?type=$blackListItem.key.type" +
                "&value=$blackListItem.key.value")).build()
    }

    @PUT
    @Path('/blacklist/')
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateBlackList(final BlackListItem blackListItem,
                             @Context final UriInfo uriInfo,
                             @Context final HttpHeaders headers) {
        blackListManager.update(blackListItem)
        Response.ok(new StatusEntity("$uriInfo.absolutePath" +
                "?type=$blackListItem.key.type" +
                "&value=$blackListItem.key.value")).build()
    }

    @DELETE
    @Path('/blacklist/')
    Response deleteBlackList(@QueryParam('type') final String type,
                             @QueryParam('value') final String value,
                             @Context final UriInfo uriInfo,
                             @Context final HttpHeaders headers) {
        blackListManager.delete(new BlackListItem())
        Response.ok(new StatusEntity("BlackList item with type='$type' " +
                "and value='$value' has been successfully deactivated")).build()
    }
}
