package org.most.client;

import net.sf.json.JSONObject;
import org.apache.http.HttpException;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Created by kbarbounakis on 12/20/15.
 */
public class ClientDataQueryableTest {

    private static final String HOST_URI = "http://127.0.0.1:3000/";
    private static final String TEST_USERNAME = "alexis.rees@example.com";
    private static final String TEST_PASSWORD = "user";

    private ClientDataQueryable testGetQueryable(String model) throws URISyntaxException, IOException, HttpException {
        ClientDataService svc = new ClientDataService(HOST_URI);
        return svc.authenticate(TEST_USERNAME,TEST_PASSWORD).model(model);
    }

    @Test
    public void testSimpleFilter() throws HttpException, IOException, URISyntaxException {

        ClientDataQueryable q = testGetQueryable("Person");
        DataObject result = q.where("id").equal(353).first();
        System.out.println(String.format("%s %s", result.getString("givenName"), result.getString("familyName")));
    }

    @Test
    public void testSelect() throws HttpException, IOException, URISyntaxException {

        ClientDataQueryable q = testGetQueryable("Person");
        DataObject result = q.select("id", "givenName", "familyName")
                    .where("givenName").equal("Alexis").first();
        System.out.println(String.format("(%d) %s %s", result.getInteger("id"), result.getString("givenName"), result.getString("familyName")));
    }

    @Test
    public void testAndExpression() throws HttpException, IOException, URISyntaxException {

        ClientDataQueryable q = testGetQueryable("Product");
        DataObjectArray result = q.select("id","name","price")
                .where("price").lowerOrEqual(900)
                .and("category").equal("Laptops")
                .orderBy("price")
                .take(5);
        result.forEach((v) -> {
            System.out.println(String.format("%d\t%s\t%f", v.getInteger("id"),v.getString("name"), v.getDouble("price")));
        });
    }

    @Test
    public void testOrExpression() throws HttpException, IOException, URISyntaxException {

        ClientDataQueryable q = testGetQueryable("Product");
        DataObjectArray result = q.where("category").equal("Laptops")
                .or("category").equal("Desktops")
                .orderByDescending("price")
                .take(5);
        result.forEach((v) -> {
            System.out.println(String.format("%d\t%s (%s)\t%f", v.getInteger("id"),v.getString("name"),
                    v.getString("category"), v.getDouble("price")));
        });
    }

    @Test
    public void testSelectCount() throws HttpException, IOException, URISyntaxException {

        ClientDataQueryable q = testGetQueryable("Person");
        Object result = q.select(FieldExpression.create("id").count().as("personCount")).value();
        System.out.println(String.format("Person Count=%d", (int)result));

    }

}
