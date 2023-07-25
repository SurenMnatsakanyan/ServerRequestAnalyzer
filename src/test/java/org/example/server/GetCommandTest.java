package org.example.server;

import org.example.client.Args;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetCommandTest {

    @Test
    public void redundantBracketWillBeRemovedTest() {
        Args<String, ?> args = new Args<>();
        args.setType("get");
        args.setKey("person");
        HashMap<String, String> container = new HashMap<>();
        container.put("person", "5");
        Command getCommand = new GetCommand(args, container);
        BackResponse response = getCommand.execute();
        BackResponse expectedResponse = new BackResponse();
        expectedResponse.setResponse("OK");
        expectedResponse.setValue("5");
        assertEquals(expectedResponse, response);
    }

}
