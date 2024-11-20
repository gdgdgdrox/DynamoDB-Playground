package com.ddbpractice.dynamodb;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@RestController
@RequestMapping(value="/dynamodbclient/practice")
public class DynamodbController {

    @Autowired
    private DynamoDbClient client;
    
    @GetMapping("/createTable")
    public void createTable(){
        System.out.println("in create table");
        CreateTableRequest createTableRequest = CreateTableRequest.builder().
        attributeDefinitions(AttributeDefinition.builder().attributeName("name").attributeType(ScalarAttributeType.S).build())
        .keySchema(KeySchemaElement.builder().attributeName("name").keyType(KeyType.HASH).build())
        .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5l).writeCapacityUnits(5l).build())
        .tableName("animal")
        .build();

        client.createTable(createTableRequest);
        
    }

    @PostMapping("/addAnimal")
    public ResponseEntity<String> addAnimal(@RequestBody AnimalDTO animalDTO){
        System.out.println("in add animal");
        System.out.println("received: " + animalDTO.toString());

        // construct the animal object to add
        HashMap<String,AttributeValue> animal = new HashMap<>();
        animal.put("name",AttributeValue.builder().s(animalDTO.getName()).build());
        animal.put("sound",AttributeValue.builder().s(animalDTO.getSound()).build());
        animal.put("temperament", AttributeValue.builder().s(animalDTO.getTemperament() != null ? animalDTO.getTemperament() : "").build());

        PutItemRequest putItemRequest = PutItemRequest.builder().tableName("animal").item(animal).build();
        PutItemResponse putItemResponse = client.putItem(putItemRequest);
        if (putItemResponse.sdkHttpResponse().isSuccessful()){
            System.out.println("success");
            return ResponseEntity.status(HttpStatus.CREATED).body("created");
        }

        return null;
    }

    @GetMapping("/getAnimal/{animalName}")
    public ResponseEntity<AnimalDTO> getAnimal(@PathVariable String animalName){
        System.out.println("in get animal");
        System.out.println("path variable: " + animalName);

        HashMap<String,AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("name",AttributeValue.builder().s(animalName).build());

        GetItemRequest getItemRequest = GetItemRequest.builder().tableName("animal").key(keyToGet).build();
        GetItemResponse getItemResponse = client.getItem(getItemRequest);
        if (!getItemResponse.hasItem()){
            String message = animalName + " not found.";
            System.out.println(message);
            return ResponseEntity.ok().body(null);
        }
        Map<String,AttributeValue> item = getItemResponse.item();
        AnimalDTO animal = new AnimalDTO();
        animal.setName(item.get("name").s());
        animal.setSound(item.get("sound").s());
        animal.setTemperament(item.get("temperament").s());
        System.out.println(animal);
        return ResponseEntity.ok().body(animal);
    }
}
