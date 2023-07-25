package org.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.client.Args;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SetCommandTest {

    @Test
    void testingTwoStringsValueAndKeySavedInMap() {
        Args<String, String> args = buildInputArgs();
        HashMap<String, String> expectedContainer = constructingExpectedMap();
        HashMap<String, String> actualContainer = new HashMap<>();
        BackResponse backResponse = getBackResponseExpected();
        JsonObject jsonObjectForJsonData = new Gson().fromJson(new Gson().toJson(args), JsonObject.class);
        SetCommand setCommand = new SetCommand(args, actualContainer, jsonObjectForJsonData);
        assertEquals(backResponse, setCommand.execute());
        assertEquals(expectedContainer, setCommand.getContainer());
    }

    private static BackResponse getBackResponseExpected() {
        BackResponse backResponse = new BackResponse();
        backResponse.setResponse("OK");
        return backResponse;
    }

    private static HashMap<String, String> constructingExpectedMap() {
        HashMap<String, String> expectedContainer = new HashMap<>();
        expectedContainer.put("person", "4");
        return expectedContainer;
    }

    private static Args<String, String> buildInputArgs() {
        Args<String, String> args = new Args<>();
        args.setType("set");
        args.setKey("person");
        args.setValue("4");
        return args;
    }

    @Test
    void testingStringKeyAndJsonObjectValue() {
        Args<String, JsonObject> args = new Args<>();
        Scanner sc = null;
        StringBuilder builder = new StringBuilder();
        try {
            sc = new Scanner(new FileReader("/Users/smnatsakanyan/Desktop/hard_project/src/main/java/org/example/Stage7/client/data/secondSetFile.json"));
            while (sc.hasNext()) {
                builder.append(sc.nextLine()).append("\n");
            }
            args = new Gson().fromJson(String.valueOf(builder), Args.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, String> expectedContainer = new HashMap<>();
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(builder), JsonObject.class);
        expectedContainer.put(jsonObject.get("key").getAsString(), new Gson().toJson(jsonObject.get("value")));
        HashMap<String, String> actualContainer = new HashMap<>();
        BackResponse backResponseExpected = getBackResponseExpected();
        SetCommand setCommand = new SetCommand(args, actualContainer, jsonObject);
        BackResponse backResponseActual = setCommand.execute();
        assertEquals(backResponseExpected, backResponseActual);
        assertEquals(expectedContainer, actualContainer);
    }

    @Test
    void testingStringArrayKeyAndStringValue() {
        Args<ArrayList<String>, String> args = new Args<>();
        String fileContentUpdate = null;
        String fileContentSet = null;
        HashMap<String, String> expectedContainer = new HashMap<>();
        HashMap<String, String> actualContainer = new HashMap<>();
        BackResponse backResponseExpected = getBackResponseExpected();
        try {
            fileContentUpdate = new String(Files.readAllBytes(Paths.get("/Users/smnatsakanyan/Desktop/hard_project/src/main/java/org/example/Stage7/client/data/updateFile.json")));
            fileContentSet = new String(Files.readAllBytes(Paths.get("/Users/smnatsakanyan/Desktop/hard_project/src/main/java/org/example/Stage7/client/data/secondSetFile.json")));
            args = new Gson().fromJson(fileContentUpdate, Args.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject jsonObjectBeforeUpdate = new Gson().fromJson(fileContentSet, JsonObject.class);
        JsonObject jsonObjectBeforeUpdateValue = (JsonObject) jsonObjectBeforeUpdate.get("value");
        System.out.println(jsonObjectBeforeUpdate);
        actualContainer.put(jsonObjectBeforeUpdate.get("key").getAsString(), new Gson().toJson(jsonObjectBeforeUpdateValue));
        JsonObject partitionJsonObject = jsonObjectBeforeUpdateValue;
        String[] keys = {"persona","rocket","launches"};
        for (int i = 1; i < 2; i++) {
            partitionJsonObject = (JsonObject) partitionJsonObject.get(keys[i]);
        }
        partitionJsonObject.addProperty(keys[keys.length - 1], "88");
        expectedContainer.put(keys[0], jsonObjectBeforeUpdateValue.toString());
        JsonObject jsonObjectAfterUpdate = new Gson().fromJson(fileContentUpdate, JsonObject.class);
        SetCommand setCommand = new SetCommand(args, actualContainer, jsonObjectAfterUpdate);
        BackResponse backResponseActual = setCommand.execute();
        assertEquals(backResponseExpected, backResponseActual);
        assertEquals(expectedContainer,actualContainer);
    }

    @Test
    void testingForNonExistentArrayKeyAndJsonObject() {
        Args<ArrayList<String>, JsonObject> args = new Args<>();
        String fileContent = null;
        HashMap<String, String> expectedContainer = new HashMap<>();
        HashMap<String, String> actualContainer = new HashMap<>();
        BackResponse backResponseExpected = getBackResponseExpected();
        try {
            fileContent = new String(Files.readAllBytes(Paths.get("/Users/smnatsakanyan/Desktop/hard_project/src/main/java/org/example/Stage7/client/data/notCreatedBeforeData.json")));
            args = new Gson().fromJson(fileContent, Args.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedContainer.put("person1","{\"inside1\":{\"inside2\":{\"id1\":12,\"id2\":14}}}");
        JsonObject jsonObject = new Gson().fromJson(fileContent, JsonObject.class);
        SetCommand setCommand = new SetCommand(args, actualContainer, jsonObject);
        BackResponse backResponseActual = setCommand.execute();
        assertEquals(backResponseExpected,backResponseActual);
        assertEquals(expectedContainer,actualContainer);
    }

}
