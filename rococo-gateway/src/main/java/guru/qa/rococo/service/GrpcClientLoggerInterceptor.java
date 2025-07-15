package guru.qa.rococo.service;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GrpcClientLoggerInterceptor implements ClientInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(GrpcClientLoggerInterceptor.class);
    private static final int MAX_LOG_LENGTH = 2000;

    private String truncateMessage(Object message) {
        if (message == null) {
            return "null";
        }
        String messageStr = message.toString();
        if (messageStr.length() <= MAX_LOG_LENGTH) {
            return messageStr;
        }

        return truncateFields(messageStr);
    }

    private String truncateFields(String messageStr) {
        StringBuilder result = new StringBuilder();
        String[] lines = messageStr.split("\n");

        for (String line : lines) {
            if (line.contains(": ")) {
                int colonIndex = line.indexOf(": ");
                String fieldName = line.substring(0, colonIndex + 2); // Include ": "
                String fieldValue = line.substring(colonIndex + 2);

                if (fieldValue.length() > MAX_LOG_LENGTH) {
                    String truncatedValue = fieldValue.substring(0, MAX_LOG_LENGTH) +
                        "... [TRUNCATED - original length: " + fieldValue.length() + "]";
                    result.append(fieldName).append(truncatedValue);
                } else {
                    result.append(line);
                }
            } else {
                result.append(line);
            }
            result.append("\n");
        }

        // Remove the last newline if it was added
        if (!result.isEmpty() && result.charAt(result.length() - 1) == '\n') {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        String methodName = method.getFullMethodName();
        logger.info("gRPC Client Call Started: [{}]", methodName);

        return new ForwardingClientCall.SimpleForwardingClientCall<>(
                next.newCall(method, callOptions)) {

            @Override
            public void sendMessage(ReqT message) {
                logger.debug("gRPC Client Request [{}] - Type: {}, Content: {}",
                        methodName, message.getClass().getSimpleName(), truncateMessage(message));
                super.sendMessage(message);
            }

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                logger.debug("gRPC Client Headers [{}]: {}", methodName, headers);
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<>(responseListener) {
                    @Override
                    public void onMessage(RespT message) {
                        logger.debug("gRPC Client Response [{}] - Type: {}, Content: {}",
                                methodName, message.getClass().getSimpleName(), truncateMessage(message));
                        super.onMessage(message);
                    }

                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        if (status.isOk()) {
                            logger.info("gRPC Client Call Completed: [{}] - SUCCESS", methodName);
                        } else {
                            logger.error("gRPC Client Call Failed: [{}] - Status: {} - {}",
                                    methodName, status.getCode(), status.getDescription());
                        }
                        super.onClose(status, trailers);
                    }

                    @Override
                    public void onReady() {
                        logger.debug("gRPC Client Ready: [{}]", methodName);
                        super.onReady();
                    }
                }, headers);
            }

            @Override
            public void cancel(String message, Throwable cause) {
                logger.warn("gRPC Client Call Cancelled: [{}] - {}", methodName, message);
                super.cancel(message, cause);
            }
        };
    }
}
