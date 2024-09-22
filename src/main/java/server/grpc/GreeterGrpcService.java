package server.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.GreetProto;
import server.GreetProto.HelloReply;
import server.GreetProto.HelloRequest;
import server.GreeterGrpcServiceGrpc.GreeterGrpcServiceImplBase;
import server.security.GrpcSecurityConfig.RoleAuth;

@GrpcService
public class GreeterGrpcService extends GreeterGrpcServiceImplBase {

	@Override
	@RoleAuth({"ROLE_ADMIN", "ROLE_USER"})
	public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {

		String message = request.getName();

		GreetProto.HelloReply reply = 
				GreetProto
				.HelloReply
				.newBuilder()
				.setMessage(message)
				.build();

		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}
}
