package server.grpc;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.UserGrpcServiceGrpc.UserGrpcServiceImplBase;
import server.UserProto.UserGrpc;
import server.UserProto.RequestId;
import server.services.IUserService;

@GrpcService
public class UserGrpcService extends UserGrpcServiceImplBase {
	
	@Autowired
	private IUserService services;
	
	@Override
	public void getUserGrpc(RequestId request, StreamObserver<UserGrpc> responseObserver) {

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
