package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.cirrus.infrastructure.handler.util.JacksonMapper;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.lambda.model.Runtime;

@ExtendWith(MockitoExtension.class)
class PublishCodeModelTests extends ApiModelTests<PublishCodeRequest, PublishCodeResponse> {

  @Mock(stubOnly = true)
  private static Logger logger;

  protected PublishCodeModelTests() {
    super(mapper(), PublishCodeRequest.class, PublishCodeResponse.class);
  }

  private static Mapper mapper() {
    return new JacksonMapper(new ObjectMapper(), logger);
  }

  protected PublishCodeRequest request() {
    List<Runtime> runtimes = List.copyOf(Runtime.knownValues());
    int index = ThreadLocalRandom.current().nextInt(runtimes.size());
    return PublishCodeRequest.builder()
        .codeId(HandlerTests.CODE_ID)
        .runtime(runtimes.get(index).toString())
        .build();
  }

  protected PublishCodeResponse response() {
    return PublishCodeResponse.of(HandlerTests.ARTIFACT_ID);
  }
}
