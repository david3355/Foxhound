package com.jagerdev.foxhoundpricetracker.webpage;

import com.google.gson.Gson;

import org.restlet.data.Status;

import controllers.exceptions.ImproperPathSelectorException;
import controllers.exceptions.PathForProductNotFoundException;
import controllers.exceptions.SourcePageNotAvailableException;
import controllers.validators.OnInvalidInput;
import database.DatabaseException;
import fi.iki.elonen.NanoHTTPD;
import model.Product;
import tracker.PriceTrackerManager;
import utility.logger.CommonLogger;
import utility.logger.PriceTrackerLogger;

import static com.jagerdev.foxhoundpricetracker.webpage.ResponseBuilder.buildResponse;

public class MiniRESTServer extends NanoHTTPD
{
       private PriceTrackerManager manager;
       private Gson jsonTransformer;
       private CommonLogger logger;
       private NanoHttpdRouter router;

       public MiniRESTServer(int port, OnInvalidInput invalidInputHandler) throws DatabaseException
       {
              this("0.0.0.0", port, invalidInputHandler);
       }

       public MiniRESTServer(String hostname, int port, OnInvalidInput invalidInputHandler) throws DatabaseException
       {
              super(hostname, port);
              logger = PriceTrackerLogger.getNewLogger(this.getClass().getName());
              jsonTransformer = new Gson();
              this.manager = new PriceTrackerManager(invalidInputHandler);
              this.router = new NanoHttpdRouter();
              router.addRoute("/pricetracker/products", products);
       }

       private RouteMatchHandler products = new HTTPMethodHandler()
       {
              @Override
              public Response post(IHTTPSession session)
              {
                     // get the POST body
                     String postBody = session.getQueryParameterString();
                     // or you can access the POST request's parameters
                     String targetPrice = session.getParms().get("target_price");

                     String sourcePageUri = session.getParms().get("source_page_uri");
                     String targetName = session.getParms().get("target_name");    // Product
                     String inspectFrequency = session.getParms().get("inspect_frequency");      // 1
                     String if_unit = session.getParms().get("if_unit");     // day

                     try
                     {
                            Product trackedProduct = manager.trackNewItem(targetPrice, sourcePageUri, targetName, inspectFrequency, if_unit);
                            logger.info("New product added on REST API: %s", trackedProduct.getName());
                            ProductJsonModel jsonProduct = new ProductJsonModel(
                                    trackedProduct.getId(),
                                    trackedProduct.getName(),
                                    trackedProduct.getWebPath(),
                                    trackedProduct.getActualPrice(),
                                    trackedProduct.getPriceHtmlPathSelector(),
                                    trackedProduct.isAvailableNow(),
                                    trackedProduct.getInspectFrequency(),
                                    trackedProduct.getDateOfRecord(),
                                    trackedProduct.getDateOfLastCheck(),
                                    trackedProduct.getActiveAlarms());
                            String respData = jsonTransformer.toJson(jsonProduct);
                            Response resp = newFixedLengthResponse(respData);
                            resp.addHeader("Access-Control-Allow-Origin", "*");
                            return resp;
                     } catch (SourcePageNotAvailableException e)
                     {
                            logger.warning("Cannot add new product on REST API: %s. Details: %s", targetName, e.getMessage());
                            return buildResponse(e.getMessage(), Status.CLIENT_ERROR_BAD_REQUEST);
                     } catch (PathForProductNotFoundException | ImproperPathSelectorException | DatabaseException e)
                     {
                            logger.warning("Cannot add new product on REST API: %s. Details: %s", targetName, e.getMessage());
                            return buildResponse(e.getMessage(), Status.SERVER_ERROR_INTERNAL);
                     }
              }
       };

       public Response serve(IHTTPSession session)
       {
              logger.info("Request on REST");
              return router.route(session.getUri(), session);
       }
}
