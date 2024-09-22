package server.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.GreetProto;
import server.GreetProto.HelloReply;
import server.GreetProto.HelloRequest;
import server.GreeterGrpc.GreeterImplBase;

// Estandar: objectGrpcServices

@GrpcService
public class GreeterService extends GreeterImplBase {

	@Override
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
