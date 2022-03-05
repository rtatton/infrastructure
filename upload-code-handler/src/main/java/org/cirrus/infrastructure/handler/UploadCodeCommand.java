package org.cirrus.infrastructure.handler;

import java.util.function.Supplier;
import javax.inject.Inject;
import org.cirrus.infrastructure.handler.util.Mapper;

final class UploadCodeCommand implements Command<UploadCodeRequest, UploadCodeResponse> {

  private final Supplier<String> urlSupplier;
  private final Mapper mapper;

  @Inject
  public UploadCodeCommand(Supplier<String> urlSupplier, Mapper mapper) {
    this.urlSupplier = urlSupplier;
    this.mapper = mapper;
  }

  @Override
  public UploadCodeResponse run(UploadCodeRequest request) {
    return UploadCodeResponse.of(urlSupplier.get());
  }

  @Override
  public String runFromString(String input) {
    UploadCodeRequest request = mapper.read(input, UploadCodeRequest.class);
    UploadCodeResponse response = run(request);
    return mapper.write(response);
  }
}
