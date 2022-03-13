package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cirrus.infrastructure.handler.util.JacksonMapper;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    return PublishCodeRequest.builder()
        .codeId(HandlerTests.CODE_ID)
        .runtime(HandlerTests.RUNTIME)
        .build();
  }

  protected PublishCodeResponse response() {
    return PublishCodeResponse.of(HandlerTests.ARTIFACT_ID);
  }
}
