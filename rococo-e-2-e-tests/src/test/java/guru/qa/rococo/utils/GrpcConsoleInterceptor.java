package guru.qa.rococo.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

public class GrpcConsoleInterceptor implements io.grpc.ClientInterceptor {

    private static final JsonFormat.Printer printer = JsonFormat.printer();
    private static final int MAX_FIELD_LENGTH = 100;

    private String truncateMessage(MessageOrBuilder message) {
        if (message == null) {
            return "null";
        }
        try {
            String jsonStr = printer.print(message);
            return truncateJsonFields(jsonStr);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    private String truncateJsonFields(String jsonStr) {
        StringBuilder result = new StringBuilder();
        String[] lines = jsonStr.split("\n");

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.contains(": \"") && !trimmedLine.endsWith("\": \"")) {
                // Find field name and value
                int colonIndex = trimmedLine.indexOf(": \"");
                String beforeColon = trimmedLine.substring(0, colonIndex + 3); // Include ": "
                String afterColon = trimmedLine.substring(colonIndex + 3);

                // Find the end of the string value
                int endQuoteIndex = afterColon.lastIndexOf("\"");
                if (endQuoteIndex > 0) {
                    String fieldValue = afterColon.substring(0, endQuoteIndex);
                    String afterValue = afterColon.substring(endQuoteIndex);

                    if (fieldValue.length() > MAX_FIELD_LENGTH) {
                        String truncatedValue = fieldValue.substring(0, MAX_FIELD_LENGTH) +
                            "... [TRUNCATED - original length: " + fieldValue.length() + "]";
                        result.append(line, 0, line.indexOf(trimmedLine))
                              .append(beforeColon)
                              .append(truncatedValue)
                              .append(afterValue);
                    } else {
                        result.append(line);
                    }
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor,
                                                               CallOptions callOptions,
                                                               Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall(
                channel.newCall(methodDescriptor, callOptions)
        ) {

            @Override
            public void sendMessage(Object message) {
                if (message == null) {
                    System.out.println("REQUEST: null");
                } else {
                    System.out.println("REQUEST: " + truncateMessage((MessageOrBuilder) message));
                }
                super.sendMessage(message);
            }

            @Override
            public void start(Listener responseListener, Metadata headers) {
                ForwardingClientCallListener<Object> clientCallListener = new ForwardingClientCallListener<>() {

                    @Override
                    public void onMessage(Object message) {
                        if (message == null) {
                            System.out.println("RESPONSE: null");
                        } else {
                            System.out.println("RESPONSE: " + truncateMessage((MessageOrBuilder) message));
                        }
                        super.onMessage(message);
                    }

                    @Override
                    protected Listener<Object> delegate() {
                        return responseListener;
                    }
                };
                super.start(clientCallListener, headers);
            }
        };
    }
}
