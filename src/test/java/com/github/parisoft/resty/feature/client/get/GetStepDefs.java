package com.github.parisoft.resty.feature.client.get;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.http.entity.ContentType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.parisoft.resty.RESTy;
import com.github.parisoft.resty.response.Response;
import com.github.parisoft.resty.server.LocalServer;
import com.github.parisoft.resty.server.domain.Car;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetStepDefs {

    private Response actualResponse;
    private Car actualCar;
    private List<Car> actualCarList;

    @Before
    public void before() {
        LocalServer.start();
    }

    @When("^do a GET request to \"(.*?)\" for a Response instance from json$")
    public void do_a_GET_request_to_for_a_Response_instance_from_json(String path) throws Throwable {
        actualResponse = RESTy.request(LocalServer.getHost())
                .accept(ContentType.APPLICATION_JSON)
                .path(path)
                .client()
                .get();
    }

    @When("^do a GET request to \"(.*?)\" for a list of Car instances from json$")
    public void do_a_GET_request_to_for_a_list_of_Car_instances_from_json(String path) throws Throwable {
        actualCarList = RESTy.request(LocalServer.getHost())
                .accept(ContentType.APPLICATION_JSON)
                .path(path)
                .client()
                .get(new TypeReference<List<Car>>() {});
    }

    @When("^do a GET request to \"(.*?)\" for a Car instance from json$")
    public void do_a_GET_request_to_for_a_Car_instance_from_json(String path) throws Throwable {
        actualCar = RESTy.request(LocalServer.getHost())
                .accept(ContentType.APPLICATION_JSON)
                .path(path)
                .client()
                .get(Car.class);
    }

    @When("^do a GET request to \"(.*?)\" for a Response instance from xml$")
    public void do_a_GET_request_to_for_a_Response_instance_from_xml(String path) throws Throwable {
        actualResponse = RESTy.request(LocalServer.getHost())
                .accept(ContentType.APPLICATION_XML)
                .path(path)
                .client()
                .get();
    }

    @When("^do a GET request to \"(.*?)\" for a list of Car instances from xml$")
    public void do_a_GET_request_to_for_a_list_of_Car_instances_from_xml(String path) throws Throwable {
        actualCarList = RESTy.request(LocalServer.getHost())
                .accept(ContentType.APPLICATION_XML)
                .path(path)
                .client()
                .get(new TypeReference<List<Car>>() {});
    }

    @When("^do a GET request to \"(.*?)\" for a Car instance from xml$")
    public void do_a_GET_request_to_for_a_Car_instance_from_xml(String path) throws Throwable {
        actualCar = RESTy.request(LocalServer.getHost())
                .accept(ContentType.APPLICATION_XML)
                .path(path)
                .client()
                .get(Car.class);
    }

    @Then("^the status code is (\\d+)$")
    public void the_status_code_is(int expectedStatusCode) throws Throwable {
        assertEquals(expectedStatusCode, actualResponse.getStatusCode());
    }

    @Then("^the content type is \"(.*?)\"$")
    public void the_content_type_is(String expectedContentType) throws Throwable {
        assertEquals(expectedContentType, actualResponse.getContentType().getMimeType());
    }

    @Then("^the car list size is (\\d+)$")
    public void the_car_list_size_is(int expectedSize) throws Throwable {
        assertEquals(expectedSize, actualCarList.size());
    }

    @Then("^the (\\d+)st car is a \"(.*?)\"$")
    public void the_st_car_is_a(int index, String carname) throws Throwable {
        actualCar = actualCarList.get(index - 1);
        the_car_is_a(carname);
    }

    @Then("^the (\\d+)nd car is a \"(.*?)\"$")
    public void the_nd_car_is_a(int index, String carname) throws Throwable {
        actualCar = actualCarList.get(index - 1);
        the_car_is_a(carname);
    }

    @Then("^the car is a \"(.*?)\"$")
    public void the_car_is_a(String expectedCarName) throws Throwable {
        assertEquals(expectedCarName, actualCar.getName());
    }
}
