package server.grpc;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.GreetProto;
import server.GreetProto.HelloReply;
import server.GreetProto.HelloRequest;
import server.GreetProto.UserGrpc;
import server.GreetProto.UserGrpcById;
import server.GreeterGrpc.GreeterImplBase;
import server.services.IUserService;

@GrpcService
public class GreeterService extends GreeterImplBase {

	@Autowired
	private IUserService services;

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
	
	@Override
	public void getUserGrpc(UserGrpcById request, StreamObserver<UserGrpc> responseObserver) {

		var response = services.findById(request.getId());

		UserGrpc user = 
				UserGrpc
				.newBuilder()
				.setId(response.getId())
				.setName(response.getName())
				.build();

		responseObserver.onNext(user);
		responseObserver.onCompleted();
	}
}
