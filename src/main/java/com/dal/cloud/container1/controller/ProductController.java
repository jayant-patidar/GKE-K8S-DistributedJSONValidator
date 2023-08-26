package com.dal.cloud.container1.controller;

import com.dal.cloud.container1.model.*;
import com.dal.cloud.container1.model.Error;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@RestController
public class ProductController {


    @PostMapping("/store-file")
    public Object StoreFile(@RequestBody FileInput input) {
        if (input.getFile() == null) {
            Error error = new Error();
            error.setFile(null);
            error.setError("Invalid JSON input.");
            return error;
        }

        try {
            String storePath = "/JAYA_PV_dir/FileStore/";
            File file = new File(storePath + input.getFile());
            System.out.println(storePath + input.getFile());

            if (file.exists()) {
                file.delete();
            }
            if (file.createNewFile()) {

                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter writer = new BufferedWriter(fileWriter);
                writer.write(input.getData());
                writer.close();

                Message message = new Message();
                message.setFile(input.getFile());
                message.setMessage("Success.");
                return message;
            } else {
                Error error = new Error();
                error.setFile(input.getFile());
                error.setError("Error while storing the file to the storage.");
                return error;
            }
        } catch (IOException e) {
            Error error = new Error();
            error.setFile(input.getFile());
            error.setError("Error while storing the file to the storage.");
            return error;
        }
    }



    @PostMapping("/calculate")
    public Object calculate(@RequestBody Input input) {
        String fileName = input.getFile();
        String productName = input.getProduct();
        System.out.println(productName);
        String storePath = "/JAYA_PV_dir/FileStore/";
        if (fileName == null) {

            Error error = new Error();
            error.setFile(fileName);
            error.setError("Invalid JSON input.");
            return error;
        } else {
            File file = new File(storePath + fileName);
            boolean fileExist = file.exists();

            if (fileExist) {
                Output output = new Output();
                output.setFile(fileName);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Input> requestEntity = new HttpEntity<>(input, headers);

                RestTemplate restTemplate = new RestTemplate();
                String apiUrl = "http://container2:6001/calculate";
                ResponseEntity<Object> responseEntity = restTemplate.exchange(
                        "http://container2:6001/calculate",
                        HttpMethod.POST,
                        requestEntity,
                        Object.class
                );

                Object o = responseEntity.getBody();
                return o;
            } else {
                Error error = new Error();
                error.setFile(fileName);
                error.setError("File not found.");
                return error;
            }
        }
    }

}
