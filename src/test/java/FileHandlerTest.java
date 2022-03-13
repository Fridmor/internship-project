import com.github.fridmor.FileHandler;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class FileHandlerTest {

    @Test
    public void readFileTest() {
        File file = new File("src/main/resources/EUR_F01_02_2002_T01_02_2022.csv");
        List<List<String>> data = FileHandler.readFile(file, 7);
        assertFalse(data.isEmpty());
    }

    @Test
    public void writeFileTest() {
        File oldFile = new File("src/main/resources/USD_F01_02_2002_T01_02_2022.csv");
        List<List<String>> oldData = FileHandler.readFile(oldFile, 7);
        File newFile = new File("src/main/resources/temp.csv");
        FileHandler.writeFile(newFile, oldData, null);
        List<List<String>> newData = FileHandler.readFile(oldFile, 7);
        assertFalse(newData.isEmpty());
    }
}
