package org.cirrus.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cirrus.infrastructure.handler.util.JacksonMapper;
import org.cirrus.infrastructure.handler.util.Logger;
import org.cirrus.infrastructure.handler.util.Mapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UploadCodeModelTests extends ApiModelTests<UploadCodeRequest, UploadCodeResponse> {

  @Mock(stubOnly = true)
  private static Logger logger;

  protected UploadCodeModelTests() {
    super(mapper(), UploadCodeRequest.class, UploadCodeResponse.class);
  }

  private static Mapper mapper() {
    return new JacksonMapper(new ObjectMapper(), logger);
  }

  protected UploadCodeRequest request() {
    return UploadCodeRequest.create();
  }

  protected UploadCodeResponse response() {
    return UploadCodeResponse.builder()
        .codeId(HandlerTests.CODE_ID)
        .uploadUrl(HandlerTests.UPLOAD_URL)
        .build();
  }
}
