package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cirrus.infrastructure.handler.util.JacksonMapper;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateNodeModelTests extends ApiModelTests<CreateNodeRequest, CreateNodeResponse> {

  @Mock(stubOnly = true)
  private static Logger logger;

  protected CreateNodeModelTests() {
    super(mapper(), CreateNodeRequest.class, CreateNodeResponse.class);
  }

  private static Mapper mapper() {
    return new JacksonMapper(new ObjectMapper(), logger);
  }

  protected CreateNodeRequest request() {
    return CreateNodeRequest.builder()
        .nodeId(HandlerTests.NODE_ID)
        .functionConfig(HandlerTests.functionConfig())
        .queueConfig(HandlerTests.queueConfig())
        .build();
  }

  protected CreateNodeResponse response() {
    return CreateNodeResponse.builder()
        .nodeId(HandlerTests.NODE_ID)
        .functionId(HandlerTests.FUNCTION_ID)
        .queueId(HandlerTests.QUEUE_ID)
        .build();
  }
}
