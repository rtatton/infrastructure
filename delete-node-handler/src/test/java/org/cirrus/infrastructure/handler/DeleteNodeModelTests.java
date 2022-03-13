package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cirrus.infrastructure.handler.util.JacksonMapper;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteNodeModelTests extends ApiModelTests<DeleteNodeRequest, DeleteNodeResponse> {

  @Mock(stubOnly = true)
  private static Logger logger;

  protected DeleteNodeModelTests() {
    super(mapper(), DeleteNodeRequest.class, DeleteNodeResponse.class);
  }

  private static Mapper mapper() {
    return new JacksonMapper(new ObjectMapper(), logger);
  }

  protected DeleteNodeRequest request() {
    return DeleteNodeRequest.of(HandlerTests.NODE_ID);
  }

  protected DeleteNodeResponse response() {
    return DeleteNodeResponse.create();
  }
}
