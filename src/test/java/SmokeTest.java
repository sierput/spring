
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pl.tieto.mat.HomeController;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmokeTest.class)
public class SmokeTest {


	@Test
	public void contexLoads() throws Exception {
	//	assertThat(controller).isNotNull();
	}

}
