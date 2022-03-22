import com.github.fridmor.DataValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataValidatorTest {

    @Test
    public void inputDataTest() {
        assertTrue(DataValidator.inputDataIsValid("28.02.2022;86,5032;Евро"));
        assertFalse(DataValidator.inputDataIsValid("31.02.2022;86,5032;Евро"));
    }

    @Test
    public void inputDateTest() {
        assertTrue(DataValidator.dateIsValid("28.02.2022"));
        assertFalse(DataValidator.dateIsValid("31.02.2022"));
    }
}
