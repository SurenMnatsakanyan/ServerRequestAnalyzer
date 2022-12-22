import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.Stage7.client.Args;
import org.example.Stage7.server.BackResponse;
import org.example.Stage7.server.DeleteCommand;
import org.example.Stage7.server.SetCommand;
import org.junit.jupiter.api.Test;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteCommandTest {


    @Test
    void DeletingStringKeyAndJsonObjectValue() {
        Args<String, JsonObject> args = new Args<>();
        Scanner sc = null;
        StringBuilder builder = new StringBuilder();
        HashMap<String, String> expectedContainer = new HashMap<>();
        HashMap<String, String> actualContainer = new HashMap<>();
        BackResponse backResponseExpected = new BackResponse();
        backResponseExpected.setResponse("OK");
        String fileContent;
        try {
            sc = new Scanner(new FileReader("/Users/smnatsakanyan/Desktop/hard_project/src/main/java/org/example/Stage7/client/data/secondSetFile.json"));
            while (sc.hasNext()) {
                builder.append(sc.nextLine()).append("\n");
            }
            fileContent = new String(Files.readAllBytes(Paths.get("/Users/smnatsakanyan/Desktop/hard_project/src/main/java/org/example/Stage7/client/data/deleteFile.json")));
            args = new Gson().fromJson(fileContent,Args.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedContainer.put("persona","{\"name\":\"Elon Musk\",\"car\":{\"model\":\"Tesla Roadster\"},\"rocket\":{\"name\":\"Falcon 9\",\"launches\":\"87\"}}");
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(builder), JsonObject.class);
        actualContainer.put(jsonObject.get("key").getAsString(), new Gson().toJson(jsonObject.get("value")));
        DeleteCommand deleteCommand = new DeleteCommand(args,actualContainer);
        BackResponse backResponseActual = deleteCommand.execute();
        assertEquals(backResponseExpected, backResponseActual);
        assertEquals(expectedContainer, actualContainer);
    }
}
