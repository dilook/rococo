package guru.qa.rococo.service;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.stereotype.Component;

@Component
@GlobalServerInterceptor
public class GrpcLoggerInterceptor implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(GrpcLoggerInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {

        String methodName = call.getMethodDescriptor().getFullMethodName();
        logger.info("gRPC Server Call Started: [{}]", methodName);
        logger.debug("gRPC Headers [{}]: {}", methodName, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(
                next.startCall(call, headers)) {
            @Override
            public void onMessage(ReqT message) {
                logger.debug("gRPC Request [{}] - Type: {}, Content:\n {}",
                        methodName, message.getClass().getSimpleName(), message);
                super.onMessage(message);
            }

            @Override
            public void onHalfClose() {
                logger.debug("gRPC Half Close: [{}]", methodName);
                super.onHalfClose();
            }

            @Override
            public void onCancel() {
                logger.warn("gRPC Server Call Cancelled: [{}]", methodName);
                super.onCancel();
            }
        };
    }
}
