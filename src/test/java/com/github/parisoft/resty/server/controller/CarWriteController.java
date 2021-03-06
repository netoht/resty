package com.github.parisoft.resty.server.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import javax.ws.rs.core.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarWriteController {

    private static final String APPLICATION_JSON = MediaType.APPLICATION_JSON + "; charset=utf-8";
    private static final String APPLICATION_XML = MediaType.APPLICATION_XML + "; charset=utf-8";

    @RequestMapping(method={POST, PUT, PATCH}, value="/car", consumes=APPLICATION_JSON, produces=APPLICATION_JSON)
    public ResponseEntity<String> saveCars(@RequestBody String cars) throws Exception {
        return ResponseEntity.status(CREATED).body(cars);
    }

    @RequestMapping(method={POST, PUT, PATCH}, value="/car", consumes=APPLICATION_XML, produces=APPLICATION_XML)
    public ResponseEntity<String> saveCarsInXml(@RequestBody String cars) throws Exception {
        return ResponseEntity.status(CREATED).body(cars);
    }

    @RequestMapping(method={POST, PUT, PATCH}, value="/car/{name}", consumes=APPLICATION_JSON, produces=APPLICATION_JSON)
    public ResponseEntity<String> saveCar(@PathVariable("name") String name, @RequestBody String car) throws Exception {
        return ResponseEntity.status(CREATED).body(car);
    }

    @RequestMapping(method={POST, PUT, PATCH}, value="/car/{name}", consumes=APPLICATION_XML, produces=APPLICATION_XML)
    public ResponseEntity<String> saveCarInXml(@PathVariable("name") String name, @RequestBody String car) throws Exception {
        return ResponseEntity.status(CREATED).body(car);
    }

    @RequestMapping(value="/err")
    public void f() {
        throw new RuntimeException("fake");
    }
}
