package it.polito.ai.lab2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/*If @Transaction => doesn't save to DB so tests fail! Don't know how to solve, so the DB must not have those entry, and it won't be clean */
@SpringBootTest
class SprangularBackendTests {

  @Test
  void contextLoads() {
    }
}
